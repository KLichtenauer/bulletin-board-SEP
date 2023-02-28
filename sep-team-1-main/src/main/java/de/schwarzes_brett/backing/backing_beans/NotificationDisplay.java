package de.schwarzes_brett.backing.backing_beans;

/**
 * Used to display notifications. Type of notification can be any kind. Notification can be sent when a user edits an ad successfully as well as when
 * the edit was unsuccessful.
 */
public interface NotificationDisplay {

    /**
     * Display the notifications corresponding to the current implementing class.
     */
    void displayNotifications();

}
