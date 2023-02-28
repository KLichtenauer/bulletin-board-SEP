package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Is thrown if access of the data storage fails.
 *
 * @author Daniel Lipp
 */
public class DataStorageAccessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new Exception.
     */
    public DataStorageAccessException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public DataStorageAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param suppressed Exception which is the cause this exception.
     */
    public DataStorageAccessException(Exception suppressed) {
        super(suppressed);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param message    The detail message. The detail message is saved for later retrieval by the getMessage() method.
     * @param suppressed Exception which is the cause this exception.
     */
    public DataStorageAccessException(String message, Exception suppressed) {
        super(message, suppressed);
    }
}
