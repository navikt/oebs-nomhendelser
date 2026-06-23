package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.innflytting.InnflyttingTilNorge;

@ExtendWith(MockitoExtension.class)
class InnflyttingTilNorgeDtoTest {

    @Mock
    private InnflyttingTilNorge innflyttingTilNorge;

    @Test
    void map_nullInput_returnsNull() {
        assertNull(InnflyttingTilNorgeDto.map(null));
    }

    @Test
    void map_withAllFields_mapsAllFieldsCorrectly() {
        when(innflyttingTilNorge.getFraflyttingsland()).thenReturn(new Utf8("SWE"));
        when(innflyttingTilNorge.getFraflyttingsstedIUtlandet()).thenReturn(new Utf8("Stockholm"));

        InnflyttingTilNorgeDto result = InnflyttingTilNorgeDto.map(innflyttingTilNorge);

        assertEquals("SWE", result.getFraflyttingsland());
        assertEquals("Stockholm", result.getFraflyttingsstedIUtlandet());
    }

    @Test
    void map_withNullOptionalFields_mapsNulls() {
        when(innflyttingTilNorge.getFraflyttingsland()).thenReturn(null);
        when(innflyttingTilNorge.getFraflyttingsstedIUtlandet()).thenReturn(null);

        InnflyttingTilNorgeDto result = InnflyttingTilNorgeDto.map(innflyttingTilNorge);

        assertNull(result.getFraflyttingsland());
        assertNull(result.getFraflyttingsstedIUtlandet());
    }
}
