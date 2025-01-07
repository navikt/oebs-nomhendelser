package no.nav.oebs.nom.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-controller som brukes av Kubernetes til readiness- og liveness-prober. Konfigureres i nais-p.yaml.
 */
@RestController
@RequestMapping(path = "/internal")
public class HealthCheckController {

	private final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

	private HealthCheckDbProbe healthCheckDbProbe;

	HealthCheckController(HealthCheckDbProbe healthCheckDbProbe) {
		this.healthCheckDbProbe = healthCheckDbProbe;
	}

	@GetMapping(path = "/isready")
	public void isReady() {
		healthCheckDbProbe.pingDatabase();

		logger.debug("/isready");
	}

	@GetMapping(path = "/isalive")
	public void isalive() {
		healthCheckDbProbe.pingDatabase();

		logger.debug("/isalive");
	}
}
