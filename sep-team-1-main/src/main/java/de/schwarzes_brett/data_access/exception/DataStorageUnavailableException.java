package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Gets thrown if an error occurs when trying to
 * access the data storage like sql exception which gets wrapped.
 */
public class DataStorageUnavailableException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with null as its detail message.
     */
    public DataStorageUnavailableException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public DataStorageUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param suppressed Exception which is the cause this exception.
     */
    public DataStorageUnavailableException(Exception suppressed) {
        super(suppressed);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param message    The detail message. The detail message is saved for later retrieval by the getMessage() method.
     * @param suppressed Exception which is the cause this exception.
     */
    public DataStorageUnavailableException(String message, Exception suppressed) {
        super(message, suppressed);
    }
}
