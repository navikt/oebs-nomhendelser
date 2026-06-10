package no.nav.oebs.nom.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.doedsfall.Doedsfall;

import java.time.LocalDate;

/**
 * Klasse for intern representasjon av et Doedsfall-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class DoedsfallDto {

	private LocalDate doedsdato;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static DoedsfallDto map(Doedsfall doedsfall) {
		if (doedsfall == null) {
			return null;
		}
		return DoedsfallDto.builder() //
				.doedsdato(doedsfall.getDoedsdato()) //
				.build();
	}
}
