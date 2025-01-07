package no.nav.oebs.nom.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.nom.logging.LoggingUtils;
import no.nav.oebs.nom.mdc.MdcOperations;
import no.nav.oebs.nom.db.entity.NomsHendelse;
//import no.nav.oebs.nom.db.repository.NomshendelseFacadeRepository;
import no.nav.oebs.nom.db.repository.NomshendelseRepository;
import no.nav.oebs.nom.exception.HendelseBehandlingException;
import no.nav.oebs.nom.kafka.nomshendelse.model.*;

/**
 * Serviceklasse som behandler mottatte skjermingshendelser fra Kafka.
 */
@Slf4j
@Service
public class NomshendelseService extends NomshendelseServiceBase {

	private NomshendelseRepository hendelseRepository;

	//private SkjermingshendelseFacadeRepository hendelseFacadeRepository;

	public NomshendelseService(ServiceConfig serviceConfig, NomshendelseRepository hendelseRepository,
			ObjectMapper objectMapper) {
		super(serviceConfig, objectMapper);
		this.hendelseRepository = hendelseRepository;
	}

	/**
	 * Behandler mottatt skjermingshendelse.
	 * <p>
	 * Status som lagres i skjermingshendelse-tabellen:
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
		NomsHendelse skjermingshendelse = createAndSaveNomsHendelseEntity(mottattHendelse);

		try {
			if (isNyHendelseDuplikat(skjermingshendelse)) {
				skjermingshendelse.setStatus(NomsHendelse.STATUS_DUPLIKAT);
			} else {
				addHendelseArenaToEntity(skjermingshendelse);

				// hendelseFacadeRepository.mottaSkjermingshendelse(skjermingshendelse.getHendelseArena());

				skjermingshendelse.setStatus(NomsHendelse.STATUS_BEHANDLET);
			}
		} catch (Exception e) {
			skjermingshendelse.setStatus(NomsHendelse.STATUS_RETRY);
			skjermingshendelse.setRetryTeller(serviceConfig.getRetryMaxAttempts());
			skjermingshendelse.setRetryTidspunkt(getNextRetryTidspunkt(skjermingshendelse));
			skjermingshendelse.setFeilinformasjon(LoggingUtils.formatExceptionAsString(e));

			log.warn(String.format("Feilet under behandling av skjermingshendelse; id=%d, retrytidspunkt=%s, cause=%s",
					skjermingshendelse.getId(), skjermingshendelse.getRetryTidspunkt(), e.getMessage()), e);

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

		return hendelseRepository.save(hendelse);
	}

	/**
	 * Sjekker om en ny skjermingshendelse er duplikat av tidligere mottatte hendelser. Sjekken gjøres på hendelse ID, samt at
	 * status ikke må være NY.
	 */
	private boolean isNyHendelseDuplikat(NomsHendelse nomsHendelse) {
		return !hendelseRepository
				.findByHendelseIdAndStatusNotIn(nomsHendelse.getHendelseId(), List.of(NomsHendelse.STATUS_NY))
				.isEmpty();
	}
}
