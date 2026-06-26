package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;

class NomshendelseDtoTest {

    @Test
    void builder_withAllFields_setsAllFields() {
        LocalDateTime timestamp = LocalDateTime.of(2024, Month.JANUARY, 15, 10, 30);

        NomshendelseDto dto = NomshendelseDto.builder()
                .hendelseId("topic-0-42")
                .hendelseTimestamp(timestamp)
                .fodselsnr("12345678901")
                .hendelseAsJson("{\"status\": true}")
                .build();

        assertNotNull(dto);
        assertEquals("topic-0-42", dto.getHendelseId());
        assertEquals(timestamp, dto.getHendelseTimestamp());
        assertEquals("12345678901", dto.getFodselsnr());
        assertEquals("{\"status\": true}", dto.getHendelseAsJson());
    }

    @Test
    void builder_withNoFields_createsObjectWithNullFields() {
        NomshendelseDto dto = NomshendelseDto.builder().build();

        assertNotNull(dto);
        assertNull(dto.getHendelseId());
        assertNull(dto.getHendelseTimestamp());
        assertNull(dto.getFodselsnr());
        assertNull(dto.getHendelseAsJson());
    }
}
