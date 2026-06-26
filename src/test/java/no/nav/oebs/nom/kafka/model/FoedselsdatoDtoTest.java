package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.foedselsdato.Foedselsdato;

@ExtendWith(MockitoExtension.class)
class FoedselsdatoDtoTest {

    @Mock
    private Foedselsdato foedselsdato;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(FoedselsdatoDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        LocalDate dato = LocalDate.of(1985, Month.AUGUST, 10);
        when(foedselsdato.getFoedselsaar()).thenReturn(1985);
        when(foedselsdato.getFoedselsdato()).thenReturn(dato);

        FoedselsdatoDto result = FoedselsdatoDto.map(foedselsdato);

        assertEquals(1985, result.getFoedselsaar());
        assertEquals(dato, result.getFoedselsdato());
    }

    @Test
    void map_withNullFields_mapsNulls() {
        when(foedselsdato.getFoedselsaar()).thenReturn(null);
        when(foedselsdato.getFoedselsdato()).thenReturn(null);

        FoedselsdatoDto result = FoedselsdatoDto.map(foedselsdato);

        assertNull(result.getFoedselsaar());
        assertNull(result.getFoedselsdato());
    }
}
