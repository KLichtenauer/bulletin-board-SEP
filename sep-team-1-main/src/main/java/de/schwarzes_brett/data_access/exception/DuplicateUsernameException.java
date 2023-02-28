package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Throws an exception if a username already exists. Gets thrown when raceconditions related to duplicate username
 * occur.
 */
public class DuplicateUsernameException extends DuplicateContentException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception if username occurs twice with null as its detail message.
     */
    public DuplicateUsernameException() {
        super();
    }

    /**
     * Constructs a new exception if username occurs twice with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public DuplicateUsernameException(String message) {
        super(message);
    }
}
