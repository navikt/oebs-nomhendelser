package no.nav.oebs.nom.kafka;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import no.nav.oebs.nom.db.entity.NomsLogg;
import no.nav.oebs.nom.logging.LoggingUtils;
import no.nav.oebs.nom.mdc.MdcOperations;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.nom.db.repository.NomsLoggRepository;

/**
 * Kafka-basislistener som inneholder basisfunksjonalitet for mottak av hendelser. Kafka-listeneren som leser aktuelle topics
 * skal arve denne klassen.
 */
@Slf4j
public class BaseHendelseListener {

	public static final int STATUS_OK = 900;
	public static final int STATUS_ERROR = 909;

	private NomsLoggRepository nomsLoggRepository;

	public BaseHendelseListener(NomsLoggRepository nomsLoggRepository) {
		this.nomsLoggRepository = nomsLoggRepository;
	}

	/**
	 * Returnerer en epoch millisekund-verdi som en LocalDateTime-verdi.
	 */
	protected LocalDateTime epochToLocalDateTime(long epoch) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneId.systemDefault());
	}

	/**
	 * Genererer korrelasjonId-verdien og setter den på MDC-konteksten. Generert verdi returneres.
	 */
	protected String generateAndSetCorrelationId() {
		String correlationId = MdcOperations.generateCorrelationId();
		MdcOperations.put(MdcOperations.MDC_CORRELATION_ID, correlationId);

		return correlationId;
	}

	/**
	 * Logger Kafka-meldingen med tilleggsinformasjon til kalloggen.
	 */
	protected void logToNomsLogg(String korrelasjonId, int status, long startTime, String message,
			ConsumerRecord<?, ?> consumerRecord, Exception exception) {

		long endTime = System.currentTimeMillis();

		NomsLogg nomsLoggEntry = NomsLogg.builder() //
				.korrelasjonId(korrelasjonId) //
				.tidspunkt(LocalDateTime.now()) //
				.type(NomsLogg.TYPE_KAFKA) //
				.kallRetning(NomsLogg.RETNING_INN) //
				.operation(consumerRecord.topic()) //
				.status(status) //
				.kalltid(endTime - startTime) //
				.kafkaPartition(consumerRecord.partition()) //
				.kafkaOffset(consumerRecord.offset()) //
				.kafkaTimestamp(epochToLocalDateTime(consumerRecord.timestamp())) //
				.kafkaTimestampType(consumerRecord.timestampType().name()) //
				.kafkaKey(getValueAsString(consumerRecord.key(), NomsLogg.MAX_KAFKA_KEY_LEN)) //
				.request(message) //
				.logginfo(LoggingUtils.formatExceptionAsString(exception)) //
				.build();

		log.debug(nomsLoggEntry.toString());

		saveNomsLogg(nomsLoggEntry);
	}

	private String getValueAsString(Object value, int maxLength) {
		String result = null;
		if (value != null) {
			String stringValue = value.toString();
			result = stringValue.substring(0, Math.min(stringValue.length(), maxLength));
		}
		return result;
	}

	private void saveNomsLogg(NomsLogg nomsLoggEntry) {
		try {
			nomsLoggRepository.save(nomsLoggEntry);
		} catch (Exception e) {
			log.error("Feil ved logging av kalloggdata til databasen; feilmelding=" + e.getMessage(), e);
		}
	}
}
