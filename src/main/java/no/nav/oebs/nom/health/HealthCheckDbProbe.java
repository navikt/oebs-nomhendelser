package no.nav.oebs.nom.health;

import org.springframework.stereotype.Component;

import no.nav.oebs.nom.db.repository.LoggRepository;

/**
 * Helsesjekk som brukes for å sjekke at databasen er tilgjengelig for applikasjonen.
 */
@Component
public class HealthCheckDbProbe {

	private LoggRepository loggRepository;

	HealthCheckDbProbe(LoggRepository loggRepository) {
		this.loggRepository = loggRepository;
	}

	/**
	 * Pinger databasen ved å forsøke en spørring mot kall-loggen, men henter ingen data.
	 */
	public void pingDatabase() {
		loggRepository.pingKallLogg();
	}
}
