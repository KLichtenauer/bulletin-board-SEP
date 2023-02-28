package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.FollowService;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Backing Bean for the subscription of an ad creator or an ad.
 *
 * @author Jonas Elsper
 */
@Named
@ViewScoped
public class FollowBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * The ad the user came from.
     */
    @Inject
    @ManagedProperty("#{flash.keep.ad}")
    private AdDTO ad;

    /**
     * The follow service instance.
     */
    @Inject
    private FollowService followService;

    /**
     * The session of the current user.
     */
    @Inject
    private UserSession userSession;

    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;

    /**
     * Whether the user follows the ad.
     */
    private boolean followAd;

    /**
     * Whether the user follows the creator of the ad.
     */
    private boolean followUser;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public FollowBean() {
    }

    /**
     * Gets the user which follows.
     *
     * @return An instance of {@code UserDTO} which contains the userSession information.
     */
    public UserDTO getUser() {
        return userSession.getUser();
    }

    /**
     * Gets the ad which gets followed.
     *
     * @return An instance of {@code adDTO} which contains the ad's information.
     */
    public AdDTO getAd() {
        return ad;
    }

    /**
     * Sets the flag if the ad is followed.
     *
     * @return True if ad is already followed, false otherwise.
     */
    public boolean isFollowAd() {
        return followAd;
    }

    /**
     * Sets the userSession as follower of the ad.
     *
     * @param followAd The value of a userSession following an ad.
     */
    public void setFollowAd(boolean followAd) {
        this.followAd = followAd;
    }

    /**
     * Checks if user is already followed.
     *
     * @return True if user is already followed, false otherwise.
     */
    public boolean isFollowUser() {
        return followUser;
    }

    /**
     * Sets the flag if the user is followed.
     *
     * @param followUser Sets the boolean value if user is followed.
     */
    public void setFollowUser(boolean followUser) {
        this.followUser = followUser;
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.fine("Starting to initialize followBean.");
        followAd = followService.isAdFollowed(ad, userSession.getUser());
        followUser = followService.isUserFollowed(ad.getCreator(), userSession.getUser());
        logger.info("Initialized followBean.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
        // Generate message for successful follow.
        notificationHelper.generateFacesMessage(NotificationContext.FOLLOW, null);
    }

    /**
     * Saves any changes and gets called when the saved button gets pressed. Updates the follow status of the ad or userSession.
     */
    public void save() {
        logger.fine("Saving the follow selection of the user.");
        if (followUser && !followService.isUserFollowed(ad.getCreator(), userSession.getUser())) {
            logger.finest("Follow the ad-creator.");
            followService.insertFollowUser(userSession.getUser(), ad.getCreator());
        } else {
            logger.finest("Unfollow the ad-creator");
            if (!followUser && followService.isUserFollowed(ad.getCreator(), userSession.getUser())) {
                followService.removeFollowUser(userSession.getUser(), ad.getCreator());
            }
        }
        if (followAd && !followService.isAdFollowed(ad, userSession.getUser())) {
            logger.finest("Follow the ad.");
            followService.insertFollowAd(userSession.getUser(), ad);
        } else {
            logger.finest("Unfollow the ad.");
            if (!followAd && followService.isAdFollowed(ad, userSession.getUser())) {
                followService.removeFollowAd(userSession.getUser(), ad);
            }
        }
        displayNotifications();
        logger.fine("Saved the follow selection of the user successfully.");
    }
}
