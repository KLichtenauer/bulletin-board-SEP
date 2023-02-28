package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.business_logic.dictionary.CompleteDictionary;
import de.schwarzes_brett.business_logic.exception.MailUnavailableException;
import de.schwarzes_brett.business_logic.mail.MailService;
import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.MessageDTO;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Provides a service to the backing layer to manage messages of users assigned to ads. Messages can be inserted, updated, fetched and deleted.
 *
 * @author Valentin Damjantschitsch.
 */
@Named
@RequestScoped
public class MessageService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Service to handle user specific actions.
     */
    @Inject
    private UserService userService;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public MessageService() {
    }

    /**
     * Handles the insertion of a new message.
     *
     * @param message Contains all information about the message to be inserted.
     * @param path    The URL to the ad
     */
    public void insertMessage(MessageDTO message, String path) {
        logger.fine("Inserting the message into the database.");
        Transaction transaction = TransactionFactory.produce();
        try {
            DAOFactory.getMessageDAO(transaction).insertMessage(message);
            transaction.commit();
        } finally {
            transaction.abort();
        }
        sendMail(message, path);
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private void sendMail(MessageDTO message, String path) {
        UserDTO addressee = message.getReceiver();
        userService.fetchUserByUsername(addressee);
        String subject = CompleteDictionary.get("mail_subject_notification", addressee.getLanguage()) + " " + message.getAd().getTitle();
        String msg = buildMessage(addressee, message, path);
        try {
            MailService.sendMail(addressee.getEmail(), subject, msg);
        } catch (MailUnavailableException e) {
            logger.severe("Notification for user with the mail \"" + addressee.getEmail() + "\" could not be sent.");
        }
    }

    /**
     * @author Jonas Elsper
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private String buildMessage(UserDTO addressee, MessageDTO message, String path) {
        if (message.getMessageToBeAnswered() == null) {
            return CompleteDictionary.get("mail_opening", addressee.getLanguage()) + " " + addressee.getCredentials().getUsername() + ","
                   + System.lineSeparator() + CompleteDictionary.get("mail_notification_introduction", addressee.getLanguage())
                   + System.lineSeparator()
                   + System.lineSeparator()
                   + CompleteDictionary.get("mail_notification_message_content", addressee.getLanguage())
                   + System.lineSeparator() + "<>" + "\t" + message.getContent()
                   + System.lineSeparator()
                   + System.lineSeparator()
                   + CompleteDictionary.get("mail_notification_message_link", addressee.getLanguage())
                   + System.lineSeparator()
                   + path;
        } else {
            return CompleteDictionary.get("mail_opening", addressee.getLanguage()) + " " + addressee.getCredentials().getUsername() + ","
                   + System.lineSeparator() + CompleteDictionary.get("mail_notification_introduction", addressee.getLanguage())
                   + System.lineSeparator()
                   + System.lineSeparator()
                   + CompleteDictionary.get("mail_notification_message_content", addressee.getLanguage())
                   + System.lineSeparator() + "<" + message.getMessageToBeAnswered().getContent() + ">" + "\t" + message.getContent()
                   + System.lineSeparator()
                   + System.lineSeparator()
                   + CompleteDictionary.get("mail_notification_message_link", addressee.getLanguage())
                   + System.lineSeparator()
                   + path;
        }
    }

    /**
     * Handles the deletion of a message.
     *
     * @param message Contains all information about the message to be deleted.
     */
    public void deleteMessage(Integer message) {
        logger.fine("Deleting the message.");
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getMessageDAO(transaction).deleteMessage(message);
            transaction.commit();
        }
    }

    /**
     * Updates the visibility of a given message.
     *
     * @param message The message of which the visibility has to be updated.
     */
    public void updateVisibility(MessageDTO message) {
        logger.fine("Updating the visibility of the message.");
        Transaction transaction = TransactionFactory.produce();

        try {
            DAOFactory.getMessageDAO(transaction).updateVisibility(message);
            transaction.commit();
        } finally {
            transaction.abort();
        }
    }
}
