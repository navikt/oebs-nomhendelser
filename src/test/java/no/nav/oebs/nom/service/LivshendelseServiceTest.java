package no.nav.oebs.nom.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import tools.jackson.databind.json.JsonMapper;
import no.nav.oebs.nom.db.entity.BaseHendelse;
import no.nav.oebs.nom.db.entity.Livshendelse;
import no.nav.oebs.nom.db.repository.LivshendelseRepository;
import no.nav.oebs.nom.exception.HendelseBehandlingException;
import no.nav.oebs.nom.kafka.model.LivshendelseDto;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LivshendelseServiceTest {

    @Mock
    private LivshendelseRepository livshendelseRepository;

    @Mock
    private ServiceConfig serviceConfig;

    private LivshendelseService livshendelseService;

    @BeforeEach
    void setUp() {
        livshendelseService = new LivshendelseService(serviceConfig, livshendelseRepository, new JsonMapper());

        when(serviceConfig.getRetryMaxAttempts()).thenReturn(5);
        when(serviceConfig.getRetryAttempt1DelayMins()).thenReturn(1);
        when(serviceConfig.getRetryAttemptNDelayMins()).thenReturn(60);

        when(livshendelseRepository.save(any(Livshendelse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void behandleHendelse_newEvent_setsStatusToBehandlet() {
        when(livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(anyString(), anyString(), anyList()))
                .thenReturn(List.of());

        livshendelseService.behandleHendelse(buildDto("hendelse-1", List.of("12345678901"), "NAVN_V1"));

        ArgumentCaptor<Livshendelse> captor = ArgumentCaptor.forClass(Livshendelse.class);
        verify(livshendelseRepository).save(captor.capture());
        assertEquals(BaseHendelse.STATUS_BEHANDLET, captor.getValue().getStatus());
    }

    @Test
    void behandleHendelse_duplicateHendelseId_setsStatusToDuplikat() {
        Livshendelse eksisterende = new Livshendelse();
        eksisterende.setStatus(BaseHendelse.STATUS_BEHANDLET);
        when(livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(anyString(), anyString(), anyList()))
                .thenReturn(List.of(eksisterende));

        livshendelseService.behandleHendelse(buildDto("hendelse-duplikat", List.of("12345678901"), "NAVN_V1"));

        ArgumentCaptor<Livshendelse> captor = ArgumentCaptor.forClass(Livshendelse.class);
        verify(livshendelseRepository).save(captor.capture());
        assertEquals(BaseHendelse.STATUS_DUPLIKAT, captor.getValue().getStatus());
    }

    @Test
    void behandleHendelse_repositoryThrowsDuringDuplicateCheck_setsStatusToRetry() {
        when(livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(anyString(), anyString(), anyList()))
                .thenThrow(new RuntimeException("DB-feil under duplikatsjekk"));

        LivshendelseDto dto = buildDto("hendelse-feil", List.of("12345678901"), "DOEDSFALL_V1");
        assertThrows(HendelseBehandlingException.class,
                () -> livshendelseService.behandleHendelse(dto));

        ArgumentCaptor<Livshendelse> captor = ArgumentCaptor.forClass(Livshendelse.class);
        verify(livshendelseRepository).save(captor.capture());
        assertEquals(BaseHendelse.STATUS_RETRY, captor.getValue().getStatus());
    }

    @Test
    void behandleHendelse_repositoryThrowsDuringDuplicateCheck_setsRetryTellerToMax() {
        when(livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(anyString(), anyString(), anyList()))
                .thenThrow(new RuntimeException("DB-feil"));

        LivshendelseDto dto = buildDto("hendelse-feil-2", List.of("12345678901"), "NAVN_V1");
        assertThrows(HendelseBehandlingException.class,
                () -> livshendelseService.behandleHendelse(dto));

        ArgumentCaptor<Livshendelse> captor = ArgumentCaptor.forClass(Livshendelse.class);
        verify(livshendelseRepository).save(captor.capture());
        assertEquals(5, captor.getValue().getRetryTeller());
    }

    @Test
    void behandleHendelse_repositoryThrowsDuringDuplicateCheck_setsRetryTidspunktInFuture() {
        when(livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(anyString(), anyString(), anyList()))
                .thenThrow(new RuntimeException("DB-feil"));

        LocalDateTime foerKall = LocalDateTime.now();

        LivshendelseDto dto = buildDto("hendelse-retry", List.of("12345678901"), "NAVN_V1");
        assertThrows(HendelseBehandlingException.class,
                () -> livshendelseService.behandleHendelse(dto));

        ArgumentCaptor<Livshendelse> captor = ArgumentCaptor.forClass(Livshendelse.class);
        verify(livshendelseRepository).save(captor.capture());
        assertNotNull(captor.getValue().getRetryTidspunkt());
        assertTrue(captor.getValue().getRetryTidspunkt().isAfter(foerKall));
    }

    @Test
    void behandleHendelse_repositoryUnavailableOnFirstSave_throwsRuntimeException() {
        when(livshendelseRepository.save(any(Livshendelse.class)))
                .thenThrow(new RuntimeException("Oracle nede"));

        LivshendelseDto dto = buildDto("hendelse-db-nede", List.of("12345678901"), "NAVN_V1");
        assertThrows(RuntimeException.class,
                () -> livshendelseService.behandleHendelse(dto));
    }

    @Test
    void behandleHendelse_persistsHendelseIdAndOpplysningstype() {
        when(livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(anyString(), anyString(), anyList()))
                .thenReturn(List.of());

        livshendelseService.behandleHendelse(buildDto("hendelse-felt-test", List.of("12345678901"), "ADRESSEBESKYTTELSE_V1"));

        ArgumentCaptor<Livshendelse> captor = ArgumentCaptor.forClass(Livshendelse.class);
        verify(livshendelseRepository).save(captor.capture());
        assertEquals("hendelse-felt-test", captor.getValue().getHendelseId());
        assertEquals("ADRESSEBESKYTTELSE_V1", captor.getValue().getHendelseOpplysningstype());
    }

    @Test
    void behandleHendelse_elevenCharIdentifiersSortedBeforeLonger() {
        // 11-char = fnr/dnr, should come before 13-char aktorId
        List<String> identifiers = List.of("2222222222222", "11111111111", "3333333333333", "22222222222");
        when(livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(anyString(), anyString(), anyList()))
                .thenReturn(List.of());

        livshendelseService.behandleHendelse(buildDto("hendelse-sort", identifiers, "NAVN_V1"));

        ArgumentCaptor<Livshendelse> captor = ArgumentCaptor.forClass(Livshendelse.class);
        verify(livshendelseRepository).save(captor.capture());
        String personidenter = captor.getValue().getHendelsePersonidenter();
        // 11-char identifiers come first
        assertTrue(personidenter.startsWith("11111111111,22222222222"),
                "11-char identifiers should be first, sorted: " + personidenter);
    }

    @Test
    void behandleHendelse_onlyFnrIdentifiers_noLeadingComma() {
        List<String> identifiers = List.of("12345678901", "98765432100");
        when(livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(anyString(), anyString(), anyList()))
                .thenReturn(List.of());

        livshendelseService.behandleHendelse(buildDto("hendelse-fnr-only", identifiers, "NAVN_V1"));

        ArgumentCaptor<Livshendelse> captor = ArgumentCaptor.forClass(Livshendelse.class);
        verify(livshendelseRepository).save(captor.capture());
        String personidenter = captor.getValue().getHendelsePersonidenter();
        assertFalse(personidenter.startsWith(","), "Should not start with comma");
        assertFalse(personidenter.endsWith(","), "Should not end with comma");
    }

    @Test
    void behandleHendelse_hendelseIdExceedsMaxLength_isTruncated() {
        String longId = "a".repeat(Livshendelse.MAX_ID_LEN + 10);
        when(livshendelseRepository.findByHendelseIdAndHendelseOpplysningstypeAndStatusNotIn(anyString(), anyString(), anyList()))
                .thenReturn(List.of());

        livshendelseService.behandleHendelse(buildDto(longId, List.of("12345678901"), "NAVN_V1"));

        ArgumentCaptor<Livshendelse> captor = ArgumentCaptor.forClass(Livshendelse.class);
        verify(livshendelseRepository).save(captor.capture());
        assertEquals(Livshendelse.MAX_ID_LEN, captor.getValue().getHendelseId().length());
    }

    private LivshendelseDto buildDto(String hendelseId, List<String> personidenter, String opplysningstype) {
        return LivshendelseDto.builder()
                .hendelseId(hendelseId)
                .personidenter(personidenter)
                .master("FREG")
                .opprettet(ZonedDateTime.now())
                .opplysningstype(opplysningstype)
                .endringstype("OPPRETTET")
                .tidligereHendelseId(null)
                .build();
    }
}
