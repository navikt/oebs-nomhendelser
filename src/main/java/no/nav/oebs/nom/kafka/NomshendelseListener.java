package no.nav.oebs.nom.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.nom.mdc.MdcOperations;
import no.nav.oebs.nom.db.repository.LoggRepository;
import no.nav.oebs.nom.exception.HendelseBehandlingException;
import no.nav.oebs.nom.kafka.model.NomshendelseDto;
import no.nav.oebs.nom.service.NomshendelseService;

/**
 * Kafka-listener som mottar nomshendelser fra topicen.
 */
@Slf4j
@Component
public class NomshendelseListener extends BaseHendelseListener {

	NomshendelseService nomshendelseService;

	public NomshendelseListener(LoggRepository loggRepository, NomshendelseService nomshendelseService) {
		super(loggRepository);
		this.nomshendelseService = nomshendelseService;
	}

	/**
	 * Listener som mottar hendelsen som et JSON-objekt på stringformat. Fødselsnr er nøkkelverdien til hendelsen.
	 * 
	 * @param hendelseAsJson
	 *            mottatt hendelse på JSON-format.
	 * @param consumerRecord
	 *            inneholder metadata om m.a. topic, partition og offset, i tillegg til selve hendelsen.
	 */
	@KafkaListener(topics = { "${app.kafka.nomshendelse.topic}" }, //
			clientIdPrefix = "${spring.kafka.client-id}-nomshendelse", //
			containerFactory = "kafkaNomshendelseListenerContainerFactory", //
			autoStartup = "${app.kafka.nomshendelse.auto-startup:true}")
	public void listen(@Payload String hendelseAsJson, ConsumerRecord<?, ?> consumerRecord) {
		long startTime = System.currentTimeMillis();
		String korrelasjonId = generateAndSetCorrelationId();
		int status = STATUS_OK;
		Exception exception = null;
		//boolean acceptedByFilter = false;

		try {
			NomshendelseDto nomshendelseDto = createNomshendelseDto(hendelseAsJson, consumerRecord);

				nomshendelseService.behandleHendelse(nomshendelseDto);

		} catch (HendelseBehandlingException e) {
			status = STATUS_ERROR;
			exception = e;
		} catch (Exception e) {
			status = STATUS_ERROR;
			exception = e;

			log.error("Mottatt nomshendelse kan ikke behandles og må rulles tilbake på topic", e);
			throw e;
		} finally {
			logToLogg(korrelasjonId, status, startTime, hendelseAsJson, consumerRecord, exception);
			MdcOperations.remove(MdcOperations.MDC_CORRELATION_ID);
		}
	}

	private NomshendelseDto createNomshendelseDto(String hendelseAsJson, ConsumerRecord<?, ?> consumerRecord) {
		return NomshendelseDto.builder() //
				.hendelseId(createHendelseId(consumerRecord)) //
				.hendelseTimestamp(epochToLocalDateTime(consumerRecord.timestamp())) //
				.fodselsnr(getValueAsString(consumerRecord.key())) //
				.hendelseAsJson(hendelseAsJson) //
				// .fodselsnr((consumerRecord.offset())
				.build();
	}

	/**
	 * Returnerer en hendelse ID for meldingen. Nomshendelser har ingen unik id i selve meldingen. Det opprettes derfor en
	 * unik id basert på topic-, partisjon- og offset-verdiene mottatt fra Kafka.
	 */
	private String createHendelseId(ConsumerRecord<?, ?> consumerRecord) {
		return String.join("-", consumerRecord.topic(), String.valueOf(consumerRecord.partition()),
				String.valueOf(consumerRecord.offset()));
	}

	private String getValueAsString(Object value) {
		return (value != null) ? value.toString() : null;
	}
}
