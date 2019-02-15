package edu.uw.edm.pws.exception;

/**
 * @author Maxime Deravet Date: 2019-02-15
 */
public class BadSearchRequestException extends PWSException {

    public BadSearchRequestException(String reason) {
        super(reason);

    }
}
