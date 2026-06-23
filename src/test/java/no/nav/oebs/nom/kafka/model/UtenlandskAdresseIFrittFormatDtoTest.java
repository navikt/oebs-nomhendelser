package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.common.adresse.UtenlandskAdresseIFrittFormat;

@ExtendWith(MockitoExtension.class)
class UtenlandskAdresseIFrittFormatDtoTest {

    @Mock
    private UtenlandskAdresseIFrittFormat utenlandskAdresseIFrittFormat;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(UtenlandskAdresseIFrittFormatDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        when(utenlandskAdresseIFrittFormat.getAdresselinje1()).thenReturn(new Utf8("Linje 1"));
        when(utenlandskAdresseIFrittFormat.getAdresselinje2()).thenReturn(new Utf8("Linje 2"));
        when(utenlandskAdresseIFrittFormat.getAdresselinje3()).thenReturn(new Utf8("Linje 3"));
        when(utenlandskAdresseIFrittFormat.getPostkode()).thenReturn(new Utf8("12345"));
        when(utenlandskAdresseIFrittFormat.getByEllerStedsnavn()).thenReturn(new Utf8("München"));
        when(utenlandskAdresseIFrittFormat.getLandkode()).thenReturn(new Utf8("DEU"));

        UtenlandskAdresseIFrittFormatDto result = UtenlandskAdresseIFrittFormatDto.map(utenlandskAdresseIFrittFormat);

        assertEquals("Linje 1", result.getAdresselinje1());
        assertEquals("Linje 2", result.getAdresselinje2());
        assertEquals("Linje 3", result.getAdresselinje3());
        assertEquals("12345", result.getPostkode());
        assertEquals("München", result.getByEllerStedsnavn());
        assertEquals("DEU", result.getLandkode());
    }

    @Test
    void map_withNullOptionalFields_mapsNulls() {
        when(utenlandskAdresseIFrittFormat.getAdresselinje1()).thenReturn(null);
        when(utenlandskAdresseIFrittFormat.getAdresselinje2()).thenReturn(null);
        when(utenlandskAdresseIFrittFormat.getAdresselinje3()).thenReturn(null);
        when(utenlandskAdresseIFrittFormat.getPostkode()).thenReturn(null);
        when(utenlandskAdresseIFrittFormat.getByEllerStedsnavn()).thenReturn(null);
        when(utenlandskAdresseIFrittFormat.getLandkode()).thenReturn(null);

        UtenlandskAdresseIFrittFormatDto result = UtenlandskAdresseIFrittFormatDto.map(utenlandskAdresseIFrittFormat);

        assertNull(result.getAdresselinje1());
        assertNull(result.getAdresselinje2());
        assertNull(result.getAdresselinje3());
        assertNull(result.getPostkode());
        assertNull(result.getByEllerStedsnavn());
        assertNull(result.getLandkode());
    }
}
