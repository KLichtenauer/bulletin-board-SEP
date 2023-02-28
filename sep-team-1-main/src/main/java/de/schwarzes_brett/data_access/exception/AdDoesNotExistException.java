package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Throws an exception if an error occurs when trying to
 * access an ad but this ad does not exist anymore.
 */
public class AdDoesNotExistException extends DoesNotExistException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception if needed ad doesn't exist with null as its detail message.
     */
    public AdDoesNotExistException() {
        super();
    }

    /**
     * Constructs a new exception if needed ad doesn't exist with null as its detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public AdDoesNotExistException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param suppressed Exception which is the cause this exception.
     */
    public AdDoesNotExistException(Exception suppressed) {
        super(suppressed);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param message    The detail message. The detail message is saved for later retrieval by the getMessage() method.
     * @param suppressed Exception which is the cause this exception.
     */
    public AdDoesNotExistException(String message, Exception suppressed) {
        super(message, suppressed);
    }
}
