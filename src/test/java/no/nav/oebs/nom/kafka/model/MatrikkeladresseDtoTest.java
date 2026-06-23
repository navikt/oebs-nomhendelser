package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.common.adresse.Koordinater;
import no.nav.person.pdl.leesah.common.adresse.Matrikkeladresse;

@ExtendWith(MockitoExtension.class)
class MatrikkeladresseDtoTest {

    @Mock
    private Matrikkeladresse matrikkeladresse;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(MatrikkeladresseDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        Koordinater koordinater = mock(Koordinater.class);
        when(koordinater.getX()).thenReturn(11.0f);
        when(koordinater.getY()).thenReturn(60.0f);
        when(koordinater.getZ()).thenReturn(null);

        when(matrikkeladresse.getMatrikkelId()).thenReturn(new Utf8("99999"));
        when(matrikkeladresse.getBruksenhetsnummer()).thenReturn(new Utf8("U0101"));
        when(matrikkeladresse.getTilleggsnavn()).thenReturn(new Utf8("Gard"));
        when(matrikkeladresse.getPostnummer()).thenReturn(new Utf8("1234"));
        when(matrikkeladresse.getKommunenummer()).thenReturn(new Utf8("3201"));
        when(matrikkeladresse.getKoordinater()).thenReturn(koordinater);

        MatrikkeladresseDto result = MatrikkeladresseDto.map(matrikkeladresse);

        assertEquals("99999", result.getMatrikkelId());
        assertEquals("U0101", result.getBruksenhetsnummer());
        assertEquals("Gard", result.getTilleggsnavn());
        assertEquals("1234", result.getPostnummer());
        assertEquals("3201", result.getKommunenummer());
        assertEquals(11.0f, result.getKoordinater().getX());
    }

    @Test
    void map_withNullKoordinater_mapsNullKoordinater() {
        when(matrikkeladresse.getMatrikkelId()).thenReturn(null);
        when(matrikkeladresse.getBruksenhetsnummer()).thenReturn(null);
        when(matrikkeladresse.getTilleggsnavn()).thenReturn(null);
        when(matrikkeladresse.getPostnummer()).thenReturn(null);
        when(matrikkeladresse.getKommunenummer()).thenReturn(null);
        when(matrikkeladresse.getKoordinater()).thenReturn(null);

        MatrikkeladresseDto result = MatrikkeladresseDto.map(matrikkeladresse);

        assertNull(result.getKoordinater());
    }
}
