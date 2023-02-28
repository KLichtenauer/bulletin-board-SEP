package de.schwarzes_brett.backing.exception;

import java.io.Serial;

/**
 * Should be thrown when a user tries to access a site he does not have the permissions for.
 */
public class UnauthorisedAccessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception.
     */
    public UnauthorisedAccessException() {
        super();
    }

    /**
     * Constructs a new exception and sets the message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public UnauthorisedAccessException(String message) {
        super(message);
    }
}

