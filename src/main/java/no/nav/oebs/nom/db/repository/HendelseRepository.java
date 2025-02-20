package no.nav.oebs.nom.db.repository;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;

import no.nav.oebs.nom.db.entity.BaseHendelse;

/**
 * Basisrepository for hendelsetabeller. Arves av aktuelle hendelserepositories.
 **/

@NoRepositoryBean
public interface HendelseRepository<T extends BaseHendelse, ID> extends JpaRepository<T, ID> {

	String SKIP_LOCKED = "-2";

	/**
	 * Finner og låser alle hendelser som skal rekjøres. Dette er hendelser med retry-status der retry-tidspunktet er nådd
	 * (eller har passert).
	 * For å unngå samtidighetskonflikter benyttes låsemekanisme SKIP_LOCKED
	 */
	@QueryHints(value = { @QueryHint(name = "jakarta.persistence.lock.timeout", value = SKIP_LOCKED),
			@QueryHint(name = AvailableHints.HINT_FOLLOW_ON_LOCKING, value = "false") })
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<T> findByStatusAndRetryTidspunktLessThanEqual(String status, LocalDateTime tidspunkt);
}
