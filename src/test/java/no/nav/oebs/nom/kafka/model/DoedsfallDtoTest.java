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

import no.nav.person.pdl.leesah.doedsfall.Doedsfall;

@ExtendWith(MockitoExtension.class)
class DoedsfallDtoTest {

    @Mock
    private Doedsfall doedsfall;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(DoedsfallDto.map(null));
    }

    @Test
    void map_withDoedsdato_mapsDateCorrectly() {
        LocalDate doedsdato = LocalDate.of(2023, Month.JULY, 15);
        when(doedsfall.getDoedsdato()).thenReturn(doedsdato);

        DoedsfallDto result = DoedsfallDto.map(doedsfall);

        assertEquals(doedsdato, result.getDoedsdato());
    }

    @Test
    void map_withNullDoedsdato_mapsNullDate() {
        when(doedsfall.getDoedsdato()).thenReturn(null);

        DoedsfallDto result = DoedsfallDto.map(doedsfall);

        assertNull(result.getDoedsdato());
    }
}
