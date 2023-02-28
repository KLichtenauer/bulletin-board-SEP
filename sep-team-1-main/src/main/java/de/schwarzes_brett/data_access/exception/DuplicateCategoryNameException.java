package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * Throws an exception if the name of a category already
 * exists. Gets thrown when raceconditions related to duplicate category name
 * occurs.
 */
public class DuplicateCategoryNameException extends DuplicateContentException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception if category name occurs twice with null as its detail message.
     */
    public DuplicateCategoryNameException() {
        super();
    }

    /**
     * Constructs a new exception if category occurs twice with the specified detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public DuplicateCategoryNameException(String message) {
        super(message);
    }
}
