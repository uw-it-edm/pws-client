package edu.uw.edm.pws.exception;

/**
 * @author Maxime Deravet Date: 2019-02-15
 */
public class NoSuchPersonException extends PWSException {

    public NoSuchPersonException(String id) {
        super("Person " + id + " doesn't exist");
    }
}
