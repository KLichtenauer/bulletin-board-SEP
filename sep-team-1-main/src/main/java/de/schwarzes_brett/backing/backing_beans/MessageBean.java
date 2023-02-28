package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.business_logic.services.MessageService;
import de.schwarzes_brett.dto.MessageDTO;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Backing Bean for the creation and dispatchment of a message.
 *
 * @author Valentin Damjantschitsch.
 */
@Named
@ViewScoped
public class MessageBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * External context to use the flash storage.
     */
    @Inject
    private transient ExternalContext externalContext;

    /**
     * The message service instance.
     */
    @Inject
    private MessageService messageService;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * The current message.
     */
    @Inject
    @ManagedProperty("#{flash.keep.message}")
    private MessageDTO message;

    /**
     * Default constructor.
     */
    public MessageBean() {
    }

    /**
     * Getter of the written message.
     *
     * @return Instance of {@code AdDTO} containing the message information.
     */
    public MessageDTO getMessage() {
        return message;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

    }

    /**
     * Gets called when the user sends a message.
     *
     * @return The corresponding facelet after sending a message.
     */
    public String sendMessage() {
        logger.log(Level.FINEST, "Sending the message");
        String path = externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName() + ":"
                      + externalContext.getRequestServerPort() + externalContext.getRequestContextPath() + "/view/public/ad.xhtml"
                      + "?id=" + message.getAd().getId().toString();
        messageService.insertMessage(message, path);
        return "/view/public/ad?faces-redirect=true&id=" + message.getAd().getId();
    }

    /**
     * Deletes a message.
     *
     * @param messageID The ID of the message to be deleted.
     */
    public void deleteMessage(Integer messageID) {
        logger.log(Level.FINEST, "Deleting the message");
        messageService.deleteMessage(messageID);
    }

    /**
     * Updates the visibility of a message.
     *
     * @param message The message to be updated.
     */
    public void updateVisibilityMessage(MessageDTO message) {
        logger.log(Level.FINEST, "Deleting the message");
        messageService.updateVisibility(message);
    }
}
