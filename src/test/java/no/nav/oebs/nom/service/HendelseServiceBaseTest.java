package no.nav.oebs.nom.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import no.nav.oebs.nom.db.entity.NomsHendelse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HendelseServiceBaseTest {

    @Mock
    private ServiceConfig serviceConfig;

    private HendelseServiceBase serviceBase;

    @BeforeEach
    void setUp() {
        serviceBase = new HendelseServiceBase(serviceConfig) {};
        when(serviceConfig.getRetryAttempt1DelayMins()).thenReturn(1);
        when(serviceConfig.getRetryAttemptNDelayMins()).thenReturn(60);
        when(serviceConfig.getRetryMaxAttempts()).thenReturn(5);
    }

    @Test
    void getNextRetryTidspunkt_retryCountBelowMax_usesAttemptNDelay() {
        NomsHendelse hendelse = buildHendelse(3); // 3 < 5 → uses N-delay (60 min)

        LocalDateTime tidspunktFør = LocalDateTime.now();
        LocalDateTime neste = serviceBase.getNextRetryTidspunkt(hendelse);

        assertNotNull(neste);
        assertTrue(neste.isAfter(tidspunktFør.plusMinutes(59)),
                "Should use retryAttemptNDelayMins (60 min) when retryCount < maxAttempts");
    }

    @Test
    void getNextRetryTidspunkt_retryCountEqualToMax_usesAttempt1Delay() {
        NomsHendelse hendelse = buildHendelse(5); // 5 >= 5 → uses attempt1-delay (1 min)

        LocalDateTime tidspunktFør = LocalDateTime.now();
        LocalDateTime neste = serviceBase.getNextRetryTidspunkt(hendelse);

        assertNotNull(neste);
        assertTrue(neste.isAfter(tidspunktFør),
                "Next timestamp should be in the future");
        assertTrue(neste.isBefore(tidspunktFør.plusMinutes(59)),
                "Should use retryAttempt1DelayMins (1 min) when retryCount >= maxAttempts");
    }

    @Test
    void getNextRetryTidspunkt_retryCountAboveMax_usesAttempt1Delay() {
        NomsHendelse hendelse = buildHendelse(10); // 10 >= 5 → uses attempt1-delay (1 min)

        LocalDateTime tidspunktFør = LocalDateTime.now();
        LocalDateTime neste = serviceBase.getNextRetryTidspunkt(hendelse);

        assertTrue(neste.isAfter(tidspunktFør));
        assertTrue(neste.isBefore(tidspunktFør.plusMinutes(59)));
    }

    @Test
    void getNextRetryTidspunkt_returnsTimestampInFuture() {
        NomsHendelse hendelse = buildHendelse(2);

        LocalDateTime neste = serviceBase.getNextRetryTidspunkt(hendelse);

        assertTrue(neste.isAfter(LocalDateTime.now()), "Next retry should be in the future");
    }

    private NomsHendelse buildHendelse(int retryTeller) {
        NomsHendelse hendelse = new NomsHendelse();
        hendelse.setRetryTeller(retryTeller);
        return hendelse;
    }
}
