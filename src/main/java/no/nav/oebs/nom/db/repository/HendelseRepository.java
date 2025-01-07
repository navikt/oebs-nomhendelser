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
 *
 * @param <T>
 *            Hendelsetypen.
 * @param <ID>
 *            ID-typen (primærnøkkel).
 */
@NoRepositoryBean
public interface HendelseRepository<T extends BaseHendelse, ID> extends JpaRepository<T, ID> {

	String SKIP_LOCKED = "-2";

	/**
	 * Finner og låser alle hendelser som skal rekjøres. Dette er hendelser med retry-status der retry-tidspunktet er nådd
	 * (eller har passert).
	 * <p>
	 * For å unngå samtidighetskonflikter benyttes en låsemekanisme:
	 * <ul>
	 * <li>Låsemodus PESSIMISTIC_WRITE - gir eksklusiv lås på radene som leses opp.</li>
	 * <li>Hint SKIP_LOCKED - hopper over rader som allerede er låst.</li>
	 * <li>Hint FOLLOW_ON_LOCKING - benyttes fordi Oracle oppfører seg litt annereledes enn en del andre databaser: <i>Oracle
	 * uses MVCC (Multiversion Concurrency Control) so Readers don't block Writers and Writers don't block Readers. Even if you
	 * acquire a row-level lock with Oracle, and you modify that row without committing, other transactions can still read the
	 * last committed value</i>.</li>
	 * </ul>
	 *
	 * @return En liste med alle hendelser som skal rekjøres.
	 */
	@QueryHints(value = { @QueryHint(name = "jakarta.persistence.lock.timeout", value = SKIP_LOCKED),
			@QueryHint(name = AvailableHints.HINT_FOLLOW_ON_LOCKING, value = "false") })
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<T> findByStatusAndRetryTidspunktLessThanEqual(String status, LocalDateTime tidspunkt);
}
