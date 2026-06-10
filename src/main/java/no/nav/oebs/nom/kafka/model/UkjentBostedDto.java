package no.nav.oebs.nom.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.common.adresse.UkjentBosted;

/**
 * Klasse for intern representasjon av et UkjentBosted-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class UkjentBostedDto {

	private String bostedskommune;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static UkjentBostedDto map(UkjentBosted ukjentBosted) {
		if (ukjentBosted == null) {
			return null;
		}
		return UkjentBostedDto.builder() //
				.bostedskommune(ModelUtils.getAsString(ukjentBosted.getBostedskommune())) //
				.build();
	}
}
