package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.foedsel.Foedsel;

@ExtendWith(MockitoExtension.class)
class FoedselDtoTest {

    @Mock
    private Foedsel foedsel;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(FoedselDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        LocalDate foedselsdato = LocalDate.of(1990, 3, 21);
        when(foedsel.getFoedselsaar()).thenReturn(1990);
        when(foedsel.getFoedselsdato()).thenReturn(foedselsdato);
        when(foedsel.getFoedeland()).thenReturn(new Utf8("NOR"));
        when(foedsel.getFoedested()).thenReturn(new Utf8("Oslo"));
        when(foedsel.getFoedekommune()).thenReturn(new Utf8("0301"));

        FoedselDto result = FoedselDto.map(foedsel);

        assertEquals(1990, result.getFoedselsaar());
        assertEquals(foedselsdato, result.getFoedselsdato());
        assertEquals("NOR", result.getFoedeland());
        assertEquals("Oslo", result.getFoedested());
        assertEquals("0301", result.getFoedekommune());
    }

    @Test
    void map_withNullOptionalFields_mapsNulls() {
        when(foedsel.getFoedselsaar()).thenReturn(null);
        when(foedsel.getFoedselsdato()).thenReturn(null);
        when(foedsel.getFoedeland()).thenReturn(null);
        when(foedsel.getFoedested()).thenReturn(null);
        when(foedsel.getFoedekommune()).thenReturn(null);

        FoedselDto result = FoedselDto.map(foedsel);

        assertNull(result.getFoedselsaar());
        assertNull(result.getFoedselsdato());
        assertNull(result.getFoedeland());
        assertNull(result.getFoedested());
        assertNull(result.getFoedekommune());
    }
}
