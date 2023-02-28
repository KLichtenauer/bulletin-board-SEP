package de.schwarzes_brett.business_logic.exception;

import java.io.Serial;

/**
 * Is thrown when the Hashing algorithm could not be found and there hashing is not possible.
 *
 * @author Daniel Lipp
 */
public class HashingUnavailableException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new HashingUnavailableException.
     */
    public HashingUnavailableException() {
        super();
    }

    /**
     * Constructs a new HashingUnavailableException and sets the message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public HashingUnavailableException(String message) {
        super(message);
    }
}
