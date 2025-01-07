package no.nav.oebs.nom.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Konfigurasjonsklasse for applikasjonskonfigurasjonen som ligger i Vault.
 */

@Configuration
@PropertySource("file:/var/run/secrets/nais.io/vault/" + VaultConfig.PROPERTY_FILE)
public class VaultConfig {

    static final String PROPERTY_FILE = "secrets.properties";

}
