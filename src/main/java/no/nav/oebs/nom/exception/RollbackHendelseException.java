package no.nav.oebs.nom.exception;

/**
 * Exception som kastes dersom en fatal feil oppstår under behandling av hendelsen og denne skal føre til rollback av meldingen
 * til topicen.
 */
public class RollbackHendelseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RollbackHendelseException(String message) {
		super(message);
	}

	public RollbackHendelseException(Throwable cause) {
		super(cause);
	}
}
