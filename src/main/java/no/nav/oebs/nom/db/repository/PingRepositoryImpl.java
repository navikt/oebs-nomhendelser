package no.nav.oebs.nom.db.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import no.nav.oebs.nom.db.entity.Logg;
import org.springframework.stereotype.Repository;

/**
 * Implementasjonsklasse for {@link PingRepository}.
 */
@Repository
public class PingRepositoryImpl implements PingRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public void pingKallLogg() {

		entityManager.createQuery("SELECT k FROM Logg k WHERE k.id = 0", Logg.class)
				.getResultList();
	}
}
