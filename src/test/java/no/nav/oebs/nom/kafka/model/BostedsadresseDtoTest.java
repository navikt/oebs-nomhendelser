package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.bostedsadresse.Bostedsadresse;
import no.nav.person.pdl.leesah.common.adresse.UkjentBosted;
import no.nav.person.pdl.leesah.common.adresse.Vegadresse;

@ExtendWith(MockitoExtension.class)
class BostedsadresseDtoTest {

    @Mock
    private Bostedsadresse bostedsadresse;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(BostedsadresseDto.map(null));
    }

    @Test
    void map_withDatesAndCoAdressenavn_mapsDatesCorrectly() {
        LocalDate angittFlyttedato = LocalDate.of(2021, Month.MAY, 1);
        LocalDate fraOgMed = LocalDate.of(2021, Month.MAY, 1);
        LocalDate tilOgMed = LocalDate.of(2030, Month.DECEMBER, 31);

        when(bostedsadresse.getAngittFlyttedato()).thenReturn(angittFlyttedato);
        when(bostedsadresse.getGyldigFraOgMed()).thenReturn(fraOgMed);
        when(bostedsadresse.getGyldigTilOgMed()).thenReturn(tilOgMed);
        when(bostedsadresse.getCoAdressenavn()).thenReturn(new Utf8("c/o Testesen"));
        when(bostedsadresse.getVegadresse()).thenReturn(null);
        when(bostedsadresse.getMatrikkeladresse()).thenReturn(null);
        when(bostedsadresse.getUtenlandskAdresse()).thenReturn(null);
        when(bostedsadresse.getUkjentBosted()).thenReturn(null);

        BostedsadresseDto result = BostedsadresseDto.map(bostedsadresse);

        assertEquals(angittFlyttedato, result.getAngittFlyttedato());
        assertEquals(fraOgMed, result.getGyldigFraOgMed());
        assertEquals(tilOgMed, result.getGyldigTilOgMed());
        assertEquals("c/o Testesen", result.getCoAdressenavn());
        assertNull(result.getVegadresse());
        assertNull(result.getMatrikkeladresse());
        assertNull(result.getUtenlandskAdresse());
        assertNull(result.getUkjentBosted());
    }

    @Test
    void map_withVegadresse_mapsVegadresse() {
        Vegadresse vegadresse = mock(Vegadresse.class);
        when(vegadresse.getAdressenavn()).thenReturn(new Utf8("Kirkegata"));
        when(vegadresse.getHusnummer()).thenReturn(new Utf8("1"));
        when(vegadresse.getHusbokstav()).thenReturn(null);
        when(vegadresse.getBruksenhetsnummer()).thenReturn(null);
        when(vegadresse.getMatrikkelId()).thenReturn(null);
        when(vegadresse.getKommunenummer()).thenReturn(null);
        when(vegadresse.getBydelsnummer()).thenReturn(null);
        when(vegadresse.getTilleggsnavn()).thenReturn(null);
        when(vegadresse.getPostnummer()).thenReturn(null);
        when(vegadresse.getKoordinater()).thenReturn(null);

        when(bostedsadresse.getAngittFlyttedato()).thenReturn(null);
        when(bostedsadresse.getGyldigFraOgMed()).thenReturn(null);
        when(bostedsadresse.getGyldigTilOgMed()).thenReturn(null);
        when(bostedsadresse.getCoAdressenavn()).thenReturn(null);
        when(bostedsadresse.getVegadresse()).thenReturn(vegadresse);
        when(bostedsadresse.getMatrikkeladresse()).thenReturn(null);
        when(bostedsadresse.getUtenlandskAdresse()).thenReturn(null);
        when(bostedsadresse.getUkjentBosted()).thenReturn(null);

        BostedsadresseDto result = BostedsadresseDto.map(bostedsadresse);

        assertEquals("Kirkegata", result.getVegadresse().getAdressenavn());
    }

    @Test
    void map_withUkjentBosted_mapsUkjentBosted() {
        UkjentBosted ukjentBosted = mock(UkjentBosted.class);
        when(ukjentBosted.getBostedskommune()).thenReturn(new Utf8("0301"));

        when(bostedsadresse.getAngittFlyttedato()).thenReturn(null);
        when(bostedsadresse.getGyldigFraOgMed()).thenReturn(null);
        when(bostedsadresse.getGyldigTilOgMed()).thenReturn(null);
        when(bostedsadresse.getCoAdressenavn()).thenReturn(null);
        when(bostedsadresse.getVegadresse()).thenReturn(null);
        when(bostedsadresse.getMatrikkeladresse()).thenReturn(null);
        when(bostedsadresse.getUtenlandskAdresse()).thenReturn(null);
        when(bostedsadresse.getUkjentBosted()).thenReturn(ukjentBosted);

        BostedsadresseDto result = BostedsadresseDto.map(bostedsadresse);

        assertEquals("0301", result.getUkjentBosted().getBostedskommune());
    }
}
