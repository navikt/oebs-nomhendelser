package no.nav.oebs.nom.db.repository;

import no.nav.oebs.nom.db.entity.Livshendelse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Grensesnitt for repository som håndterer dataaksess mot livshendelse-tabellen. Metodene implementeres automatisk av Spring
 * Data.
 */
@Repository
public interface LivshendelseRepository extends JpaRepository<Livshendelse, Long> {

	/**
	 * Finner livshendelser med spesifisert hendelse ID og opplysningstype, unntatt hendelser med spesifisert status.
	 *
	 * @return En liste med alle livshendelser som ble funnet; ellers en tom liste.
	 */
	List<Livshendelse> findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(String hendelseId, String opplysningstype,
			List<String> statuser);

}
