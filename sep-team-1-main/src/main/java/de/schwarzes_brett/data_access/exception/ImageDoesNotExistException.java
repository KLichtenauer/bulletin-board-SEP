package de.schwarzes_brett.data_access.exception;

import java.io.Serial;

/**
 * An exception that is thrown when trying to access an image that does not exist.
 *
 * @author Tim-Florian Feulner
 */
public class ImageDoesNotExistException extends DoesNotExistException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception if needed image doesn't exist.
     */
    public ImageDoesNotExistException() {
        super();
    }

    /**
     * Constructs a new exception if needed image doesn't exist.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public ImageDoesNotExistException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param suppressed Exception which is the cause this exception.
     */
    public ImageDoesNotExistException(Exception suppressed) {
        super(suppressed);
    }

    /**
     * Constructs a new exception with the specified suppressed exception.
     *
     * @param message    The detail message. The detail message is saved for later retrieval by the getMessage() method.
     * @param suppressed Exception which is the cause this exception.
     */
    public ImageDoesNotExistException(String message, Exception suppressed) {
        super(message, suppressed);
    }

}
