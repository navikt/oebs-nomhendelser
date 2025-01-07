package no.nav.oebs.nom.db.repository;

/**
 * Egendefinerte metoder for NomsLogg-repository.
 */
public interface NomsLoggRepositoryCustom {

	/**
	 * Kjører en select mot tabellen NomsLogg, uten å finne noen rader. Vil feile hvis databasen ikke er tilgjengelig.
	 */
	void pingKallLogg();
}
