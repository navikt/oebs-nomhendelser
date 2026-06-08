package no.nav.oebs.nom.kafka.nomshendelse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.common.adresse.Postboksadresse;

/**
 * Klasse for intern representasjon av et Postboksadresse-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class PostboksadresseDto {

	private String postbokseier;

	private String postboks;

	private String postnummer;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static PostboksadresseDto map(Postboksadresse postboksadresse) {
		if (postboksadresse == null) {
			return null;
		}
		return PostboksadresseDto.builder() //
				.postbokseier(ModelUtils.getAsString(postboksadresse.getPostbokseier())) //
				.postboks(ModelUtils.getAsString(postboksadresse.getPostboks())) //
				.postnummer(ModelUtils.getAsString(postboksadresse.getPostnummer())) //
				.build();
	}
}
