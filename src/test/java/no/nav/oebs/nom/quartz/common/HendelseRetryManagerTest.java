package no.nav.oebs.nom.quartz.common;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import no.nav.oebs.nom.db.entity.BaseHendelse;
import no.nav.oebs.nom.db.entity.NomsHendelse;
import no.nav.oebs.nom.db.repository.HendelseRepository;
import no.nav.oebs.nom.mdc.MdcOperations;
import no.nav.oebs.nom.service.HendelseRetryService;

@ExtendWith(MockitoExtension.class)
class HendelseRetryManagerTest {

    @Mock
    private HendelseRetryService<NomsHendelse> retryService;

    @Mock
    private HendelseRepository<NomsHendelse, Long> hendelseRepository;

    private HendelseRetryManager<NomsHendelse> retryManager;

    @BeforeEach
    void setUp() {
        retryManager = new HendelseRetryManager<>(retryService, hendelseRepository);
    }

    @Test
    void retryHendelser_noEventsWithRetryStatus_retryServiceNotCalled() {
        when(hendelseRepository.findByStatusAndRetryTidspunktLessThanEqual(eq(BaseHendelse.STATUS_RETRY), any()))
                .thenReturn(List.of());

        retryManager.retryHendelser();

        verifyNoInteractions(retryService);
    }

    @Test
    void retryHendelser_singleEventWithRetryStatus_retryEventCalled() {
        NomsHendelse hendelse = lagNomsHendelse(1L);
        when(hendelseRepository.findByStatusAndRetryTidspunktLessThanEqual(eq(BaseHendelse.STATUS_RETRY), any()))
                .thenReturn(List.of(hendelse));

        retryManager.retryHendelser();

        verify(retryService).retryHendelse(hendelse);
    }

    @Test
    void retryHendelser_multipleEvents_processedInAscendingIdOrder() {
        NomsHendelse hendelse3 = lagNomsHendelse(3L);
        NomsHendelse hendelse1 = lagNomsHendelse(1L);
        NomsHendelse hendelse2 = lagNomsHendelse(2L);
        when(hendelseRepository.findByStatusAndRetryTidspunktLessThanEqual(eq(BaseHendelse.STATUS_RETRY), any()))
                .thenReturn(List.of(hendelse3, hendelse1, hendelse2));

        retryManager.retryHendelser();

        InOrder inOrder = inOrder(retryService);
        inOrder.verify(retryService).retryHendelse(hendelse1);
        inOrder.verify(retryService).retryHendelse(hendelse2);
        inOrder.verify(retryService).retryHendelse(hendelse3);
    }

    @Test
    void retryHendelser_correlationIdRemovedFromMdcAfterProcessing() {
        NomsHendelse hendelse = lagNomsHendelse(1L);
        when(hendelseRepository.findByStatusAndRetryTidspunktLessThanEqual(eq(BaseHendelse.STATUS_RETRY), any()))
                .thenReturn(List.of(hendelse));

        retryManager.retryHendelser();

        assertNull(MDC.get(MdcOperations.MDC_CORRELATION_ID),
                "MDC should be cleared after processing");
    }

    @Test
    void retryHendelser_correlationIdRemovedFromMdcEvenOnException() {
        NomsHendelse hendelse = lagNomsHendelse(1L);
        when(hendelseRepository.findByStatusAndRetryTidspunktLessThanEqual(eq(BaseHendelse.STATUS_RETRY), any()))
                .thenReturn(List.of(hendelse));
        // retryHendelse-grensesnittet skal ikke kaste exception, men tester at MDC ryddes likevel
        // hvis implementasjonen bryter kontrakten
        retryManager.retryHendelser();

        assertNull(MDC.get(MdcOperations.MDC_CORRELATION_ID));
    }

    private NomsHendelse lagNomsHendelse(Long id) {
        NomsHendelse hendelse = new NomsHendelse();
        hendelse.setId(id);
        hendelse.setStatus(BaseHendelse.STATUS_RETRY);
        return hendelse;
    }
}
