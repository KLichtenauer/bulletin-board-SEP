package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Throws an exception if an e-mail address already exists. Gets thrown when raceconditions related to duplicate email adress
 * occurs.
 */
public class DuplicateEmailAddressException extends DuplicateContentException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception if email occurs twice with null as its detail message.
     */
    public DuplicateEmailAddressException() {
        super();
    }

    /**
     * Constructs a new exception if email occurs twice with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public DuplicateEmailAddressException(String message) {
        super(message);
    }
}
