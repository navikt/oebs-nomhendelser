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
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.apache.avro.util.Utf8;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.test.util.ReflectionTestUtils;

import no.nav.oebs.nom.db.entity.Logg;
import no.nav.oebs.nom.db.repository.LoggRepository;
import no.nav.oebs.nom.exception.HendelseBehandlingException;
import no.nav.oebs.nom.kafka.model.LivshendelseDto;
import no.nav.oebs.nom.mdc.MdcOperations;
import no.nav.oebs.nom.service.LivshendelseService;
import no.nav.person.pdl.leesah.Endringstype;
import no.nav.person.pdl.leesah.Personhendelse;

@ExtendWith(MockitoExtension.class)
class LivshendelseListenerTest {

    @Mock
    private LoggRepository loggRepository;

    @Mock
    private LivshendelseService livshendelseService;

    @Mock
    private Personhendelse personhendelse;

    private LivshendelseListener listener;

    @BeforeEach
    void setUp() {
        listener = new LivshendelseListener(loggRepository, livshendelseService);
        ReflectionTestUtils.setField(listener, "isDebugMode", false);
        configurePersonhendelse();
    }

    @AfterEach
    void cleanUpMdc() {
        MDC.remove(MdcOperations.MDC_CORRELATION_ID);
    }

    @Test
    void listen_validHendelse_callsBehandleHendelse() {
        ConsumerRecord<String, Personhendelse> conRecord = buildRecord();

        listener.listen(personhendelse, conRecord);

        verify(livshendelseService).behandleHendelse(any(LivshendelseDto.class));
    }

    @Test
    void listen_hendelseBehandlingException_doesNotRethrow() {
        ConsumerRecord<String, Personhendelse> conRecord = buildRecord();
        doThrow(new HendelseBehandlingException(new RuntimeException("behandlingsfeil")))
                .when(livshendelseService).behandleHendelse(any());

        assertDoesNotThrow(() -> listener.listen(personhendelse, conRecord));
    }

    @Test
    void listen_unexpectedException_rethrowsException() {
        ConsumerRecord<String, Personhendelse> conRecord = buildRecord();
        doThrow(new RuntimeException("Uventet feil"))
                .when(livshendelseService).behandleHendelse(any());

        assertThrows(RuntimeException.class, () -> listener.listen(personhendelse, conRecord));
    }

    @Test
    void listen_debugModeFalse_doesNotLogToLogg() {
        ReflectionTestUtils.setField(listener, "isDebugMode", false);
        ConsumerRecord<String, Personhendelse> conRecord = buildRecord();

        listener.listen(personhendelse, conRecord);

        verify(loggRepository, never()).save(any());
    }

    @Test
    void listen_debugModeTrue_logsToLogg() {
        ReflectionTestUtils.setField(listener, "isDebugMode", true);
        when(personhendelse.toString()).thenReturn("personhendelse-debug-string");
        ConsumerRecord<String, Personhendelse> conRecord = buildRecord();

        listener.listen(personhendelse, conRecord);

        verify(loggRepository).save(any(Logg.class));
    }

    @Test
    void listen_debugModeTrueWithSuccessfulProcessing_logsStatusOk() {
        ReflectionTestUtils.setField(listener, "isDebugMode", true);
        when(personhendelse.toString()).thenReturn("personhendelse-string");
        ConsumerRecord<String, Personhendelse> conRecord = buildRecord();

        listener.listen(personhendelse, conRecord);

        org.mockito.ArgumentCaptor<Logg> captor = org.mockito.ArgumentCaptor.forClass(Logg.class);
        verify(loggRepository).save(captor.capture());
        assertEquals(STATUS_OK, captor.getValue().getStatus());
    }

    @Test
    void listen_debugModeTrueWithHendelseBehandlingException_logsStatusError() {
        ReflectionTestUtils.setField(listener, "isDebugMode", true);
        when(personhendelse.toString()).thenReturn("personhendelse-string");
        ConsumerRecord<String, Personhendelse> conRecord = buildRecord();
        doThrow(new HendelseBehandlingException(new RuntimeException("feil")))
                .when(livshendelseService).behandleHendelse(any());

        listener.listen(personhendelse, conRecord);

        org.mockito.ArgumentCaptor<Logg> captor = org.mockito.ArgumentCaptor.forClass(Logg.class);
        verify(loggRepository).save(captor.capture());
        assertEquals(STATUS_ERROR, captor.getValue().getStatus());
    }

    @Test
    void listen_clearsCorrelationIdFromMdcAfterProcessing() {
        ConsumerRecord<String, Personhendelse> conRecord = buildRecord();

        listener.listen(personhendelse, conRecord);

        Assertions.assertNull(MDC.get(MdcOperations.MDC_CORRELATION_ID),
                "Correlation ID should be removed from MDC after processing");
    }

    @Test
    void listen_clearsCorrelationIdFromMdcEvenOnHendelseBehandlingException() {
        ConsumerRecord<String, Personhendelse> conRecord = buildRecord();
        doThrow(new HendelseBehandlingException(new RuntimeException("feil")))
                .when(livshendelseService).behandleHendelse(any());

        listener.listen(personhendelse, conRecord);

        Assertions.assertNull(MDC.get(MdcOperations.MDC_CORRELATION_ID));
    }

    private void configurePersonhendelse() {
        when(personhendelse.getHendelseId()).thenReturn(new Utf8("test-hendelse-id"));
        when(personhendelse.getPersonidenter()).thenReturn(List.of(new Utf8("12345678901")));
        when(personhendelse.getMaster()).thenReturn(new Utf8("FREG"));
        when(personhendelse.getOpprettet()).thenReturn(Instant.now());
        when(personhendelse.getOpplysningstype()).thenReturn(new Utf8("NAVN_V1"));
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
    }

    private ConsumerRecord<String, Personhendelse> buildRecord() {
        return new ConsumerRecord<>("pdl-topic", 0, 1L, "key", null);
    }
}
