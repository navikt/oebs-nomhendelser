package no.nav.oebs.nom.kafka.nomshendelse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.common.adresse.PostadresseIFrittFormat;

/**
 * Klasse for intern representasjon av et PostadresseIFrittFormat-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class PostadresseIFrittFormatDto {

	private String adresselinje1;

	private String adresselinje2;

	private String adresselinje3;

	private String postnummer;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static PostadresseIFrittFormatDto map(PostadresseIFrittFormat postadresseIFrittFormat) {
		if (postadresseIFrittFormat == null) {
			return null;
		}
		return PostadresseIFrittFormatDto.builder() //
				.adresselinje1(ModelUtils.getAsString(postadresseIFrittFormat.getAdresselinje1())) //
				.adresselinje2(ModelUtils.getAsString(postadresseIFrittFormat.getAdresselinje2())) //
				.adresselinje3(ModelUtils.getAsString(postadresseIFrittFormat.getAdresselinje3())) //
				.postnummer(ModelUtils.getAsString(postadresseIFrittFormat.getPostnummer())) //
				.build();
	}
}
