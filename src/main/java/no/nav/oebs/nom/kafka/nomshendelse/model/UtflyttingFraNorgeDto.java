package no.nav.oebs.nom.kafka.nomshendelse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.utflytting.UtflyttingFraNorge;

import java.time.LocalDate;

/**
 * Klasse for intern representasjon av et UtflyttingFraNorge-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class UtflyttingFraNorgeDto {

	private String tilflyttingsland;

	private String tilflyttingsstedIUtlandet;

	private LocalDate utflyttingsdato;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static UtflyttingFraNorgeDto map(UtflyttingFraNorge utflyttingFraNorge) {
		if (utflyttingFraNorge == null) {
			return null;
		}
		return UtflyttingFraNorgeDto.builder() //
				.tilflyttingsland(ModelUtils.getAsString(utflyttingFraNorge.getTilflyttingsland())) //
				.tilflyttingsstedIUtlandet(ModelUtils.getAsString(utflyttingFraNorge.getTilflyttingsstedIUtlandet())) //
				.utflyttingsdato(utflyttingFraNorge.getUtflyttingsdato()) //
				.build();
	}
}
