package no.nav.oebs.nom.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.oebs.nom.db.entity.BaseHendelse;
import no.nav.oebs.nom.db.entity.NomsHendelse;
import no.nav.oebs.nom.db.repository.NomshendelseRepository;
import no.nav.oebs.nom.exception.HendelseBehandlingException;
import no.nav.oebs.nom.kafka.nomshendelse.model.NomshendelseDto;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NomshendelseServiceTest {

    @Mock
    private NomshendelseRepository nomshendelseRepository;

    @Mock
    private ServiceConfig serviceConfig;

    private NomshendelseService nomshendelseService;

    @BeforeEach
    void setUp() {
        nomshendelseService = new NomshendelseService(serviceConfig, nomshendelseRepository, new ObjectMapper());

        when(serviceConfig.getRetryMaxAttempts()).thenReturn(5);
        when(serviceConfig.getRetryAttempt1DelayMins()).thenReturn(1);
        when(serviceConfig.getRetryAttemptNDelayMins()).thenReturn(60);

        // Returner samme entity som ble sendt inn, slik at status kan endres av service
        when(nomshendelseRepository.save(any(NomsHendelse.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void behandleHendelse_newEvent_setsStatusToProcessed() {
        when(nomshendelseRepository.findByHendelseIdAndStatusNotIn(any(), any()))
                .thenReturn(List.of());

        nomshendelseService.behandleHendelse(buildDto("topic-0-1", "true"));

        ArgumentCaptor<NomsHendelse> captor = ArgumentCaptor.forClass(NomsHendelse.class);
        verify(nomshendelseRepository).save(captor.capture());
        assertEquals(BaseHendelse.STATUS_BEHANDLET, captor.getValue().getStatus());
    }

    @Test
    void behandleHendelse_newEvent_updatesEventFieldWithOebsJson() {
        when(nomshendelseRepository.findByHendelseIdAndStatusNotIn(any(), any()))
                .thenReturn(List.of());

        nomshendelseService.behandleHendelse(buildDto("topic-0-1", "true"));

        ArgumentCaptor<NomsHendelse> captor = ArgumentCaptor.forClass(NomsHendelse.class);
        verify(nomshendelseRepository).save(captor.capture());
        // Etter behandling skal hendelse-feltet inneholde JSON med fodselsnr og status
        String hendelseJson = captor.getValue().getHendelse();
        assertNotNull(hendelseJson);
        assertTrue(hendelseJson.contains("fodselsnr"));
    }

    @Test
    void behandleHendelse_duplicateEventId_setsStatusToDuplicate() {
        NomsHendelse tidligereHendelse = new NomsHendelse();
        tidligereHendelse.setStatus(BaseHendelse.STATUS_BEHANDLET);
        when(nomshendelseRepository.findByHendelseIdAndStatusNotIn(eq("topic-0-1"), any()))
                .thenReturn(List.of(tidligereHendelse));

        nomshendelseService.behandleHendelse(buildDto("topic-0-1", "true"));

        ArgumentCaptor<NomsHendelse> captor = ArgumentCaptor.forClass(NomsHendelse.class);
        verify(nomshendelseRepository).save(captor.capture());
        assertEquals(BaseHendelse.STATUS_DUPLIKAT, captor.getValue().getStatus());
    }

    @Test
    void behandleHendelse_processingFailure_throwsHendelseBehandlingException() {
        when(nomshendelseRepository.findByHendelseIdAndStatusNotIn(any(), any()))
                .thenReturn(List.of());

        // Ugyldig hendelse-innhold som ikke kan behandles (ikke en boolean-verdi, men et JSON-objekt)
        // vil ikke feile i seg selv siden Boolean.parseBoolean tolererer alt, men vi kan simulere
        // feil ved å kaste exception fra repository etter første save
        when(nomshendelseRepository.findByHendelseIdAndStatusNotIn(any(), any()))
                .thenThrow(new RuntimeException("Databasefeil"));

        assertThrows(HendelseBehandlingException.class,
                () -> nomshendelseService.behandleHendelse(buildDto("topic-0-2", "true")));
    }

    @Test
    void behandleHendelse_processingFailure_setsStatusToRetry() {
        when(nomshendelseRepository.findByHendelseIdAndStatusNotIn(any(), any()))
                .thenThrow(new RuntimeException("Uventet feil"));

        ArgumentCaptor<NomsHendelse> captor = ArgumentCaptor.forClass(NomsHendelse.class);

        assertThrows(HendelseBehandlingException.class,
                () -> nomshendelseService.behandleHendelse(buildDto("topic-0-3", "true")));

        verify(nomshendelseRepository).save(captor.capture());
        assertEquals(BaseHendelse.STATUS_RETRY, captor.getValue().getStatus());
    }

    @Test
    void behandleHendelse_processingFailure_setsRetryCountToMaxAttempts() {
        when(nomshendelseRepository.findByHendelseIdAndStatusNotIn(any(), any()))
                .thenThrow(new RuntimeException("Feil"));

        ArgumentCaptor<NomsHendelse> captor = ArgumentCaptor.forClass(NomsHendelse.class);
        assertThrows(HendelseBehandlingException.class,
                () -> nomshendelseService.behandleHendelse(buildDto("topic-0-4", "true")));

        verify(nomshendelseRepository).save(captor.capture());
        assertEquals(5, captor.getValue().getRetryTeller());
    }

    @Test
    void behandleHendelse_processingFailure_setsRetryTimestampInFuture() {
        when(nomshendelseRepository.findByHendelseIdAndStatusNotIn(any(), any()))
                .thenThrow(new RuntimeException("Feil"));

        LocalDateTime tidspunktFørKall = LocalDateTime.now();
        ArgumentCaptor<NomsHendelse> captor = ArgumentCaptor.forClass(NomsHendelse.class);
        assertThrows(HendelseBehandlingException.class,
                () -> nomshendelseService.behandleHendelse(buildDto("topic-0-5", "true")));

        verify(nomshendelseRepository).save(captor.capture());
        assertNotNull(captor.getValue().getRetryTidspunkt());
        assertTrue(captor.getValue().getRetryTidspunkt().isAfter(tidspunktFørKall));
    }

    @Test
    void behandleHendelse_databaseUnavailableOnFirstSave_throwsRuntimeException() {
        when(nomshendelseRepository.save(any()))
                .thenThrow(new RuntimeException("Oracle nede"));

        // Når DB er nede ved første save, skal hendelsen rulles tilbake til topic (ikke RETRY)
        assertThrows(RuntimeException.class,
                () -> nomshendelseService.behandleHendelse(buildDto("topic-0-6", "true")));
    }

    @Test
    void behandleHendelse_persistsEventWithCorrectEventIdAndPersonalNumber() {
        when(nomshendelseRepository.findByHendelseIdAndStatusNotIn(any(), any()))
                .thenReturn(List.of());

        nomshendelseService.behandleHendelse(buildDto("topic-0-99", "false"));

        ArgumentCaptor<NomsHendelse> captor = ArgumentCaptor.forClass(NomsHendelse.class);
        verify(nomshendelseRepository).save(captor.capture());
        assertEquals("topic-0-99", captor.getValue().getHendelseId());
        assertEquals("12345678901", captor.getValue().getHendelseFodselsnr());
    }

    private NomshendelseDto buildDto(String hendelseId, String hendelseJson) {
        return NomshendelseDto.builder()
                .hendelseId(hendelseId)
                .hendelseTimestamp(LocalDateTime.now())
                .fodselsnr("12345678901")
                .hendelseAsJson(hendelseJson)
                .build();
    }
}
