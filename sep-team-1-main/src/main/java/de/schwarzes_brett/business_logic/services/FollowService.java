package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.business_logic.notification.Notification;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.notification.NotificationLevel;
import de.schwarzes_brett.business_logic.notification.NotificationProvider;
import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Provides means to create and remove subscriptions to a user or an ad.
 *
 * @author Jonas Elsper
 */
@Named
@RequestScoped
public class FollowService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * The notification storage.
     */
    @Inject
    private NotificationProvider notificationProvider;


    /**
     * Default constructor.
     */
    public FollowService() {
    }

    /**
     * Creates a subscription from the follower to the followed user. All ads of the followed user are followed as well.
     *
     * @param follower user who wants to follow.
     * @param followed user the follower wants to follow.
     */
    public void insertFollowUser(UserDTO follower, UserDTO followed) {
        if (follower != null && followed != null) {
            try (Transaction t = TransactionFactory.produce()) {
                DAOFactory.getFollowDAO(t).insertFollowUser(follower, followed);
                notificationProvider
                        .insert(new Notification(NotificationLevel.SUCCESS, "m_followUserSuccess", NotificationContext.FOLLOW));
                t.commit();
            }
        } else {
            logger.severe("Follower or followed UserDTO was null.");
            throw new IllegalArgumentException("Illegal arguments for inserting new follower.");
        }
    }

    /**
     * Creates a subscription from the follower to an ad.
     *
     * @param follower user who wants to follow an ad
     * @param ad       ad to be followed.
     */
    public void insertFollowAd(UserDTO follower, AdDTO ad) {
        if (follower != null && ad != null) {
            try (Transaction t = TransactionFactory.produce()) {
                DAOFactory.getFollowDAO(t).insertFollowAd(follower, ad);
                notificationProvider
                        .insert(new Notification(NotificationLevel.SUCCESS, "m_followAdSuccess", NotificationContext.FOLLOW));
                t.commit();
            }
        } else {
            logger.severe("Follower or ad was null.");
            throw new IllegalArgumentException("Illegal arguments for inserting new follower.");
        }
    }

    /**
     * Removes the subscription from the follower to the followed user.
     *
     * @param follower user who wants to cancel his subscription.
     * @param followed user the follower does not want to follow anymore.
     */
    public void removeFollowUser(UserDTO follower, UserDTO followed) {
        if (follower != null && followed != null) {
            try (Transaction t = TransactionFactory.produce()) {
                DAOFactory.getFollowDAO(t).removeFollowUser(follower, followed);
                notificationProvider
                        .insert(new Notification(NotificationLevel.SUCCESS, "m_unfollowUserSuccess", NotificationContext.FOLLOW));
                t.commit();
            }
        } else {
            logger.severe("Follower or followed user was null.");
            throw new IllegalArgumentException("Illegal arguments for removing follower.");
        }
    }

    /**
     * Removes the subscription from a user to a specific ad.
     *
     * @param follower user who wants to cancel his subscription.
     * @param ad       ad which should not be followed by the user anymore.
     */
    public void removeFollowAd(UserDTO follower, AdDTO ad) {
        if (follower != null && ad != null) {
            try (Transaction t = TransactionFactory.produce()) {
                DAOFactory.getFollowDAO(t).removeFollowAd(follower, ad);
                notificationProvider
                        .insert(new Notification(NotificationLevel.SUCCESS, "m_unfollowAdSuccess", NotificationContext.FOLLOW));
                t.commit();
            }
        } else {
            logger.severe("Follower or ad was null.");
            throw new IllegalArgumentException("Illegal arguments for removing follower.");
        }
    }

    /**
     * Checks if a user is following a certain ad.
     *
     * @param ad   The ad which the user may follow.
     * @param user The user who may follow the ad.
     * @return True if the user follows the ad else false.
     */
    public boolean isAdFollowed(AdDTO ad, UserDTO user) {
        if (ad != null && user != null) {
            try (Transaction t = TransactionFactory.produce()) {
                boolean result = DAOFactory.getFollowDAO(t).isAdFollowed(ad, user);
                t.commit();
                return result;
            }
        }
        logger.severe("Given AdDTO or UserDTO was null.");
        throw new IllegalArgumentException("The given arguments were invalid.");
    }

    /**
     * Checks if a user is following a certain ad-creator.
     *
     * @param adCreator The ad-creator whom the user may follow.
     * @param user      The user who may follow the ad-creator.
     * @return True if the user follows the ad-creator else false.
     */
    public boolean isUserFollowed(UserDTO adCreator, UserDTO user) {
        if (adCreator != null && user != null) {
            try (Transaction t = TransactionFactory.produce()) {
                boolean result = DAOFactory.getFollowDAO(t).isUserFollowed(adCreator, user);
                t.commit();
                return result;
            }
        }
        logger.severe("One of the two given UserDTOs was null.");
        throw new IllegalArgumentException("The given arguments were invalid.");
    }
}
