package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.utflytting.UtflyttingFraNorge;

@ExtendWith(MockitoExtension.class)
class UtflyttingFraNorgeDtoTest {

    @Mock
    private UtflyttingFraNorge utflyttingFraNorge;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(UtflyttingFraNorgeDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        LocalDate utflyttingsdato = LocalDate.of(2022, Month.SEPTEMBER, 1);
        when(utflyttingFraNorge.getTilflyttingsland()).thenReturn(new Utf8("DEU"));
        when(utflyttingFraNorge.getTilflyttingsstedIUtlandet()).thenReturn(new Utf8("Berlin"));
        when(utflyttingFraNorge.getUtflyttingsdato()).thenReturn(utflyttingsdato);

        UtflyttingFraNorgeDto result = UtflyttingFraNorgeDto.map(utflyttingFraNorge);

        assertEquals("DEU", result.getTilflyttingsland());
        assertEquals("Berlin", result.getTilflyttingsstedIUtlandet());
        assertEquals(utflyttingsdato, result.getUtflyttingsdato());
    }

    @Test
    void map_withNullOptionalFields_mapsNulls() {
        when(utflyttingFraNorge.getTilflyttingsland()).thenReturn(null);
        when(utflyttingFraNorge.getTilflyttingsstedIUtlandet()).thenReturn(null);
        when(utflyttingFraNorge.getUtflyttingsdato()).thenReturn(null);

        UtflyttingFraNorgeDto result = UtflyttingFraNorgeDto.map(utflyttingFraNorge);

        assertNull(result.getTilflyttingsland());
        assertNull(result.getTilflyttingsstedIUtlandet());
        assertNull(result.getUtflyttingsdato());
    }
}
