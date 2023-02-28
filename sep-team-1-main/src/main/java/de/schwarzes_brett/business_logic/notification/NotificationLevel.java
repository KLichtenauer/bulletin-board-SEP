package de.schwarzes_brett.business_logic.notification;

/**
 * The kind of displayed notification.
 */
public enum NotificationLevel {

    /**
     * The notification is of informative kind.
     */
    INFORMATION,

    /**
     * The notification describes a successful user action.
     */
    SUCCESS,

    /**
     * The notification describes a unsuccessful user action.
     */
    ERROR
}
