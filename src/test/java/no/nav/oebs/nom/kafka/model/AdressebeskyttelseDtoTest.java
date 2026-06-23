package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.adressebeskyttelse.Adressebeskyttelse;
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering;

@ExtendWith(MockitoExtension.class)
class AdressebeskyttelseDtoTest {

    @Mock
    private Adressebeskyttelse adressebeskyttelse;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(AdressebeskyttelseDto.map(null));
    }

    @Test
    void map_withGradering_mapsGraderingToString() {
        when(adressebeskyttelse.getGradering()).thenReturn(Gradering.FORTROLIG);

        AdressebeskyttelseDto result = AdressebeskyttelseDto.map(adressebeskyttelse);

        assertEquals("FORTROLIG", result.getGradering());
    }

    @Test
    void map_withStrengtFortrolig_mapsGraderingCorrectly() {
        when(adressebeskyttelse.getGradering()).thenReturn(Gradering.STRENGT_FORTROLIG);

        AdressebeskyttelseDto result = AdressebeskyttelseDto.map(adressebeskyttelse);

        assertEquals("STRENGT_FORTROLIG", result.getGradering());
    }
}
