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

import no.nav.person.pdl.leesah.common.adresse.Postboksadresse;
import no.nav.person.pdl.leesah.common.adresse.PostadresseIFrittFormat;
import no.nav.person.pdl.leesah.common.adresse.UtenlandskAdresse;
import no.nav.person.pdl.leesah.common.adresse.UtenlandskAdresseIFrittFormat;
import no.nav.person.pdl.leesah.common.adresse.Vegadresse;
import no.nav.person.pdl.leesah.kontaktadresse.Kontaktadresse;

@ExtendWith(MockitoExtension.class)
class KontaktadresseDtoTest {

    @Mock
    private Kontaktadresse kontaktadresse;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(KontaktadresseDto.map(null));
    }

    @Test
    void map_withDatesAndType_mapsDatesAndTypeCorrectly() {
        LocalDate fraOgMed = LocalDate.of(2020, 1, 1);
        LocalDate tilOgMed = LocalDate.of(2025, 12, 31);

        when(kontaktadresse.getGyldigFraOgMed()).thenReturn(fraOgMed);
        when(kontaktadresse.getGyldigTilOgMed()).thenReturn(tilOgMed);
        when(kontaktadresse.getType()).thenReturn(new Utf8("INNLAND"));
        when(kontaktadresse.getCoAdressenavn()).thenReturn(new Utf8("c/o Familie"));
        when(kontaktadresse.getPostboksadresse()).thenReturn(null);
        when(kontaktadresse.getVegadresse()).thenReturn(null);
        when(kontaktadresse.getPostadresseIFrittFormat()).thenReturn(null);
        when(kontaktadresse.getUtenlandskAdresse()).thenReturn(null);
        when(kontaktadresse.getUtenlandskAdresseIFrittFormat()).thenReturn(null);

        KontaktadresseDto result = KontaktadresseDto.map(kontaktadresse);

        assertEquals(fraOgMed, result.getGyldigFraOgMed());
        assertEquals(tilOgMed, result.getGyldigTilOgMed());
        assertEquals("INNLAND", result.getType());
        assertEquals("c/o Familie", result.getCoAdressenavn());
        assertNull(result.getPostboksadresse());
        assertNull(result.getVegadresse());
        assertNull(result.getPostadresseIFrittFormat());
        assertNull(result.getUtenlandskAdresse());
        assertNull(result.getUtenlandskAdresseIFrittFormat());
    }

    @Test
    void map_withPostboksadresse_mapsPostboksadresse() {
        Postboksadresse postboksadresse = mock(Postboksadresse.class);
        when(postboksadresse.getPostboks()).thenReturn(new Utf8("1234"));
        when(postboksadresse.getPostbokseier()).thenReturn(null);
        when(postboksadresse.getPostnummer()).thenReturn(null);

        when(kontaktadresse.getGyldigFraOgMed()).thenReturn(null);
        when(kontaktadresse.getGyldigTilOgMed()).thenReturn(null);
        when(kontaktadresse.getType()).thenReturn(new Utf8("INNLAND"));
        when(kontaktadresse.getCoAdressenavn()).thenReturn(null);
        when(kontaktadresse.getPostboksadresse()).thenReturn(postboksadresse);
        when(kontaktadresse.getVegadresse()).thenReturn(null);
        when(kontaktadresse.getPostadresseIFrittFormat()).thenReturn(null);
        when(kontaktadresse.getUtenlandskAdresse()).thenReturn(null);
        when(kontaktadresse.getUtenlandskAdresseIFrittFormat()).thenReturn(null);

        KontaktadresseDto result = KontaktadresseDto.map(kontaktadresse);

        assertEquals("1234", result.getPostboksadresse().getPostboks());
    }

    @Test
    void map_withUtenlandskAdresse_mapsUtenlandskAdresse() {
        UtenlandskAdresse utenlandskAdresse = mock(UtenlandskAdresse.class);
        when(utenlandskAdresse.getLandkode()).thenReturn(new Utf8("FRA"));
        when(utenlandskAdresse.getAdressenavnNummer()).thenReturn(null);
        when(utenlandskAdresse.getBygningEtasjeLeilighet()).thenReturn(null);
        when(utenlandskAdresse.getPostboksNummerNavn()).thenReturn(null);
        when(utenlandskAdresse.getPostkode()).thenReturn(null);
        when(utenlandskAdresse.getBySted()).thenReturn(null);
        when(utenlandskAdresse.getRegionDistriktOmraade()).thenReturn(null);

        when(kontaktadresse.getGyldigFraOgMed()).thenReturn(null);
        when(kontaktadresse.getGyldigTilOgMed()).thenReturn(null);
        when(kontaktadresse.getType()).thenReturn(new Utf8("UTLAND"));
        when(kontaktadresse.getCoAdressenavn()).thenReturn(null);
        when(kontaktadresse.getPostboksadresse()).thenReturn(null);
        when(kontaktadresse.getVegadresse()).thenReturn(null);
        when(kontaktadresse.getPostadresseIFrittFormat()).thenReturn(null);
        when(kontaktadresse.getUtenlandskAdresse()).thenReturn(utenlandskAdresse);
        when(kontaktadresse.getUtenlandskAdresseIFrittFormat()).thenReturn(null);

        KontaktadresseDto result = KontaktadresseDto.map(kontaktadresse);

        assertEquals("FRA", result.getUtenlandskAdresse().getLandkode());
    }
}
