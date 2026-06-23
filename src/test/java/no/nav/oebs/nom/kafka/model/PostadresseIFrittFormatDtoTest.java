package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.common.adresse.PostadresseIFrittFormat;

@ExtendWith(MockitoExtension.class)
class PostadresseIFrittFormatDtoTest {

    @Mock
    private PostadresseIFrittFormat postadresseIFrittFormat;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(PostadresseIFrittFormatDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        when(postadresseIFrittFormat.getAdresselinje1()).thenReturn(new Utf8("Gateveien 1"));
        when(postadresseIFrittFormat.getAdresselinje2()).thenReturn(new Utf8("c/o Noen"));
        when(postadresseIFrittFormat.getAdresselinje3()).thenReturn(new Utf8("Ekstra linje"));
        when(postadresseIFrittFormat.getPostnummer()).thenReturn(new Utf8("0150"));

        PostadresseIFrittFormatDto result = PostadresseIFrittFormatDto.map(postadresseIFrittFormat);

        assertEquals("Gateveien 1", result.getAdresselinje1());
        assertEquals("c/o Noen", result.getAdresselinje2());
        assertEquals("Ekstra linje", result.getAdresselinje3());
        assertEquals("0150", result.getPostnummer());
    }

    @Test
    void map_withNullOptionalFields_mapsNulls() {
        when(postadresseIFrittFormat.getAdresselinje1()).thenReturn(null);
        when(postadresseIFrittFormat.getAdresselinje2()).thenReturn(null);
        when(postadresseIFrittFormat.getAdresselinje3()).thenReturn(null);
        when(postadresseIFrittFormat.getPostnummer()).thenReturn(null);

        PostadresseIFrittFormatDto result = PostadresseIFrittFormatDto.map(postadresseIFrittFormat);

        assertNull(result.getAdresselinje1());
        assertNull(result.getAdresselinje2());
        assertNull(result.getAdresselinje3());
        assertNull(result.getPostnummer());
    }
}
