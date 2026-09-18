package edu.uw.edm.pws.exception;

import static org.assertj.core.api.Assertions.assertThat;

import edu.uw.edm.pws.model.PWSError;
import org.junit.jupiter.api.Test;

/**
 * Guards the {@link PWSException} class hierarchy: it must extend {@link Exception} (not bare
 * {@link Throwable}), so that standard exception handling and retry frameworks (e.g. Spring Retry's
 * {@code @Retryable}/{@code RetryTemplate}) work with it without special-casing or wrapping.
 */
class PWSExceptionTest {

    @Test
    void extendsExceptionNotBareThrowable() {
        PWSException exception = new PWSException(new PWSError());

        assertThat(exception).isInstanceOf(Exception.class);
    }

    @Test
    void namedSubclassesAlsoExtendException() {
        PWSError error = new PWSError();

        assertThat(new BadPersonRequestException(error)).isInstanceOf(Exception.class);
        assertThat(new BadSearchPersonRequestException(error)).isInstanceOf(Exception.class);
        assertThat(new PWSAuthenticationException(error)).isInstanceOf(Exception.class);
        assertThat(new NoSuchPersonException(error, "someId")).isInstanceOf(Exception.class);
        assertThat(new UnknownPersonRequestException("message", new RuntimeException()))
                .isInstanceOf(Exception.class);
    }
}
