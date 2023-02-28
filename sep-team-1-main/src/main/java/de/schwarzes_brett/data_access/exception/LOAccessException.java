package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * An exception that is thrown if an error occurs when trying to access a large object in the database.
 *
 * @author Tim-Florian Feulner
 */
public class LOAccessException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     */
    public LOAccessException() {
        super();
    }

    /**
     * Constructs a new exception.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */

    public LOAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param suppressed Exception which is the cause this exception.
     */
    public LOAccessException(Exception suppressed) {
        super(suppressed);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param message    The detail message. The detail message is saved for later retrieval by the getMessage() method.
     * @param suppressed Exception which is the cause this exception.
     */
    public LOAccessException(String message, Exception suppressed) {
        super(message, suppressed);
    }

}
