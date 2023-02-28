package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Gets thrown if an error occurs when trying to
 * access a category but the category doesn't exist.
 */
public class CategoryDoesNotExistException extends DoesNotExistException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with null as its detail message.
     */
    public CategoryDoesNotExistException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public CategoryDoesNotExistException(String message) {
        super(message);
    }
}
