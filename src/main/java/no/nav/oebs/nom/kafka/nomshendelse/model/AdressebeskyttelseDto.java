package no.nav.oebs.nom.kafka.nomshendelse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.adressebeskyttelse.Adressebeskyttelse;

/**
 * Klasse for intern representasjon av et Adressebeskyttelse-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class AdressebeskyttelseDto {

	private String gradering;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static AdressebeskyttelseDto map(Adressebeskyttelse adressebeskyttelse) {
		if (adressebeskyttelse == null) {
			return null;
		}
		return AdressebeskyttelseDto.builder() //
				.gradering(adressebeskyttelse.getGradering().name()) //
				.build();
	}
}
