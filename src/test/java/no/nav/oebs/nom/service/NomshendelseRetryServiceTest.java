package no.nav.oebs.nom.service;

import static no.nav.oebs.nom.db.entity.BaseHendelse.STATUS_BEHANDLET;
import static no.nav.oebs.nom.db.entity.BaseHendelse.STATUS_ERSTATTET;
import static no.nav.oebs.nom.db.entity.BaseHendelse.STATUS_FEILET;
import static no.nav.oebs.nom.db.entity.BaseHendelse.STATUS_RETRY;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.MDC;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.oebs.nom.db.entity.NomsHendelse;
import no.nav.oebs.nom.db.repository.NomshendelseRepository;
import no.nav.oebs.nom.mdc.MdcOperations;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NomshendelseRetryServiceTest {

    @Mock
    private NomshendelseRepository nomshendelseRepository;

    @Mock
    private ServiceConfig serviceConfig;

    private NomshendelseRetryService retryService;

    @BeforeEach
    void setUp() {
        retryService = new NomshendelseRetryService(serviceConfig, nomshendelseRepository, new ObjectMapper());
        when(serviceConfig.getRetryAttempt1DelayMins()).thenReturn(1);
        when(serviceConfig.getRetryAttemptNDelayMins()).thenReturn(60);
        when(serviceConfig.getRetryMaxAttempts()).thenReturn(5);

        MDC.put(MdcOperations.MDC_CORRELATION_ID, "test-korrelasjon-id");
    }

    @AfterEach
    void cleanUpMdc() {
        MDC.remove(MdcOperations.MDC_CORRELATION_ID);
    }

    @Test
    void retryHendelse_newerEventExistsForSamePersonalNumber_setsStatusReplaced() {
        NomsHendelse hendelse = buildHendelse(1L, 3);
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                eq("12345678901"), eq(1L), any()))
                .thenReturn(List.of(new NomsHendelse()));

        retryService.retryHendelse(hendelse);

        assertEquals(STATUS_ERSTATTET, hendelse.getStatus());
    }

    @Test
    void retryHendelse_noNewerEvent_setsStatusProcessed() {
        NomsHendelse hendelse = buildHendelse(1L, 3);
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                any(), any(), any()))
                .thenReturn(List.of());

        retryService.retryHendelse(hendelse);

        assertEquals(STATUS_BEHANDLET, hendelse.getStatus());
    }

    @Test
    void retryHendelse_noNewerEvent_decrementsRetryCount() {
        NomsHendelse hendelse = buildHendelse(1L, 3);
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                any(), any(), any()))
                .thenReturn(List.of());

        retryService.retryHendelse(hendelse);

        assertEquals(2, hendelse.getRetryTeller(), "RetryCount should be decremented by 1");
    }

    @Test
    void retryHendelse_noNewerEvent_setsCorrelationIdFromMdc() {
        NomsHendelse hendelse = buildHendelse(1L, 3);
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                any(), any(), any()))
                .thenReturn(List.of());

        retryService.retryHendelse(hendelse);

        assertEquals("test-korrelasjon-id", hendelse.getKorrelasjonId());
    }

    @Test
    void retryHendelse_exceptionWithPositiveRetryCount_setsNewRetryTimestamp() {
        NomsHendelse hendelse = buildHendelse(1L, 3);
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                any(), any(), any()))
                .thenThrow(new RuntimeException("Databasefeil"));

        LocalDateTime tidspunktFørKall = LocalDateTime.now();
        retryService.retryHendelse(hendelse);

        assertNotNull(hendelse.getRetryTidspunkt());
        assertTrue(hendelse.getRetryTidspunkt().isAfter(tidspunktFørKall),
                "RetryTimestamp should be set in the future");
    }

    @Test
    void retryHendelse_exceptionWithZeroRetryCount_setsStatusFailed() {
        NomsHendelse hendelse = buildHendelse(1L, 0);
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                any(), any(), any()))
                .thenThrow(new RuntimeException("Feil"));

        retryService.retryHendelse(hendelse);

        assertEquals(STATUS_FEILET, hendelse.getStatus());
    }

    @Test
    void retryHendelse_exceptionWithNegativeRetryCount_setsStatusFailed() {
        NomsHendelse hendelse = buildHendelse(1L, -1);
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                any(), any(), any()))
                .thenThrow(new RuntimeException("Feil"));

        retryService.retryHendelse(hendelse);

        assertEquals(STATUS_FEILET, hendelse.getStatus());
    }

    @Test
    void retryHendelse_exception_appendsErrorInformation() {
        NomsHendelse hendelse = buildHendelse(1L, 3);
        hendelse.setFeilinformasjon("Tidligere feil");
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                any(), any(), any()))
                .thenThrow(new RuntimeException("Ny feil"));

        retryService.retryHendelse(hendelse);

        assertNotNull(hendelse.getFeilinformasjon());
        assertTrue(hendelse.getFeilinformasjon().contains("Ny feil"),
                "Error information should contain new error message");
    }

    @Test
    void retryHendelse_neverThrowsException() {
        // Contract from HendelseRetryService: method must never throw exceptions
        NomsHendelse hendelse = buildHendelse(1L, 3);
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                any(), any(), any()))
                .thenThrow(new RuntimeException("Uventet feil"));

        assertDoesNotThrow(() -> retryService.retryHendelse(hendelse));
    }

    @Test
    void retryHendelse_replacedByNewerEvent_setsStatusReplaced() {
        // Verify that status is set to REPLACED — not RETRY
        NomsHendelse hendelse = buildHendelse(1L, 3);
        hendelse.setStatus(STATUS_RETRY);
        when(nomshendelseRepository.findByHendelseFodselsnrAndIdGreaterThanAndStatusNotIn(
                any(), any(), any()))
                .thenReturn(List.of(new NomsHendelse()));

        retryService.retryHendelse(hendelse);

        assertEquals(STATUS_ERSTATTET, hendelse.getStatus());
    }

    private NomsHendelse buildHendelse(Long id, int retryTeller) {
        NomsHendelse hendelse = new NomsHendelse();
        hendelse.setId(id);
        hendelse.setRetryTeller(retryTeller);
        hendelse.setHendelseFodselsnr("12345678901");
        hendelse.setHendelse("true");
        hendelse.setStatus(STATUS_RETRY);
        return hendelse;
    }
}
