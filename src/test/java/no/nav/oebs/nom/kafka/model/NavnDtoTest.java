package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.navn.Navn;
import no.nav.person.pdl.leesah.navn.OriginaltNavn;

@ExtendWith(MockitoExtension.class)
class NavnDtoTest {

    @Mock
    private Navn navn;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(NavnDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        LocalDate gyldigFraOgMed = LocalDate.of(2020, 1, 1);
        OriginaltNavn originaltNavn = mock(OriginaltNavn.class);
        when(originaltNavn.getFornavn()).thenReturn(new Utf8("Ola"));
        when(originaltNavn.getMellomnavn()).thenReturn(null);
        when(originaltNavn.getEtternavn()).thenReturn(new Utf8("Nordmann"));

        when(navn.getFornavn()).thenReturn(new Utf8("Ola"));
        when(navn.getMellomnavn()).thenReturn(new Utf8("Erik"));
        when(navn.getEtternavn()).thenReturn(new Utf8("Nordmann"));
        when(navn.getForkortetNavn()).thenReturn(new Utf8("Ola Nordmann"));
        when(navn.getOriginaltNavn()).thenReturn(originaltNavn);
        when(navn.getGyldigFraOgMed()).thenReturn(gyldigFraOgMed);

        NavnDto result = NavnDto.map(navn);

        assertEquals("Ola", result.getFornavn());
        assertEquals("Erik", result.getMellomnavn());
        assertEquals("Nordmann", result.getEtternavn());
        assertEquals("Ola Nordmann", result.getForkortetNavn());
        assertEquals(gyldigFraOgMed, result.getGyldigFraOgMed());
        assertEquals("Ola", result.getOriginaltNavn().getFornavn());
    }

    @Test
    void map_withNullOriginaltNavn_mapsNullOriginaltNavn() {
        when(navn.getFornavn()).thenReturn(new Utf8("Kari"));
        when(navn.getMellomnavn()).thenReturn(null);
        when(navn.getEtternavn()).thenReturn(new Utf8("Normann"));
        when(navn.getForkortetNavn()).thenReturn(null);
        when(navn.getOriginaltNavn()).thenReturn(null);
        when(navn.getGyldigFraOgMed()).thenReturn(null);

        NavnDto result = NavnDto.map(navn);

        assertNull(result.getOriginaltNavn());
        assertNull(result.getMellomnavn());
        assertNull(result.getForkortetNavn());
    }
}
