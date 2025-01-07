package no.nav.oebs.nom.service;

import no.nav.oebs.nom.db.entity.BaseHendelse;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Grensesnitt til en tjeneste som kjører retry av hendelser som har feilet.
 * 
 * @param <T>
 *            Hendelsetypen. Må være en subtype av {@link BaseHendelse}.
 */
public interface HendelseRetryService<T extends BaseHendelse> {

	/**
	 * Rekjører en hendelse som har status RETRY. Rekjøring av hver hendelse skjer i en egen autonom transaksjon.
	 * <p>
	 * Alle feil som oppstår skal håndteres av metoden, mao. metoden skal <u>ikke</u> kaste exception tilbake til kalleren.
	 * 
	 * @param hendelse
	 *            hendelsen som skal rekjøres.
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	void retryHendelse(T hendelse);
}
