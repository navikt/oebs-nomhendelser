package no.nav.oebs.nom.service;

import java.util.List;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.nom.logging.LoggingUtils;
import no.nav.oebs.nom.mdc.MdcOperations;
import no.nav.oebs.nom.db.entity.NomsHendelse;
import no.nav.oebs.nom.db.repository.NomshendelseRepository;
import no.nav.oebs.nom.exception.HendelseBehandlingException;
import no.nav.oebs.nom.kafka.nomshendelse.model.*;

/**
 * Serviceklasse som behandler mottatte nomshendelser fra Kafka.
 */
@Slf4j
@Service
public class NomshendelseService extends NomshendelseServiceBase {

	NomshendelseRepository nomshendelseRepository;

	public NomshendelseService(ServiceConfig serviceConfig, NomshendelseRepository nomshendelseRepository,
			ObjectMapper objectMapper) {
		super(serviceConfig, objectMapper);
		this.nomshendelseRepository = nomshendelseRepository;
	}

	/**
	 * Behandler mottatt nomshendelse.
	 * <p>
	 * Status som lagres i nomshendelse-tabellen:
	 * <ul>
	 * <li>BEHANDLET - ved normal fullført behandling av hendelsen.</li>
	 * <li>RETRY - dersom det oppstår feil under behandling av hendelsen.</li>
	 * <li>DUPLIKAT - dersom samme hendelseId allerede er mottatt.</li>
	 * </ul>
	 * <p>
	 * Kaster HendelseBehandlingException dersom behandlingen feiler og hendelsen skal gå til retry. Øvrige exception som kastes
	 * skal gi tilbakerulling av hendelsen til topic.
	 *
	 * @param mottattHendelse
	 *            hendelsen som er mottatt fra topicen.
	 */
	@Transactional(noRollbackFor = HendelseBehandlingException.class)
	public void behandleHendelse(NomshendelseDto mottattHendelse) {

		// Persisterer først hendelsen. Formålet er å sjekke at databasen er tilgjengelig, ellers skal hendelsen rulles
		// tilbake på topicen. Må gjøres før try-catchen.
		NomsHendelse nomshendelse = createAndSaveNomsHendelseEntity(mottattHendelse);

		log.info(nomshendelse.getHendelseId());

		try {
			if (isNyHendelseDuplikat(nomshendelse)) {
				nomshendelse.setStatus(NomsHendelse.STATUS_DUPLIKAT);
			} else {
				addHendelseOebsToEntity(nomshendelse);

				nomshendelse.setStatus(NomsHendelse.STATUS_BEHANDLET);

			}
		} catch (Exception e) {
			nomshendelse.setStatus(NomsHendelse.STATUS_RETRY);
			nomshendelse.setRetryTeller(serviceConfig.getRetryMaxAttempts());
			nomshendelse.setRetryTidspunkt(getNextRetryTidspunkt(nomshendelse));
			nomshendelse.setFeilinformasjon(LoggingUtils.formatExceptionAsString(e));

			log.warn(String.format("Feilet under behandling av nomshendelse; id=%d, retrytidspunkt=%s, cause=%s",
					nomshendelse.getId(), nomshendelse.getRetryTidspunkt(), e.getMessage()), e);

			throw new HendelseBehandlingException(e);
		}
	}

	/**
	 * Oppretter og persisterer hendelsen. Returnerer entiteten som er persistert.
	 */
	private NomsHendelse createAndSaveNomsHendelseEntity(NomshendelseDto mottattHendelse) {
		NomsHendelse hendelse = NomsHendelse.builder() //
				.korrelasjonId(MdcOperations.get(MdcOperations.MDC_CORRELATION_ID)) //
				.status(NomsHendelse.STATUS_NY) //
				.hendelseId(mottattHendelse.getHendelseId()) //
				.hendelseFodselsnr(mottattHendelse.getFodselsnr()) //
				.hendelseOpprettet(mottattHendelse.getHendelseTimestamp()) //
				.hendelse(mottattHendelse.getHendelseAsJson()) //
				.build();

		return nomshendelseRepository.save(hendelse);
	}

	/**
	 * Sjekker om en ny nomshendelse er duplikat av tidligere mottatte hendelser. Sjekken gjøres på hendelse ID, samt at
	 * status ikke må være NY.
	 */
	private boolean isNyHendelseDuplikat(NomsHendelse nomsHendelse) {
		return !nomshendelseRepository
				.findByHendelseIdAndStatusNotIn(nomsHendelse.getHendelseId(), List.of(NomsHendelse.STATUS_NY))
				.isEmpty();
	}
}
