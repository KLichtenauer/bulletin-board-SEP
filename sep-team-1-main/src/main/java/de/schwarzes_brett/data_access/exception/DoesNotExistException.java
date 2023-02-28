package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Used to handle all exceptions where the requested data
 * does not exist. It is not thrown itself but only through
 * subclasses.
 */
public abstract class DoesNotExistException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception if anything needed from the database doesn't exist with null as its detail message.
     */
    protected DoesNotExistException() {
        super();
    }

    /**
     * Constructs a new exception if anything needed from the database doesn't exist with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    protected DoesNotExistException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param suppressed Exception which is the cause this exception.
     */
    public DoesNotExistException(Exception suppressed) {
        super(suppressed);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param message    The detail message. The detail message is saved for later retrieval by the getMessage() method.
     * @param suppressed Exception which is the cause this exception.
     */
    public DoesNotExistException(String message, Exception suppressed) {
        super(message, suppressed);
    }
}
