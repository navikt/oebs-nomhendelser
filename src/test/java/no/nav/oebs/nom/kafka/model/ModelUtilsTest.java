package no.nav.oebs.nom.kafka.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.avro.util.Utf8;
import org.junit.jupiter.api.Test;

class ModelUtilsTest {

    @Test
    void getAsString_nullInput_returnsNull() {
        assertNull(ModelUtils.getAsString(null));
    }

    @Test
    void getAsString_stringInput_returnsString() {
        assertEquals("hello", ModelUtils.getAsString("hello"));
    }

    @Test
    void getAsString_avroUtf8Input_returnsString() {
        assertEquals("avro-string", ModelUtils.getAsString(new Utf8("avro-string")));
    }
}
