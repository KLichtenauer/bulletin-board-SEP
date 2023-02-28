package de.schwarzes_brett.business_logic.notification;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Receives notifications in the business logic and makes
 * the current notifications available to the backing layer.
 *
 * @author Kilian Lichtenauer
 */
@Named
@RequestScoped
public class NotificationProvider implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The storage of current notifications to be displayed. Gets called by the backing layer if a notification exists. If so the message will be
     * extracted.
     */
    private final HashMap<NotificationContext, HashSet<Notification>> notificationStorage;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public NotificationProvider() {
        notificationStorage = new HashMap<>();
        for (NotificationContext context : NotificationContext.values()) {
            notificationStorage.put(context, new HashSet<>());
        }
    }

    /**
     * Receiving notification and storing it in a hashmap with the context as identifier. Making it available for backing layer.
     *
     * @param notification The {@code notification} to be stored.
     */
    public void insert(Notification notification) {
        logger.log(Level.FINEST, "Notification with context:" + notification.getContext() + "and level:" + notification.getLevel() + "got inserted");
        notificationStorage.get(notification.getContext()).add(notification);
    }

    /**
     * Extracts notification form given {@code NotificationContext}.
     *
     * @param context Context in which a message is to be displayed.
     * @return The notifications for the backing layer.
     */
    public Set<Notification> extract(NotificationContext context) {
        logger.log(Level.FINEST, "Notifications with context: " + context + " got extracted.");
        Set<Notification> result = notificationStorage.get(context);
        notificationStorage.put(context, new HashSet<>());
        return Collections.unmodifiableSet(result);
    }

}
