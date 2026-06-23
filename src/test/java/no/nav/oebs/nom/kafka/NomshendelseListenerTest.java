package no.nav.oebs.nom.kafka;

import static no.nav.oebs.nom.kafka.BaseHendelseListener.STATUS_ERROR;
import static no.nav.oebs.nom.kafka.BaseHendelseListener.STATUS_OK;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import no.nav.oebs.nom.db.entity.Logg;
import no.nav.oebs.nom.db.repository.LoggRepository;
import no.nav.oebs.nom.exception.HendelseBehandlingException;
import no.nav.oebs.nom.kafka.model.NomshendelseDto;
import no.nav.oebs.nom.mdc.MdcOperations;
import no.nav.oebs.nom.service.NomshendelseService;

@ExtendWith(MockitoExtension.class)
class NomshendelseListenerTest {

    @Mock
    private LoggRepository loggRepository;

    @Mock
    private NomshendelseService nomshendelseService;

    private NomshendelseListener listener;

    @BeforeEach
    void setUp() {
        listener = new NomshendelseListener(loggRepository, nomshendelseService);
    }

    @AfterEach
    void cleanUpMdc() {
        MDC.remove(MdcOperations.MDC_CORRELATION_ID);
    }

    @Test
    void listen_validPayload_callsBehandleHendelse() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, "12345678901");

        listener.listen("{\"status\": true}", record);

        ArgumentCaptor<NomshendelseDto> captor = ArgumentCaptor.forClass(NomshendelseDto.class);
        verify(nomshendelseService).behandleHendelse(captor.capture());
        assertEquals("12345678901", captor.getValue().getFodselsnr());
    }

    @Test
    void listen_validPayload_buildsHendelseIdFromTopicPartitionOffset() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 3, 42L, "12345678901");

        listener.listen("{\"status\": true}", record);

        ArgumentCaptor<NomshendelseDto> captor = ArgumentCaptor.forClass(NomshendelseDto.class);
        verify(nomshendelseService).behandleHendelse(captor.capture());
        assertEquals("nom-topic-3-42", captor.getValue().getHendelseId());
    }

    @Test
    void listen_validPayload_setsHendelseAsJsonFromPayload() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, "12345678901");
        String payload = "{\"status\": false}";

        listener.listen(payload, record);

        ArgumentCaptor<NomshendelseDto> captor = ArgumentCaptor.forClass(NomshendelseDto.class);
        verify(nomshendelseService).behandleHendelse(captor.capture());
        assertEquals(payload, captor.getValue().getHendelseAsJson());
    }

    @Test
    void listen_nullKafkaKey_setsFodselsnrToNull() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, null);

        listener.listen("{}", record);

        ArgumentCaptor<NomshendelseDto> captor = ArgumentCaptor.forClass(NomshendelseDto.class);
        verify(nomshendelseService).behandleHendelse(captor.capture());
        assertEquals(null, captor.getValue().getFodselsnr());
    }

    @Test
    void listen_hendelseBehandlingException_doesNotRethrow() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, "12345678901");
        doThrow(new HendelseBehandlingException(new RuntimeException("behandlingsfeil")))
                .when(nomshendelseService).behandleHendelse(any());

        assertDoesNotThrow(() -> listener.listen("{}", record));
    }

    @Test
    void listen_hendelseBehandlingException_stillLogsToLogg() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, "12345678901");
        doThrow(new HendelseBehandlingException(new RuntimeException("behandlingsfeil")))
                .when(nomshendelseService).behandleHendelse(any());

        listener.listen("{}", record);

        ArgumentCaptor<Logg> captor = ArgumentCaptor.forClass(Logg.class);
        verify(loggRepository).save(captor.capture());
        assertEquals(STATUS_ERROR, captor.getValue().getStatus());
    }

    @Test
    void listen_unexpectedException_rethrowsException() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, "12345678901");
        doThrow(new RuntimeException("Uventet systemfeil"))
                .when(nomshendelseService).behandleHendelse(any());

        assertThrows(RuntimeException.class, () -> listener.listen("{}", record));
    }

    @Test
    void listen_unexpectedException_stillLogsToLogg() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, "12345678901");
        doThrow(new RuntimeException("Uventet systemfeil"))
                .when(nomshendelseService).behandleHendelse(any());

        try {
            listener.listen("{}", record);
        } catch (RuntimeException ignored) {}

        ArgumentCaptor<Logg> captor = ArgumentCaptor.forClass(Logg.class);
        verify(loggRepository).save(captor.capture());
        assertEquals(STATUS_ERROR, captor.getValue().getStatus());
    }

    @Test
    void listen_successfulProcessing_logsStatusOkToLogg() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, "12345678901");

        listener.listen("{}", record);

        ArgumentCaptor<Logg> captor = ArgumentCaptor.forClass(Logg.class);
        verify(loggRepository).save(captor.capture());
        assertEquals(STATUS_OK, captor.getValue().getStatus());
    }

    @Test
    void listen_clearsCorrelationIdFromMdcAfterProcessing() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, "12345678901");

        listener.listen("{}", record);

        assertEquals(null, MDC.get(MdcOperations.MDC_CORRELATION_ID),
                "Correlation ID should be removed from MDC after processing");
    }

    @Test
    void listen_clearsCorrelationIdFromMdcEvenOnException() {
        ConsumerRecord<String, String> record = buildRecord("nom-topic", 0, 1L, "12345678901");
        doThrow(new HendelseBehandlingException(new RuntimeException("feil")))
                .when(nomshendelseService).behandleHendelse(any());

        listener.listen("{}", record);

        assertEquals(null, MDC.get(MdcOperations.MDC_CORRELATION_ID));
    }

    private ConsumerRecord<String, String> buildRecord(String topic, int partition, long offset, String key) {
        return new ConsumerRecord<>(topic, partition, offset, key, "{}");
    }
}
