package no.nav.oebs.nom.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.nom.db.repository.LoggRepository;
import no.nav.oebs.nom.exception.HendelseBehandlingException;
import no.nav.oebs.nom.kafka.model.LivshendelseDto;
import no.nav.oebs.nom.mdc.MdcOperations;
import no.nav.oebs.nom.service.LivshendelseService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.person.pdl.leesah.Personhendelse;

/**
 * Kafka-listener som mottar livshendelser fra topicen.
 */
@Slf4j
@Component
public class LivshendelseListener extends BaseHendelseListener {


	@Value("${app.kafka.debug-mode}")
	private boolean isDebugMode;

	private final LivshendelseService livshendelseService;

	public LivshendelseListener(LoggRepository pdlLoggRepository,
                                LivshendelseService livshendelseService) {

		super(pdlLoggRepository);
		this.livshendelseService = livshendelseService;
	}

	/**
	 * Listener som mottar hendelsen som et Avro Personhendelse-objekt. Dette mappes om til et internt LivshendelseDto-objekt.
	 * 
	 * @param personhendelse
	 *            mottatt hendelse som et Avro Personhendelse-objekt.
	 * @param consumerRecord
	 *            inneholder metadata om m.a. topic, partition og offset, i tillegg til selve hendelsen.
	 */
	@KafkaListener(topics = { "${app.kafka.livshendelse.topic}" }, //
			clientIdPrefix = "${spring.kafka.client-id}", //
			containerFactory = "kafkaLivshendelseListenerContainerFactory", //
			autoStartup = "${app.kafka.livshendelse.auto-startup:true}")
	public void listen(@Payload Personhendelse personhendelse, ConsumerRecord<?, ?> consumerRecord) {
		long startTime = System.currentTimeMillis();
		String korrelasjonId = generateAndSetCorrelationId();
		int status = STATUS_OK;
		Exception exception = null;

		try {
			LivshendelseDto livshendelseDto = LivshendelseDto.map(personhendelse);
			livshendelseService.behandleHendelse(livshendelseDto);

		} catch (HendelseBehandlingException e) {
			status = STATUS_ERROR;
			exception = e;
		} catch (Exception e) {
			status = STATUS_ERROR;
			exception = e;

			log.error("Mottatt livshendelse kan ikke behandles og må rulles tilbake på topic", e);
			throw e;
		} finally {
			if (isDebugMode) {
				logToLogg(korrelasjonId, status, startTime, personhendelse.toString(), consumerRecord, exception);
			}

			MdcOperations.remove(MdcOperations.MDC_CORRELATION_ID);
		}
	}
}
