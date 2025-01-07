package no.nav.oebs.nom.db.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import no.nav.oebs.nom.db.entity.NomsLogg;
import org.springframework.stereotype.Repository;

/**
 * Implementasjonsklasse for {@link NomsLoggRepositoryCustom}.
 */
@Repository
public class NomsLoggRepositoryImpl implements NomsLoggRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void pingKallLogg() {
		entityManager.createQuery("SELECT k.id FROM NomsLogg k WHERE k.id = 0", NomsLogg.class) //
				.getResultList();
	}
}
