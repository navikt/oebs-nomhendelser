package no.nav.oebs.nom.exception;

/**
 * Exception som kastes dersom en feil oppstår ved behandling av hendelsen.
 */
public class HendelseBehandlingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HendelseBehandlingException(Throwable cause) {
		super(cause);
	}
}
