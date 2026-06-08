package no.nav.oebs.nom.db.repository;

import no.nav.oebs.nom.db.entity.Logg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Grensesnitt for repository som håndterer dataaksess mot NomsLogg.
 */
@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public interface LoggRepository extends JpaRepository<Logg, Integer>, PingRepository {

}
