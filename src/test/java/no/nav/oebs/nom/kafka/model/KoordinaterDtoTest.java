package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.common.adresse.Koordinater;

@ExtendWith(MockitoExtension.class)
class KoordinaterDtoTest {

    @Mock
    private Koordinater koordinater;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(KoordinaterDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        when(koordinater.getX()).thenReturn(10.5f);
        when(koordinater.getY()).thenReturn(59.9f);
        when(koordinater.getZ()).thenReturn(100.0f);

        KoordinaterDto result = KoordinaterDto.map(koordinater);

        assertEquals(10.5f, result.getX());
        assertEquals(59.9f, result.getY());
        assertEquals(100.0f, result.getZ());
    }

    @Test
    void map_withNullFields_mapsNulls() {
        when(koordinater.getX()).thenReturn(null);
        when(koordinater.getY()).thenReturn(null);
        when(koordinater.getZ()).thenReturn(null);

        KoordinaterDto result = KoordinaterDto.map(koordinater);

        assertNull(result.getX());
        assertNull(result.getY());
        assertNull(result.getZ());
    }
}
