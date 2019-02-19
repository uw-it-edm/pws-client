package edu.uw.edm.pws.exception;

import org.springframework.http.HttpStatus;

/**
 * @author Maxime Deravet Date: 2019-02-14
 */
public class PWSException extends Throwable {
    public PWSException(HttpStatus statusCode) {
        super("Received error " + statusCode.value() + " - " + statusCode.getReasonPhrase() + " when calling PWS");

    }

    public PWSException(String error) {
        super(error);

    }

    public PWSException(String message, Throwable cause) {
        super(message, cause);
    }

    public PWSException(Throwable cause) {
        super(cause);
    }
}
