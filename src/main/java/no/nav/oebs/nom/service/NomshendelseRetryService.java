package no.nav.oebs.nom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.oebs.nom.logging.LoggingUtils;
import no.nav.oebs.nom.mdc.MdcOperations;
import no.nav.oebs.nom.db.entity.NomsHendelse;
import no.nav.oebs.nom.db.repository.NomshendelseRepository;

/**
 * Serviceklasse som rekjører skjermingshendelser som har feilet.
 */
@Slf4j
@Service
public class NomshendelseRetryService extends NomshendelseServiceBase
		implements HendelseRetryService<NomsHendelse> {

	private NomshendelseRepository hendelseRepository;

//	private SkjermingshendelseFacadeRepository hendelseFacadeRepository;
    //private OebsLoggFacadeRepository oebsLoggFacadeRepository;

	public NomshendelseRetryService(ServiceConfig serviceConfig, NomshendelseRepository hendelseRepository,
			ObjectMapper objectMapper) {
		super(serviceConfig, objectMapper);
		this.hendelseRepository = hendelseRepository;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Status som lagres i hendelse-tabellen:
	 * <ul>
	 * <li>BEHANDLET - vellykket retry av hendelsen.</li>
	 * <li>RETRY - dersom det fortsatt oppstår feil under behandling av hendelsen.</li>
	 * <li>FEILET - dersom det fortsatt feiler etter at alle retry-forsøk er oppbrukt.</li>
	 * <li>ERSTATTET - dersom det i mellomtiden har blitt mottatt en nyere hendelse med samme fødselsnr.</li>
	 * </ul>
	 */
	@Override
	public void retryHendelse(NomsHendelse nomshendelse) {
		try {
			if (isRetryHendelseErstattet(nomshendelse)) {
				nomshendelse.setStatus(NomsHendelse.STATUS_ERSTATTET);
			} else {
				nomshendelse.setKorrelasjonId(MdcOperations.get(MdcOperations.MDC_CORRELATION_ID));
				nomshendelse.decrementRetryTeller();

				/*if (hendelse.getHendelseOebs() == null) {
					addHendelseOebsToEntity(hendelse);
				}
*/
				//hendelseFacadeRepository.mottaSkjermingshendelse(hendelse.getHendelseArena());

				nomshendelse.setStatus(NomsHendelse.STATUS_BEHANDLET);
			}
		} catch (Exception e) {
			if (nomshendelse.getRetryTeller() <= 0) {
				log.error(String.format(
						"Alle rekjøringsforsøk av skjermingshendelse har feilet og status er endret til FEILET; id=%d, cause=%s",
						nomshendelse.getId(), e.getMessage()), e);

				nomshendelse.setStatus(NomsHendelse.STATUS_FEILET);

				//logNomsHendelseError(hendelse);
			} else {
				nomshendelse.setRetryTidspunkt(getNextRetryTidspunkt(nomshendelse));

				log.warn(String.format("Feilet under rekjøring av skjermingshendelse; id=%d, neste retrytidspunkt=%s, cause=%s",
						nomshendelse.getId(), nomshendelse.getRetryTidspunkt(), e.getMessage()), e);
			}

			nomshendelse.appendFeilinformasjon(LoggingUtils.formatExceptionAsString(e));
		}
	}

	/**
	 * Sjekker om det finnes nyere hendelser (større ID-verdi) med samme fødselsnr som erstatter en hendelse under retry. Unntak
	 * dersom status for nyere hendelser er DUPLIKAT; da skal hendelsen under retry ikke settes til ERSTATTET.
	 */
	private boolean isRetryHendelseErstattet(NomsHendelse nomshendelse) {
		return !hendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(nomshendelse.getHendelseFodselsnr(),
				nomshendelse.getId(), List.of(NomsHendelse.STATUS_DUPLIKAT)).isEmpty();
	}
}
