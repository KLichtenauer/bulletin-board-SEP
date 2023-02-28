package de.schwarzes_brett.backing.util;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.business_logic.notification.Notification;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.notification.NotificationProvider;
import jakarta.enterprise.context.Dependent;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * Displays faces message corresponding to a context.
 * The notifications are extracted from the {@link NotificationProvider} instance.
 * For example, notifications are displayed when a user edits an ad successfully.
 *
 * @author Tim-Florian Feulner
 */
@Named
@Dependent
public class NotificationHelper implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The dictionary for the current user.
     */
    @Inject
    private transient Dictionary dictionary;

    /**
     * The context of the current request.
     */
    @Inject
    private transient FacesContext facesContext;

    /**
     * The notification storage.
     */
    @Inject
    private NotificationProvider notificationProvider;

    /**
     * Default constructor.
     */
    public NotificationHelper() {}

    /**
     * Generates faces message for given {@code FacesContext}.
     *
     * @param context The specified context of the notification to fetch.
     * @param tagId   The tag of the facelet component for which to display the notification. Can be null.
     */
    public void generateFacesMessage(NotificationContext context, String tagId) {
        Set<Notification> notifications = notificationProvider.extract(context);
        for (Notification notification : notifications) {
            String messageText = dictionary.get(notification.getDescriptionKey());
            FacesMessage.Severity severity = switch (notification.getLevel()) {
                case INFORMATION, SUCCESS -> FacesMessage.SEVERITY_INFO;
                case ERROR -> FacesMessage.SEVERITY_ERROR;
            };
            facesContext.addMessage(tagId, new FacesMessage(severity, messageText, null));
        }
    }

}
