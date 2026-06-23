package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.common.adresse.Postboksadresse;

@ExtendWith(MockitoExtension.class)
class PostboksadresseDtoTest {

    @Mock
    private Postboksadresse postboksadresse;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(PostboksadresseDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        when(postboksadresse.getPostbokseier()).thenReturn(new Utf8("Eier AS"));
        when(postboksadresse.getPostboks()).thenReturn(new Utf8("1234"));
        when(postboksadresse.getPostnummer()).thenReturn(new Utf8("0001"));

        PostboksadresseDto result = PostboksadresseDto.map(postboksadresse);

        assertEquals("Eier AS", result.getPostbokseier());
        assertEquals("1234", result.getPostboks());
        assertEquals("0001", result.getPostnummer());
    }

    @Test
    void map_withNullOptionalFields_mapsNulls() {
        when(postboksadresse.getPostbokseier()).thenReturn(null);
        when(postboksadresse.getPostboks()).thenReturn(new Utf8("5678"));
        when(postboksadresse.getPostnummer()).thenReturn(null);

        PostboksadresseDto result = PostboksadresseDto.map(postboksadresse);

        assertNull(result.getPostbokseier());
        assertEquals("5678", result.getPostboks());
        assertNull(result.getPostnummer());
    }
}
