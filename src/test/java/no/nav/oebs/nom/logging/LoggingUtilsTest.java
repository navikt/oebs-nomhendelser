package no.nav.oebs.nom.logging;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LoggingUtilsTest {

    @Test
    void formatExceptionAsString_nullException_returnsNull() {
        assertNull(LoggingUtils.formatExceptionAsString(null));
    }

    @Test
    void formatExceptionAsString_exceptionWithMessage_returnsStringContainingMessage() {
        RuntimeException exception = new RuntimeException("Something went wrong");

        String result = LoggingUtils.formatExceptionAsString(exception);

        assertNotNull(result);
        assertTrue(result.contains("Something went wrong"));
    }

    @Test
    void formatExceptionAsString_exception_returnsStringContainingClassName() {
        IllegalArgumentException exception = new IllegalArgumentException("bad input");

        String result = LoggingUtils.formatExceptionAsString(exception);

        assertTrue(result.contains("IllegalArgumentException"));
    }

    @Test
    void formatExceptionAsString_exceptionWithCause_returnsStringContainingCause() {
        RuntimeException cause = new RuntimeException("root cause");
        RuntimeException exception = new RuntimeException("wrapper", cause);

        String result = LoggingUtils.formatExceptionAsString(exception);

        assertTrue(result.contains("root cause"), "Result should contain the cause message");
    }

    @Test
    void formatExceptionAsString_exception_returnsStringContainingStackTrace() {
        RuntimeException exception = new RuntimeException("error");

        String result = LoggingUtils.formatExceptionAsString(exception);

        assertTrue(result.contains("LoggingUtilsTest"), "Stack trace should reference the calling class");
    }
}

