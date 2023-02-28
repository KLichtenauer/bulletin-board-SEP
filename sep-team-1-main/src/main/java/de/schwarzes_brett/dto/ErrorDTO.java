package de.schwarzes_brett.dto;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * Contains the data of an error.
 *
 * @author Valentin Damjantschitsch.
 */
public class ErrorDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The message of the error.
     */
    private String message;

    /**
     * The exception of the error.
     */
    private Exception exception;

    /**
     * The stacktrace of the exception.
     */
    private String stacktrace;

    /**
     * Default constructor.
     */
    public ErrorDTO() {
    }

    /**
     * Getter for the message of the error.
     *
     * @return The message of the error.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter for the message of the error.
     *
     * @param message The message to be set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for the exception of the error.
     *
     * @return The exception of the error.
     */
    public Exception getException() {
        return exception;
    }

    /**
     * Setter for the exception of the error.
     *
     * @param exception The exception to be set.
     * @author Daniel Lipp
     */
    public void setException(Exception exception) {
        this.exception = exception;
        ByteArrayOutputStream outTemp = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(outTemp, false, StandardCharsets.UTF_8));
        stacktrace = outTemp.toString(StandardCharsets.UTF_8);
    }

    /**
     * Getter for the stacktrace of the exception.
     *
     * @return stacktrace from the exception stored in this DTO.
     * @author Daniel Lipp
     */
    public String getStacktrace() {
        return stacktrace;
    }
}
