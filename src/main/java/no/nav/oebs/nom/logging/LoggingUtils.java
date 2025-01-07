package no.nav.oebs.nom.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Hjelpemetoder relatert til logging.
 */
public class LoggingUtils {

	private LoggingUtils() {

	}

	/**
	 * Formaterer exception-objektet til stringformat der stacktracen er formatert med linjeskift for hvert kall i tracen.
	 * 
	 * @param exception
	 *            exception-objektet.
	 * @return Formatert exception; <code>null</code> dersom exception-parameteren er null.
	 */
	public static String formatExceptionAsString(Throwable exception) {
		if (exception == null) {
			return null;
		}

		StringWriter stringWriter = new StringWriter();
		exception.printStackTrace(new PrintWriter(stringWriter));

		return stringWriter.toString();
	}
}
