package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Throws an exception if a user does not exist. Gets thrown when raceconditions related to not existing user
 * occur.
 */
public class UserDoesNotExistException extends DoesNotExistException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception if needed user doesn't exist with null as its detail message.
     */
    public UserDoesNotExistException() {
        super();
    }

    /**
     * Constructs a new exception if needed user doesn't exist with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public UserDoesNotExistException(String message) {
        super(message);
    }
}
