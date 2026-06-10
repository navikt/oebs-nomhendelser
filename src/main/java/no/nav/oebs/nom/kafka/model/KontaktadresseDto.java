package no.nav.oebs.nom.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.kontaktadresse.Kontaktadresse;

import java.time.LocalDate;

/**
 * Klasse for intern representasjon av et Kontaktadresse-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class KontaktadresseDto {

	private LocalDate gyldigFraOgMed;

	private LocalDate gyldigTilOgMed;

	private String type;

	private String coAdressenavn;

	private PostboksadresseDto postboksadresse;

	private VegadresseDto vegadresse;

	private PostadresseIFrittFormatDto postadresseIFrittFormat;

	private UtenlandskAdresseDto utenlandskAdresse;

	private UtenlandskAdresseIFrittFormatDto utenlandskAdresseIFrittFormat;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static KontaktadresseDto map(Kontaktadresse kontaktadresse) {
		if (kontaktadresse == null) {
			return null;
		}
		return KontaktadresseDto.builder() //
				.gyldigFraOgMed(kontaktadresse.getGyldigFraOgMed()) //
				.gyldigTilOgMed(kontaktadresse.getGyldigTilOgMed()) //
				.type(ModelUtils.getAsString(kontaktadresse.getType())) //
				.coAdressenavn(ModelUtils.getAsString(kontaktadresse.getCoAdressenavn())) //
				.postboksadresse(PostboksadresseDto.map(kontaktadresse.getPostboksadresse())) //
				.vegadresse(VegadresseDto.map(kontaktadresse.getVegadresse())) //
				.postadresseIFrittFormat(PostadresseIFrittFormatDto.map(kontaktadresse.getPostadresseIFrittFormat())) //
				.utenlandskAdresse(UtenlandskAdresseDto.map(kontaktadresse.getUtenlandskAdresse())) //
				.utenlandskAdresseIFrittFormat(
						UtenlandskAdresseIFrittFormatDto.map(kontaktadresse.getUtenlandskAdresseIFrittFormat())) //
				.build();
	}
}
