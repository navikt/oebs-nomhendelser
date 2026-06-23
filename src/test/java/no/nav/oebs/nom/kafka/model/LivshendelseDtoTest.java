package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.person.pdl.leesah.Endringstype;
import no.nav.person.pdl.leesah.Personhendelse;
import no.nav.person.pdl.leesah.adressebeskyttelse.Adressebeskyttelse;
import no.nav.person.pdl.leesah.adressebeskyttelse.Gradering;

@ExtendWith(MockitoExtension.class)
class LivshendelseDtoTest {

    @Mock
    private Personhendelse personhendelse;

    @Test
    void map_withRequiredFields_mapsTopLevelFieldsCorrectly() {
        Instant opprettet = Instant.parse("2024-03-01T10:00:00Z");

        when(personhendelse.getHendelseId()).thenReturn(new Utf8("hendelse-123"));
        when(personhendelse.getPersonidenter()).thenReturn(List.of(new Utf8("12345678901")));
        when(personhendelse.getMaster()).thenReturn(new Utf8("FREG"));
        when(personhendelse.getOpprettet()).thenReturn(opprettet);
        when(personhendelse.getOpplysningstype()).thenReturn(new Utf8("ADRESSEBESKYTTELSE_V1"));
        when(personhendelse.getEndringstype()).thenReturn(Endringstype.OPPRETTET);
        when(personhendelse.getTidligereHendelseId()).thenReturn(null);
        when(personhendelse.getAdressebeskyttelse()).thenReturn(null);
        when(personhendelse.getDoedsfall()).thenReturn(null);
        when(personhendelse.getFoedselsdato()).thenReturn(null);
        when(personhendelse.getUtflyttingFraNorge()).thenReturn(null);
        when(personhendelse.getInnflyttingTilNorge()).thenReturn(null);
        when(personhendelse.getFolkeregisteridentifikator()).thenReturn(null);
        when(personhendelse.getNavn()).thenReturn(null);
        when(personhendelse.getKontaktadresse()).thenReturn(null);
        when(personhendelse.getBostedsadresse()).thenReturn(null);

        LivshendelseDto result = LivshendelseDto.map(personhendelse);

        assertEquals("hendelse-123", result.getHendelseId());
        assertEquals(List.of("12345678901"), result.getPersonidenter());
        assertEquals("FREG", result.getMaster());
        assertEquals("ADRESSEBESKYTTELSE_V1", result.getOpplysningstype());
        assertEquals("OPPRETTET", result.getEndringstype());
        assertNull(result.getTidligereHendelseId());
    }

    @Test
    void map_withTidligereHendelseId_mapsTidligereHendelseId() {
        Instant opprettet = Instant.now();

        when(personhendelse.getHendelseId()).thenReturn(new Utf8("hendelse-456"));
        when(personhendelse.getPersonidenter()).thenReturn(List.of(new Utf8("98765432100")));
        when(personhendelse.getMaster()).thenReturn(new Utf8("FREG"));
        when(personhendelse.getOpprettet()).thenReturn(opprettet);
        when(personhendelse.getOpplysningstype()).thenReturn(new Utf8("DOEDSFALL_V1"));
        when(personhendelse.getEndringstype()).thenReturn(Endringstype.KORRIGERT);
        when(personhendelse.getTidligereHendelseId()).thenReturn(new Utf8("hendelse-123"));
        when(personhendelse.getAdressebeskyttelse()).thenReturn(null);
        when(personhendelse.getDoedsfall()).thenReturn(null);
        when(personhendelse.getFoedselsdato()).thenReturn(null);
        when(personhendelse.getUtflyttingFraNorge()).thenReturn(null);
        when(personhendelse.getInnflyttingTilNorge()).thenReturn(null);
        when(personhendelse.getFolkeregisteridentifikator()).thenReturn(null);
        when(personhendelse.getNavn()).thenReturn(null);
        when(personhendelse.getKontaktadresse()).thenReturn(null);
        when(personhendelse.getBostedsadresse()).thenReturn(null);

        LivshendelseDto result = LivshendelseDto.map(personhendelse);

        assertEquals("hendelse-123", result.getTidligereHendelseId());
        assertEquals("KORRIGERT", result.getEndringstype());
    }

    @Test
    void map_withAdressebeskyttelse_mapsAdressebeskyttelse() {
        Adressebeskyttelse adressebeskyttelse = mock(Adressebeskyttelse.class);
        when(adressebeskyttelse.getGradering()).thenReturn(Gradering.STRENGT_FORTROLIG);

        when(personhendelse.getHendelseId()).thenReturn(new Utf8("hendelse-789"));
        when(personhendelse.getPersonidenter()).thenReturn(List.of(new Utf8("11111111111")));
        when(personhendelse.getMaster()).thenReturn(new Utf8("FREG"));
        when(personhendelse.getOpprettet()).thenReturn(Instant.now());
        when(personhendelse.getOpplysningstype()).thenReturn(new Utf8("ADRESSEBESKYTTELSE_V1"));
        when(personhendelse.getEndringstype()).thenReturn(Endringstype.OPPRETTET);
        when(personhendelse.getTidligereHendelseId()).thenReturn(null);
        when(personhendelse.getAdressebeskyttelse()).thenReturn(adressebeskyttelse);
        when(personhendelse.getDoedsfall()).thenReturn(null);
        when(personhendelse.getFoedselsdato()).thenReturn(null);
        when(personhendelse.getUtflyttingFraNorge()).thenReturn(null);
        when(personhendelse.getInnflyttingTilNorge()).thenReturn(null);
        when(personhendelse.getFolkeregisteridentifikator()).thenReturn(null);
        when(personhendelse.getNavn()).thenReturn(null);
        when(personhendelse.getKontaktadresse()).thenReturn(null);
        when(personhendelse.getBostedsadresse()).thenReturn(null);

        LivshendelseDto result = LivshendelseDto.map(personhendelse);

        assertEquals("STRENGT_FORTROLIG", result.getAdressebeskyttelse().getGradering());
    }

    @Test
    void map_withNullSubObjects_mapsNullSubObjects() {
        when(personhendelse.getHendelseId()).thenReturn(new Utf8("hendelse-000"));
        when(personhendelse.getPersonidenter()).thenReturn(List.of());
        when(personhendelse.getMaster()).thenReturn(new Utf8("FREG"));
        when(personhendelse.getOpprettet()).thenReturn(Instant.now());
        when(personhendelse.getOpplysningstype()).thenReturn(new Utf8("NAVN_V1"));
        when(personhendelse.getEndringstype()).thenReturn(Endringstype.ANNULLERT);
        when(personhendelse.getTidligereHendelseId()).thenReturn(null);
        when(personhendelse.getAdressebeskyttelse()).thenReturn(null);
        when(personhendelse.getDoedsfall()).thenReturn(null);
        when(personhendelse.getFoedselsdato()).thenReturn(null);
        when(personhendelse.getUtflyttingFraNorge()).thenReturn(null);
        when(personhendelse.getInnflyttingTilNorge()).thenReturn(null);
        when(personhendelse.getFolkeregisteridentifikator()).thenReturn(null);
        when(personhendelse.getNavn()).thenReturn(null);
        when(personhendelse.getKontaktadresse()).thenReturn(null);
        when(personhendelse.getBostedsadresse()).thenReturn(null);

        LivshendelseDto result = LivshendelseDto.map(personhendelse);

        assertNull(result.getAdressebeskyttelse());
        assertNull(result.getDoedsfall());
        assertNull(result.getFoedselsdato());
        assertNull(result.getUtflyttingFraNorge());
        assertNull(result.getInnflyttingTilNorge());
        assertNull(result.getFolkeregisteridentifikator());
        assertNull(result.getNavn());
        assertNull(result.getKontaktadresse());
        assertNull(result.getBostedsadresse());
    }
}
