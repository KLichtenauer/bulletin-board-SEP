package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.RatingService;
import de.schwarzes_brett.dto.Limits;
import de.schwarzes_brett.dto.RatingDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Backing Bean for the rating of an ad creator.
 *
 * @author michaelgruner
 */
@Named
@ViewScoped
public class RateBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The current rating service instance.
     */
    @Inject
    private RatingService ratingService;

    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;

    /**
     * The externalContext of the request.
     */
    @Inject
    private transient ExternalContext externalContext;

    /**
     * The current rating.
     */
    @Inject
    @ManagedProperty("#{flash.keep.rating}")
    private RatingDTO rating;

    /**
     * The id of the ad the user came from.
     */
    @Inject
    @ManagedProperty("#{flash.keep.adId}")
    private Integer adId;

    /**
     * The rater already rated this user.
     */
    private boolean existingRating = false;

    /**
     * The selectItems for the rating.
     */
    private SelectItem[] selectItems;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public RateBean() {
    }

    /**
     * Getter for the rating.
     *
     * @return An instance of {@code RatingDTO}.
     */
    public RatingDTO getRating() {
        return rating;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
        // Generate message for successful rating.
        notificationHelper.generateFacesMessage(NotificationContext.RATING, null);
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        selectItems = new SelectItem[Limits.MAX_RATING];
        for (int i = 0; i < Limits.MAX_RATING; i++) {
            selectItems[i] = new SelectItem(i + 1, i + 1 + "★");
        }
        logger.fine("Fetching rating from " + rating.getRater().getCredentials().getUsername()
                    + " for " + rating.getRated().getCredentials().getUsername());
        ratingService.fetchRating(rating);
        if (rating.getRating() != null) {
            existingRating = true;
        }
    }

    /**
     * Gets called when the user submits his rating.
     *
     * @return The corresponding facelet for the ad.
     */
    public String submit() {
        logger.fine("Submitted rating");
        if (rating.getRating() == null) {
            logger.fine("Rating was null");
            return "/view/public/ad?faces-redirect=true&id=" + adId;
        }
        if (existingRating) {
            logger.fine("Updating existing rating");
            ratingService.updateRating(rating);
        } else {
            logger.fine("Inserting new rating");
            ratingService.insertRating(rating);
        }
        displayNotifications();
        externalContext.getFlash().setKeepMessages(true);
        return "/view/public/ad?faces-redirect=true&id=" + adId;
    }

    /**
     * Gets called when the user deletes his rating.
     *
     * @return The corresponding facelet for the ad.
     */
    public String deleteRating() {
        logger.fine("Started deleting rating.");
        ratingService.deleteRating(rating);
        logger.fine("Deletion of the rating complete");
        displayNotifications();
        externalContext.getFlash().setKeepMessages(true);
        return "/view/public/ad?faces-redirect=true&id=" + adId;
    }

    /**
     * Returns the selectItems for the radio buttons.
     *
     * @return The selectItems.
     */
    public SelectItem[] getSelectItems() {
        return selectItems;
    }

    /**
     * Returns true if the rater already rated the user.
     *
     * @return True if the rater already rated the user.
     */
    public boolean isExistingRating() {
        return existingRating;
    }
}
