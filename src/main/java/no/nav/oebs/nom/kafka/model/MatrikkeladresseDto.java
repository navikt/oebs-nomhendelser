package no.nav.oebs.nom.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.common.adresse.Matrikkeladresse;

/**
 * Klasse for intern representasjon av et Matrikkeladresse-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class MatrikkeladresseDto {

	private String matrikkelId;

	private String bruksenhetsnummer;

	private String tilleggsnavn;

	private String postnummer;

	private String kommunenummer;

	private KoordinaterDto koordinater;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static MatrikkeladresseDto map(Matrikkeladresse matrikkeladresse) {
		if (matrikkeladresse == null) {
			return null;
		}
		return MatrikkeladresseDto.builder() //
				.matrikkelId(ModelUtils.getAsString(matrikkeladresse.getMatrikkelId())) //
				.bruksenhetsnummer(ModelUtils.getAsString(matrikkeladresse.getBruksenhetsnummer())) //
				.tilleggsnavn(ModelUtils.getAsString(matrikkeladresse.getTilleggsnavn())) //
				.postnummer(ModelUtils.getAsString(matrikkeladresse.getPostnummer())) //
				.kommunenummer(ModelUtils.getAsString(matrikkeladresse.getKommunenummer())) //
				.koordinater(KoordinaterDto.map(matrikkeladresse.getKoordinater())) //
				.build();
	}
}
