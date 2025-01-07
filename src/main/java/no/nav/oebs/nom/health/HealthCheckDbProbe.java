package no.nav.oebs.nom.health;

import org.springframework.stereotype.Component;

import no.nav.oebs.nom.db.repository.NomsLoggRepository;

/**
 * Helsesjekk som brukes for å sjekke at databasen er tilgjengelig for applikasjonen.
 */
@Component
public class HealthCheckDbProbe {

	private NomsLoggRepository nomsLoggRepository;

	HealthCheckDbProbe(NomsLoggRepository nomsLoggRepository) {
		this.nomsLoggRepository = nomsLoggRepository;
	}

	/**
	 * Pinger databasen ved å forsøke en spørring mot kall-loggen, men henter ingen data.
	 */
	public void pingDatabase() {
		nomsLoggRepository.pingKallLogg();
	}
}
