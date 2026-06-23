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
import no.nav.person.pdl.leesah.common.adresse.Vegadresse;

@ExtendWith(MockitoExtension.class)
class VegadresseDtoTest {

    @Mock
    private Vegadresse vegadresse;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(VegadresseDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        Koordinater koordinater = mock(Koordinater.class);
        when(koordinater.getX()).thenReturn(10.5f);
        when(koordinater.getY()).thenReturn(59.9f);
        when(koordinater.getZ()).thenReturn(null);

        when(vegadresse.getMatrikkelId()).thenReturn(new Utf8("12345"));
        when(vegadresse.getHusnummer()).thenReturn(new Utf8("5"));
        when(vegadresse.getHusbokstav()).thenReturn(new Utf8("B"));
        when(vegadresse.getBruksenhetsnummer()).thenReturn(new Utf8("H0101"));
        when(vegadresse.getAdressenavn()).thenReturn(new Utf8("Storgata"));
        when(vegadresse.getKommunenummer()).thenReturn(new Utf8("0301"));
        when(vegadresse.getBydelsnummer()).thenReturn(new Utf8("030105"));
        when(vegadresse.getTilleggsnavn()).thenReturn(new Utf8("Bestum"));
        when(vegadresse.getPostnummer()).thenReturn(new Utf8("0155"));
        when(vegadresse.getKoordinater()).thenReturn(koordinater);

        VegadresseDto result = VegadresseDto.map(vegadresse);

        assertEquals("12345", result.getMatrikkelId());
        assertEquals("5", result.getHusnummer());
        assertEquals("B", result.getHusbokstav());
        assertEquals("H0101", result.getBruksenhetsnummer());
        assertEquals("Storgata", result.getAdressenavn());
        assertEquals("0301", result.getKommunenummer());
        assertEquals("030105", result.getBydelsnummer());
        assertEquals("Bestum", result.getTilleggsnavn());
        assertEquals("0155", result.getPostnummer());
        assertEquals(10.5f, result.getKoordinater().getX());
    }

    @Test
    void map_withNullKoordinater_mapsNullKoordinater() {
        when(vegadresse.getMatrikkelId()).thenReturn(null);
        when(vegadresse.getHusnummer()).thenReturn(null);
        when(vegadresse.getHusbokstav()).thenReturn(null);
        when(vegadresse.getBruksenhetsnummer()).thenReturn(null);
        when(vegadresse.getAdressenavn()).thenReturn(null);
        when(vegadresse.getKommunenummer()).thenReturn(null);
        when(vegadresse.getBydelsnummer()).thenReturn(null);
        when(vegadresse.getTilleggsnavn()).thenReturn(null);
        when(vegadresse.getPostnummer()).thenReturn(null);
        when(vegadresse.getKoordinater()).thenReturn(null);

        VegadresseDto result = VegadresseDto.map(vegadresse);

        assertNull(result.getKoordinater());
    }
}
