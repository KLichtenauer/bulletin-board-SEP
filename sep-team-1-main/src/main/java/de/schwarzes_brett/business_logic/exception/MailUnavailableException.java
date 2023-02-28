package de.schwarzes_brett.business_logic.exception;

import java.io.Serial;

/**
 * Throws an exception if a mails cannot be sent because of mail server issues. Checked exception where user is told that currently no mails can be
 * sent.
 */
public class MailUnavailableException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception if needed mail doesn't exist with null as its detail message.
     */
    public MailUnavailableException() {
        super();
    }

    /**
     * Constructs a new exception if needed mail doesn't exist with null as its detail message.
     *
     * @param message The detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public MailUnavailableException(String message) {
        super(message);
    }
}
