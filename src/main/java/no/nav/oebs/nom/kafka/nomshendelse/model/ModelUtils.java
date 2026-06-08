package no.nav.oebs.nom.kafka.nomshendelse.model;

/**
 * Felles hjelpemetoder for modellklasser.
 */
public class ModelUtils {

	private ModelUtils() {
	}

	/**
	 * Returner en CharSequence-verdi som et String-objekt; null dersom input er null.
	 * <p>
	 * Avro mapper string til CharSequence, ikke String. Derfor må toString benyttes. Casting fungerer ikke siden Avro-typen til
	 * stringen er org.apache.avro.util.Utf8 (subtype av CharSequence).
	 */
	public static String getAsString(CharSequence value) {
		return (value != null) ? value.toString() : null;
	}
}
