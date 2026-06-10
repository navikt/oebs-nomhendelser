package no.nav.oebs.nom.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.common.adresse.Koordinater;

/**
 * Klasse for intern representasjon av et Koordinater-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class KoordinaterDto {

	private Float x;

	private Float y;

	private Float z;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static KoordinaterDto map(Koordinater koordinater) {
		if (koordinater == null) {
			return null;
		}
		return KoordinaterDto.builder() //
				.x(koordinater.getX()) //
				.y(koordinater.getY()) //
				.z(koordinater.getZ()) //
				.build();
	}
}
