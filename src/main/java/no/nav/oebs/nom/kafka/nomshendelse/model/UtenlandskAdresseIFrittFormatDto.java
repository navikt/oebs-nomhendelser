package no.nav.oebs.nom.kafka.nomshendelse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.common.adresse.UtenlandskAdresseIFrittFormat;

/**
 * Klasse for intern representasjon av et UtenlandskAdresseIFrittFormat-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class UtenlandskAdresseIFrittFormatDto {

	private String adresselinje1;

	private String adresselinje2;

	private String adresselinje3;

	private String postkode;

	private String byEllerStedsnavn;

	private String landkode;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static UtenlandskAdresseIFrittFormatDto map(UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat) {
		if (utenlandskAdresseIFrittFormat == null) {
			return null;
		}
		return UtenlandskAdresseIFrittFormatDto.builder() //
				.adresselinje1(ModelUtils.getAsString(utenlandskAdresseIFrittFormat.getAdresselinje1())) //
				.adresselinje2(ModelUtils.getAsString(utenlandskAdresseIFrittFormat.getAdresselinje2())) //
				.adresselinje3(ModelUtils.getAsString(utenlandskAdresseIFrittFormat.getAdresselinje3())) //
				.postkode(ModelUtils.getAsString(utenlandskAdresseIFrittFormat.getPostkode())) //
				.byEllerStedsnavn(ModelUtils.getAsString(utenlandskAdresseIFrittFormat.getByEllerStedsnavn())) //
				.landkode(ModelUtils.getAsString(utenlandskAdresseIFrittFormat.getLandkode())) //
				.build();
	}
}
