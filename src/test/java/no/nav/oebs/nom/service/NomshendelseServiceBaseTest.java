package no.nav.oebs.nom.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.oebs.nom.db.entity.NomsHendelse;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NomshendelseServiceBaseTest {

    @Mock
    private ServiceConfig serviceConfig;

    private NomshendelseServiceBase serviceBase;

    @BeforeEach
    void setUp() {
        serviceBase = new NomshendelseServiceBase(serviceConfig, new JsonMapper()) {};
    }

    @Test
    void addHendelseOebsToEntity_trueBooleanString_setsStatusTrueInJson() {
        NomsHendelse hendelse = buildHendelse("true");

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertNotNull(hendelse.getHendelse());
        assertTrue(hendelse.getHendelse().contains("\"status\":true"),
                "JSON should contain status:true");
    }

    @Test
    void addHendelseOebsToEntity_falseBooleanString_setsStatusFalseInJson(){
        NomsHendelse hendelse = buildHendelse("false");

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertTrue(hendelse.getHendelse().contains("\"status\":false"),
                "JSON should contain status:false");
    }

    @Test
    void addHendelseOebsToEntity_invalidBooleanString_setsStatusFalse() {
        // Boolean.parseBoolean returns false for anything other than "true"
        NomsHendelse hendelse = buildHendelse("ugyldig_verdi");

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertTrue(hendelse.getHendelse().contains("\"status\":false"));
    }

    @Test
    void addHendelseOebsToEntity_includesPersonalNumberInJson()  {
        NomsHendelse hendelse = buildHendelse("true");

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertTrue(hendelse.getHendelse().contains("\"fodselsnr\":\"12345678901\""),
                "JSON should contain fodselsnr");
    }

    @Test
    void addHendelseOebsToEntity_overwritesOriginalEventField() {
        NomsHendelse hendelse = buildHendelse("true");
        String opprinneligVerdi = hendelse.getHendelse();

        serviceBase.addHendelseOebsToEntity(hendelse);

        assertNotEquals(hendelse.getHendelse(), opprinneligVerdi, "Event field should be updated with OEBS JSON");
    }

    private NomsHendelse buildHendelse(String hendelseVerdi) {
        String fodselsnr = "12345678901";
        NomsHendelse hendelse = new NomsHendelse();
        hendelse.setHendelse(hendelseVerdi);
        hendelse.setHendelseFodselsnr(fodselsnr);
        return hendelse;
    }
}
