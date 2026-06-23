package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.folkeregisteridentifikator.Folkeregisteridentifikator;

@ExtendWith(MockitoExtension.class)
class FolkeregisteridentifikatorDtoTest {

    @Mock
    private Folkeregisteridentifikator folkeregisteridentifikator;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(FolkeregisteridentifikatorDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        when(folkeregisteridentifikator.getIdentifikasjonsnummer()).thenReturn(new Utf8("12345678901"));
        when(folkeregisteridentifikator.getType()).thenReturn(new Utf8("FNR"));
        when(folkeregisteridentifikator.getStatus()).thenReturn(new Utf8("I_BRUK"));

        FolkeregisteridentifikatorDto result = FolkeregisteridentifikatorDto.map(folkeregisteridentifikator);

        assertEquals("12345678901", result.getIdentifikasjonsnummer());
        assertEquals("FNR", result.getType());
        assertEquals("I_BRUK", result.getStatus());
    }

    @Test
    void map_withNullOptionalFields_mapsNulls() {
        when(folkeregisteridentifikator.getIdentifikasjonsnummer()).thenReturn(null);
        when(folkeregisteridentifikator.getType()).thenReturn(null);
        when(folkeregisteridentifikator.getStatus()).thenReturn(null);

        FolkeregisteridentifikatorDto result = FolkeregisteridentifikatorDto.map(folkeregisteridentifikator);

        assertNull(result.getIdentifikasjonsnummer());
        assertNull(result.getType());
        assertNull(result.getStatus());
    }
}
