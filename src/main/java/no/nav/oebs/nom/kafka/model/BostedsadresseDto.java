package no.nav.oebs.nom.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.bostedsadresse.Bostedsadresse;

import java.time.LocalDate;

/**
 * Klasse for intern representasjon av et Bostedsadresse-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class BostedsadresseDto {

	private LocalDate angittFlyttedato;

	private LocalDate gyldigFraOgMed;

	private LocalDate gyldigTilOgMed;

	private String coAdressenavn;

	private VegadresseDto vegadresse;

	private MatrikkeladresseDto matrikkeladresse;

	private UtenlandskAdresseDto utenlandskAdresse;

	private UkjentBostedDto ukjentBosted;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static BostedsadresseDto map(Bostedsadresse bostedsadresse) {
		if (bostedsadresse == null) {
			return null;
		}
		return BostedsadresseDto.builder() //
				.angittFlyttedato(bostedsadresse.getAngittFlyttedato()) //
				.gyldigFraOgMed(bostedsadresse.getGyldigFraOgMed()) //
				.gyldigTilOgMed(bostedsadresse.getGyldigTilOgMed()) //
				.coAdressenavn(ModelUtils.getAsString(bostedsadresse.getCoAdressenavn())) //
				.vegadresse(VegadresseDto.map(bostedsadresse.getVegadresse())) //
				.matrikkeladresse(MatrikkeladresseDto.map(bostedsadresse.getMatrikkeladresse())) //
				.utenlandskAdresse(UtenlandskAdresseDto.map(bostedsadresse.getUtenlandskAdresse())) //
				.ukjentBosted(UkjentBostedDto.map(bostedsadresse.getUkjentBosted())) //
				.build();
	}
}
