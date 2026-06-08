package no.nav.oebs.nom.kafka.nomshendelse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.innflytting.InnflyttingTilNorge;

/**
 * Klasse for intern representasjon av et InnflyttingTilNorge-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class InnflyttingTilNorgeDto {

	private String fraflyttingsland;

	private String fraflyttingsstedIUtlandet;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static InnflyttingTilNorgeDto map(InnflyttingTilNorge innflyttingTilNorge) {
		if (innflyttingTilNorge == null) {
			return null;
		}
		return InnflyttingTilNorgeDto.builder() //
				.fraflyttingsland(ModelUtils.getAsString(innflyttingTilNorge.getFraflyttingsland())) //
				.fraflyttingsstedIUtlandet(ModelUtils.getAsString(innflyttingTilNorge.getFraflyttingsstedIUtlandet())) //
				.build();
	}
}
