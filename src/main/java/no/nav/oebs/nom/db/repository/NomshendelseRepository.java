package no.nav.oebs.nom.db.repository;

import java.util.List;

import no.nav.oebs.nom.db.entity.NomsHendelse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * Grensesnitt for repository som håndterer dataaksess mot nomshendelse-tabellen. Metodene implementeres automatisk av
 * Spring Data.
 */
@Repository
public interface NomshendelseRepository extends JpaRepository<NomsHendelse, Long> {

	/**
	 * Finner nomshendelser med spesifisert hendelse ID, unntatt hendelser med spesifisert status.
	 *
	 * @return En liste med alle nomshendelser som ble funnet; ellers en tom liste.
	 */
	List<NomsHendelse> findByHendelseIdAndStatusNotIn(String hendelseId, List<String> statuser);

	/**
	 * Finner nomshendelser med spesifisert fødselsnummer, samt at hendelsene må være nyere enn spesifisert ID-verdi (dvs.
	 * større ID-verdi), unntatt hendelser med spesifisert status.
	 *
	 * @return En liste av alle nomshendelser som oppfyller kriteriene; ellers en tom liste.
	 */
	List<NomsHendelse> findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(String hendelseFodselsnr, Long id,
			List<String> statuser);
}
