package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.navn.OriginaltNavn;

@ExtendWith(MockitoExtension.class)
class OriginaltNavnDtoTest {

    @Mock
    private OriginaltNavn originaltNavn;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(OriginaltNavnDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        when(originaltNavn.getFornavn()).thenReturn(new Utf8("Ola"));
        when(originaltNavn.getMellomnavn()).thenReturn(new Utf8("Erik"));
        when(originaltNavn.getEtternavn()).thenReturn(new Utf8("Nordmann"));

        OriginaltNavnDto result = OriginaltNavnDto.map(originaltNavn);

        assertEquals("Ola", result.getFornavn());
        assertEquals("Erik", result.getMellomnavn());
        assertEquals("Nordmann", result.getEtternavn());
    }

    @Test
    void map_withNullMellomnavn_mapsNull() {
        when(originaltNavn.getFornavn()).thenReturn(new Utf8("Kari"));
        when(originaltNavn.getMellomnavn()).thenReturn(null);
        when(originaltNavn.getEtternavn()).thenReturn(new Utf8("Nordmann"));

        OriginaltNavnDto result = OriginaltNavnDto.map(originaltNavn);

        assertEquals("Kari", result.getFornavn());
        assertNull(result.getMellomnavn());
        assertEquals("Nordmann", result.getEtternavn());
    }
}
