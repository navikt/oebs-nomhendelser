package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.common.adresse.UkjentBosted;

@ExtendWith(MockitoExtension.class)
class UkjentBostedDtoTest {

    @Mock
    private UkjentBosted ukjentBosted;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(UkjentBostedDto.map(null));
    }

    @Test
    void map_withBostedskommune_mapsCorrectly() {
        when(ukjentBosted.getBostedskommune()).thenReturn(new Utf8("0301"));

        UkjentBostedDto result = UkjentBostedDto.map(ukjentBosted);

        assertEquals("0301", result.getBostedskommune());
    }

    @Test
    void map_withNullBostedskommune_mapsNull() {
        when(ukjentBosted.getBostedskommune()).thenReturn(null);

        UkjentBostedDto result = UkjentBostedDto.map(ukjentBosted);

        assertNull(result.getBostedskommune());
    }
}
