package de.schwarzes_brett.business_logic.notification;

import java.io.Serial;
import java.io.Serializable;

/**
 * A notification is a wrapped set of information for the users. Type of notification can be any kind for example: get shown when a user
 * edits an ad successfully as well as when the edit was unsuccessful. A massage for notification provider to display a message on the GUI.
 *
 * @author Kilian Lichtenauer
 */
public class Notification implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The importance level of this notification.
     */
    private final NotificationLevel level;

    /**
     * The key for the notification phrase.
     */
    private final String descriptionKey;

    /**
     * The context in which to display this notification.
     */
    private final NotificationContext context;

    /**
     * Constructs a notification object with given parameters.
     *
     * @param level          The importance level of a displayed notification
     * @param descriptionKey Description for the user.
     * @param context        Sets in which context the notification occurred.
     */
    public Notification(NotificationLevel level, String descriptionKey, NotificationContext context) {
        this.level = level;
        this.descriptionKey = descriptionKey;
        this.context = context;
    }

    /**
     * Getter for the importance of the notification.
     *
     * @return An instance of {@code NotificationLevel} representing reason.
     */
    public NotificationLevel getLevel() {
        return level;
    }

    /**
     * Getter for the description of the notification.
     *
     * @return An instance of {@code String} representing a description for the user.
     */
    public String getDescriptionKey() {
        return descriptionKey;
    }

    /**
     * Getter for the context of the notification.
     *
     * @return An instance of {@code NotificationContext} representing the context
     * in which the notification occurred
     */
    public NotificationContext getContext() {
        return context;
    }
}
