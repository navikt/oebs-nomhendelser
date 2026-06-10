package no.nav.oebs.nom.kafka.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.navn.Navn;

import java.time.LocalDate;

/**
 * Klasse for intern representasjon av et Navn-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class NavnDto {

	private String fornavn;

	private String mellomnavn;

	private String etternavn;

	private String forkortetNavn;

	private OriginaltNavnDto originaltNavn;

	private LocalDate gyldigFraOgMed;

	/**
	 * Mapper fra Avro- til Java-objekt.
	 */
	public static NavnDto map(Navn navn) {
		if (navn == null) {
			return null;
		}
		return NavnDto.builder() //
				.fornavn(ModelUtils.getAsString(navn.getFornavn())) //
				.mellomnavn(ModelUtils.getAsString(navn.getMellomnavn())) //
				.etternavn(ModelUtils.getAsString(navn.getEtternavn())) //
				.forkortetNavn(ModelUtils.getAsString(navn.getForkortetNavn())) //
				.originaltNavn(OriginaltNavnDto.map(navn.getOriginaltNavn())) //
				.gyldigFraOgMed(navn.getGyldigFraOgMed()) //
				.build();
	}
}
