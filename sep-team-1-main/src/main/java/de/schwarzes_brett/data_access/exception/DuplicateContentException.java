package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Used to handle all exceptions where the data to be stored
 * is already existing. It is not thrown itself but only
 * through subclasses.
 */
public abstract class DuplicateContentException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception if any race-condition occurs with null as its detail message.
     */
    public DuplicateContentException() {
        super();
    }

    /**
     * Constructs a new exception if any race-condition occurs with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public DuplicateContentException(String message) {
        super(message);
    }
}




