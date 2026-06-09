package no.nav.oebs.nom.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.oebs.nom.db.entity.NomsHendelse;

@ExtendWith(MockitoExtension.class)
class NomshendelseServiceBaseTest {

    @Mock
    private ServiceConfig serviceConfig;

    private NomshendelseServiceBase serviceBase;

    @BeforeEach
    void setUp() {
        serviceBase = new NomshendelseServiceBase(serviceConfig, new ObjectMapper()) {};
    }

    @Test
    void addHendelseOebsToEntity_trueBooleanString_setsStatusTrueInJson() throws JsonProcessingException {
        NomsHendelse hendelse = buildHendelse("true", "12345678901");

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertNotNull(hendelse.getHendelse());
        assertTrue(hendelse.getHendelse().contains("\"status\":true"),
                "JSON should contain status:true");
    }

    @Test
    void addHendelseOebsToEntity_falseBooleanString_setsStatusFalseInJson() throws JsonProcessingException {
        NomsHendelse hendelse = buildHendelse("false", "12345678901");

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertTrue(hendelse.getHendelse().contains("\"status\":false"),
                "JSON should contain status:false");
    }

    @Test
    void addHendelseOebsToEntity_invalidBooleanString_setsStatusFalse() throws JsonProcessingException {
        // Boolean.parseBoolean returns false for anything other than "true"
        NomsHendelse hendelse = buildHendelse("ugyldig_verdi", "12345678901");

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertTrue(hendelse.getHendelse().contains("\"status\":false"));
    }

    @Test
    void addHendelseOebsToEntity_includesPersonalNumberInJson() throws JsonProcessingException {
        NomsHendelse hendelse = buildHendelse("true", "12345678901");

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertTrue(hendelse.getHendelse().contains("\"fodselsnr\":\"12345678901\""),
                "JSON should contain fodselsnr");
    }

    @Test
    void addHendelseOebsToEntity_overwritesOriginalEventField() throws JsonProcessingException {
        NomsHendelse hendelse = buildHendelse("true", "12345678901");
        String opprinneligVerdi = hendelse.getHendelse();

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertTrue(!hendelse.getHendelse().equals(opprinneligVerdi),
                "Event field should be updated with OEBS JSON");
    }

    private NomsHendelse buildHendelse(String hendelseVerdi, String fodselsnr) {
        NomsHendelse hendelse = new NomsHendelse();
        hendelse.setHendelse(hendelseVerdi);
        hendelse.setHendelseFodselsnr(fodselsnr);
        return hendelse;
    }
}
