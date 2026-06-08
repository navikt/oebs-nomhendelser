package no.nav.oebs.nom.kafka.nomshendelse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import no.nav.person.pdl.leesah.foedselsdato.Foedselsdato;

import java.time.LocalDate;

    /**
     * Klasse for intern representasjon av et Foedselsdato-objekt i en livshendelse.
     */
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class FoedselsdatoDto {

        private Integer foedselsaar;

        private LocalDate foedselsdato;

        /**
         * Mapper fra Avro- til Java-objekt.
         */
        public static FoedselsdatoDto map(Foedselsdato foedselsdato) {
            if (foedselsdato == null) {
                return null;
            }
            return FoedselsdatoDto.builder() //
                    .foedselsaar(foedselsdato.getFoedselsaar()) //
                    .foedselsdato(foedselsdato.getFoedselsdato()) //
                    .build();
        }
}
