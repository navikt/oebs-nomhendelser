package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.common.adresse.UtenlandskAdresse;

@ExtendWith(MockitoExtension.class)
class UtenlandskAdresseDtoTest {

    @Mock
    private UtenlandskAdresse utenlandskAdresse;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(UtenlandskAdresseDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        when(utenlandskAdresse.getAdressenavnNummer()).thenReturn(new Utf8("Hauptstrasse 5"));
        when(utenlandskAdresse.getBygningEtasjeLeilighet()).thenReturn(new Utf8("Etg 2"));
        when(utenlandskAdresse.getPostboksNummerNavn()).thenReturn(new Utf8("PO Box 42"));
        when(utenlandskAdresse.getPostkode()).thenReturn(new Utf8("10115"));
        when(utenlandskAdresse.getBySted()).thenReturn(new Utf8("Berlin"));
        when(utenlandskAdresse.getRegionDistriktOmraade()).thenReturn(new Utf8("Bayern"));
        when(utenlandskAdresse.getLandkode()).thenReturn(new Utf8("DEU"));

        UtenlandskAdresseDto result = UtenlandskAdresseDto.map(utenlandskAdresse);

        assertEquals("Hauptstrasse 5", result.getAdressenavnNummer());
        assertEquals("Etg 2", result.getBygningEtasjeLeilighet());
        assertEquals("PO Box 42", result.getPostboksNummerNavn());
        assertEquals("10115", result.getPostkode());
        assertEquals("Berlin", result.getBySted());
        assertEquals("Bayern", result.getRegionDistriktOmraade());
        assertEquals("DEU", result.getLandkode());
    }

    @Test
    void map_withNullOptionalFields_mapsNulls() {
        when(utenlandskAdresse.getAdressenavnNummer()).thenReturn(null);
        when(utenlandskAdresse.getBygningEtasjeLeilighet()).thenReturn(null);
        when(utenlandskAdresse.getPostboksNummerNavn()).thenReturn(null);
        when(utenlandskAdresse.getPostkode()).thenReturn(null);
        when(utenlandskAdresse.getBySted()).thenReturn(null);
        when(utenlandskAdresse.getRegionDistriktOmraade()).thenReturn(null);
        when(utenlandskAdresse.getLandkode()).thenReturn(null);

        UtenlandskAdresseDto result = UtenlandskAdresseDto.map(utenlandskAdresse);

        assertNull(result.getAdressenavnNummer());
        assertNull(result.getBygningEtasjeLeilighet());
        assertNull(result.getPostboksNummerNavn());
        assertNull(result.getPostkode());
        assertNull(result.getBySted());
        assertNull(result.getRegionDistriktOmraade());
        assertNull(result.getLandkode());
    }
}
