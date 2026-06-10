package no.nav.oebs.nom.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.common.adresse.UtenlandskAdresse;

/**
 * Klasse for intern representasjon av et UtenlandskAdresse-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class UtenlandskAdresseDto {

	private String adressenavnNummer;

	private String bygningEtasjeLeilighet;

	private String postboksNummerNavn;

	private String postkode;

	private String bySted;

	private String regionDistriktOmraade;

	private String landkode;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static UtenlandskAdresseDto map(UtenlandskAdresse utenlandskAdresse) {
		if (utenlandskAdresse == null) {
			return null;
		}
		return UtenlandskAdresseDto.builder() //
				.adressenavnNummer(ModelUtils.getAsString(utenlandskAdresse.getAdressenavnNummer())) //
				.bygningEtasjeLeilighet(ModelUtils.getAsString(utenlandskAdresse.getBygningEtasjeLeilighet())) //
				.postboksNummerNavn(ModelUtils.getAsString(utenlandskAdresse.getPostboksNummerNavn())) //
				.postkode(ModelUtils.getAsString(utenlandskAdresse.getPostkode())) //
				.bySted(ModelUtils.getAsString(utenlandskAdresse.getBySted())) //
				.regionDistriktOmraade(ModelUtils.getAsString(utenlandskAdresse.getRegionDistriktOmraade())) //
				.landkode(ModelUtils.getAsString(utenlandskAdresse.getLandkode())) //
				.build();
	}
}
