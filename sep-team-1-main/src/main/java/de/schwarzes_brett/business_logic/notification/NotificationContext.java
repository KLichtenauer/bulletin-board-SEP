package de.schwarzes_brett.business_logic.notification;

/**
 * Context in which a message is to be displayed. Has to be set, so that backing layer knows where the notification belongs to.
 */
public enum NotificationContext {

    /**
     * The notification has no context.
     */
    NONE,

    /**
     * The notification is assigned to the username.
     */
    USERNAME,

    /**
     * The notification is assigned to the password.
     */
    PASSWORD,

    /**
     * The notification is assigned to the email.
     */
    EMAIL,

    /**
     * The notification is assigned to the phone number.
     */
    PHONE_NUMBER,

    /**
     * The notification is assigned to the ad id.
     */
    AD_ID,

    /**
     * The notification is assigned to the price.
     */
    PRICE,

    /**
     * The notification is assigned to the category name.
     */
    CATEGORY_NAME,

    /**
     * The notification is assigned to the ad title.
     */
    AD_TITLE,

    /**
     * The notification is assigned to the image.
     */
    IMAGE,

    /**
     * The notification is assigned to the rating.
     */
    RATING,
    /**
     * The notification is assigned to the follow.
     */
    FOLLOW
}
