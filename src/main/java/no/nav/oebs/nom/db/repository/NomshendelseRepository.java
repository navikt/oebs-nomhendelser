package no.nav.oebs.nom.db.repository;

import java.util.List;

import no.nav.oebs.nom.db.entity.NomsHendelse;
import org.springframework.stereotype.Repository;


/**
 * Grensesnitt for repository som håndterer dataaksess mot skjermingshendelse-tabellen. Metodene implementeres automatisk av
 * Spring Data.
 */
@Repository
public interface NomshendelseRepository extends HendelseRepository<NomsHendelse, Long> {

	/**
	 * Finner skjermingshendelser med spesifisert hendelse ID, unntatt hendelser med spesifisert status.
	 *
	 * @return En liste med alle skjermingshendelser som ble funnet; ellers en tom liste.
	 */
	List<NomsHendelse> findByHendelseIdAndStatusNotIn(String hendelseId, List<String> statuser);

	/**
	 * Finner skjermingshendelser med spesifisert fødselsnummer, samt at hendelsene må være nyere enn spesifisert ID-verdi (dvs.
	 * større ID-verdi), unntatt hendelser med spesifisert status.
	 *
	 * @return En liste av alle skjermingshendelser som oppfyller kriteriene; ellers en tom liste.
	 */
	List<NomsHendelse> findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(String hendelseFodselsnr, Long id,
			List<String> statuser);
}
