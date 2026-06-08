package no.nav.oebs.nom.kafka.nomshendelse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.common.adresse.Vegadresse;

/**
 * Klasse for intern representasjon av et Vegadresse-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class VegadresseDto {

	private String matrikkelId;

	private String husnummer;

	private String husbokstav;

	private String bruksenhetsnummer;

	private String adressenavn;

	private String kommunenummer;

	private String bydelsnummer;

	private String tilleggsnavn;

	private String postnummer;

	private KoordinaterDto koordinater;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static VegadresseDto map(Vegadresse vegadresse) {
		if (vegadresse == null) {
			return null;
		}
		return VegadresseDto.builder() //
				.matrikkelId(ModelUtils.getAsString(vegadresse.getMatrikkelId())) //
				.husnummer(ModelUtils.getAsString(vegadresse.getHusnummer())) //
				.husbokstav(ModelUtils.getAsString(vegadresse.getHusbokstav())) //
				.bruksenhetsnummer(ModelUtils.getAsString(vegadresse.getBruksenhetsnummer())) //
				.adressenavn(ModelUtils.getAsString(vegadresse.getAdressenavn())) //
				.kommunenummer(ModelUtils.getAsString(vegadresse.getKommunenummer())) //
				.bydelsnummer(ModelUtils.getAsString(vegadresse.getBydelsnummer())) //
				.tilleggsnavn(ModelUtils.getAsString(vegadresse.getTilleggsnavn())) //
				.postnummer(ModelUtils.getAsString(vegadresse.getPostnummer())) //
				.koordinater(KoordinaterDto.map(vegadresse.getKoordinater())) //
				.build();
	}
}
