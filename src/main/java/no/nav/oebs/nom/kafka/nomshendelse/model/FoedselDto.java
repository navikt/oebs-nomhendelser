package no.nav.oebs.nom.kafka.nomshendelse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;
import no.nav.person.pdl.leesah.foedsel.Foedsel;

import java.time.LocalDate;

/**
 * Klasse for intern representasjon av et Foedsel-objekt i en livshendelse.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonInclude(Include.NON_NULL)
public class FoedselDto {

    private Integer foedselsaar;

    private LocalDate foedselsdato;

    private String foedeland;

    private String foedested;

    private String foedekommune;


    /**
     * Mapper fra Avro- til Java-objekt.
     */
    public static FoedselDto map(Foedsel foedsel) {
        if (foedsel == null) {
            return null;
        }
        return FoedselDto.builder() //
                .foedselsaar(foedsel.getFoedselsaar()) //
                .foedselsdato(foedsel.getFoedselsdato()) //
                .foedeland(ModelUtils.getAsString(foedsel.getFoedeland())) //
                .foedested(ModelUtils.getAsString(foedsel.getFoedested())) //
                .foedekommune(ModelUtils.getAsString(foedsel.getFoedekommune())) //
                .build();
    }
}