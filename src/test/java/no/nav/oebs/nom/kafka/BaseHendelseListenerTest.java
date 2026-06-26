package no.nav.oebs.nom.kafka;

import static no.nav.oebs.nom.kafka.BaseHendelseListener.STATUS_ERROR;
import static no.nav.oebs.nom.kafka.BaseHendelseListener.STATUS_OK;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

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
import no.nav.oebs.nom.mdc.MdcOperations;

@ExtendWith(MockitoExtension.class)
class BaseHendelseListenerTest {

    @Mock
    private LoggRepository loggRepository;

    private TestableBaseHendelseListener listener;

    @BeforeEach
    void setUp() {
        listener = new TestableBaseHendelseListener(loggRepository);
    }

    @AfterEach
    void cleanUpMdc() {
        MDC.remove(MdcOperations.MDC_CORRELATION_ID);
    }

    @Test
    void epochToLocalDateTime_validEpochMs_returnsCorrectDate() {
        long epochMs = 1704067200000L; // 2024-01-01T00:00:00 UTC

        LocalDateTime result = listener.testEpochToLocalDateTime(epochMs);

        assertNotNull(result);
        assertEquals(2024, result.getYear());
        assertEquals(1, result.getMonthValue());
        assertEquals(1, result.getDayOfMonth());
    }

    @Test
    void epochToLocalDateTime_zeroEpoch_returnsEpochStartPoint() {
        LocalDateTime result = listener.testEpochToLocalDateTime(0L);

        assertNotNull(result);
        assertEquals(1970, result.getYear());
    }

    @Test
    void generateAndSetCorrelationId_setsCorrelationIdOnMdc() {
        String correlationId = listener.testGenerateAndSetCorrelationId();

        assertNotNull(correlationId);
        assertEquals(correlationId, MDC.get(MdcOperations.MDC_CORRELATION_ID));
    }

    @Test
    void generateAndSetCorrelationId_generatesIdsWithExpectedFormat() {
        String id1 = listener.testGenerateAndSetCorrelationId();
        String id2 = listener.testGenerateAndSetCorrelationId();

        assertNotNull(id1);
        assertNotNull(id2);
        // Format: TIMESTAMP-RANDOMNUMBER — to kall tett i tid kan ha samme timestamp,
        // men ulik random-del gjør dem unike
        assertTrue(id1.contains("-"));
        assertTrue(id2.contains("-"));
    }

    @Test
    void logToNomsLogg_successStatus_savesLogEntryWithCorrectFields() {
        ConsumerRecord<String, String> conRecord = buildConsumerRecord("nom.topic", 2, 42L, "key");
        String korrelasjonId = "korr-test-123";
        long startTime = System.currentTimeMillis();

        listener.testLogToLogg(korrelasjonId, STATUS_OK, startTime, "{hendelse:true}", conRecord, null);

        ArgumentCaptor<Logg> captor = ArgumentCaptor.forClass(Logg.class);
        verify(loggRepository).save(captor.capture());
        Logg lagret = captor.getValue();

        assertEquals(korrelasjonId, lagret.getKorrelasjonId());
        assertEquals(STATUS_OK, lagret.getStatus());
        assertEquals(Logg.TYPE_KAFKA, lagret.getType());
        assertEquals(Logg.RETNING_INN, lagret.getKallRetning());
        assertEquals("nom.topic", lagret.getOperation());
        assertEquals(2, lagret.getKafkaPartition());
        assertEquals(42L, lagret.getKafkaOffset());
        assertEquals("{hendelse:true}", lagret.getRequest());
        assertNull(lagret.getLogginfo());
    }

    @Test
    void logToLogg_errorStatus_savesWithExceptionMessage() {
        ConsumerRecord<String, String> conRecord = buildConsumerRecord("nom.topic", 0, 1L, "key");
        RuntimeException feil = new RuntimeException("Behandlingsfeil");

        listener.testLogToLogg("id", STATUS_ERROR, System.currentTimeMillis(), "{}", conRecord, feil);

        ArgumentCaptor<Logg> captor = ArgumentCaptor.forClass(Logg.class);
        verify(loggRepository).save(captor.capture());
        assertNotNull(captor.getValue().getLogginfo(), "Logginfo should contain exception details");
        assertTrue(captor.getValue().getLogginfo().contains("Behandlingsfeil"));
    }

    @Test
    void logToLogg_repositoryThrowsException_exceptionIsCaught() {
        ConsumerRecord<String, String> conRecord = buildConsumerRecord("nom.topic", 0, 0L, "key");
        when(loggRepository.save(any())).thenThrow(new RuntimeException("DB nede"));

        assertDoesNotThrow(() ->
                listener.testLogToLogg("id", STATUS_OK, System.currentTimeMillis(), "{}", conRecord, null));
    }

    @Test
    void logToLogg_kafkaKeyExceedsMaxLength_isTrimmedToMaxLength() {
        String longKey = "x".repeat(Logg.MAX_KAFKA_KEY_LEN + 50);
        ConsumerRecord<String, String> conRecord = buildConsumerRecord("topic", 0, 0L, longKey);

        listener.testLogToLogg("id", STATUS_OK, System.currentTimeMillis(), "{}", conRecord, null);

        ArgumentCaptor<Logg> captor = ArgumentCaptor.forClass(Logg.class);
        verify(loggRepository).save(captor.capture());
        assertEquals(Logg.MAX_KAFKA_KEY_LEN, captor.getValue().getKafkaKey().length());
    }

    @Test
    void logToLogg_nullKafkaKey_savedWithoutError() {
        ConsumerRecord<String, String> conRecord = buildConsumerRecord("topic", 0, 0L, null);

        listener.testLogToLogg("id", STATUS_OK, System.currentTimeMillis(), "{}", conRecord, null);

        ArgumentCaptor<Logg> captor = ArgumentCaptor.forClass(Logg.class);
        verify(loggRepository).save(captor.capture());
        assertNull(captor.getValue().getKafkaKey());
    }

    private ConsumerRecord<String, String> buildConsumerRecord(String topic, int partition, long offset, String key) {
        return new ConsumerRecord<>(topic, partition, offset, key, "{}");
    }

    /** Konkret subklasse for å eksponere protected metoder i tester */
    static class TestableBaseHendelseListener extends BaseHendelseListener {

        TestableBaseHendelseListener(LoggRepository repo) {
            super(repo);
        }

        LocalDateTime testEpochToLocalDateTime(long epoch) {
            return epochToLocalDateTime(epoch);
        }

        String testGenerateAndSetCorrelationId() {
            return generateAndSetCorrelationId();
        }

        void testLogToLogg(String korrelasjonId, int status, long startTime,
                String message, ConsumerRecord<?, ?> conRecord, Exception exception) {
            logToLogg(korrelasjonId, status, startTime, message, conRecord, exception);
        }
    }
}
