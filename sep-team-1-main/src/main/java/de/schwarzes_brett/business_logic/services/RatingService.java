package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.business_logic.notification.Notification;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.notification.NotificationLevel;
import de.schwarzes_brett.business_logic.notification.NotificationProvider;
import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.RatingDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Provides a service to the backing layer to manage ratings from users assigned to ads or users.  Ratings can be inserted, updated, fetched and
 * deleted.
 *
 * @author michaelgruner
 */
@Named
@RequestScoped
public class RatingService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The notification storage.
     */
    @Inject
    private NotificationProvider notificationProvider;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public RatingService() {
    }

    /**
     * Handles the insertion of a new rating.
     *
     * @param rating The rating of a user.
     */
    public void insertRating(RatingDTO rating) {
        try (Transaction transaction = TransactionFactory.produce()) {
            logger.fine("Start insert of a new rating.");
            DAOFactory.getRatingDAO(transaction).insertRating(rating);
            transaction.commit();
            logger.fine("End of inserting a new rating.");
            notificationProvider
                    .insert(new Notification(NotificationLevel.SUCCESS, "m_rateSuccess", NotificationContext.RATING));
        }
    }

    /**
     * Handles changes of an existing rating.
     *
     * @param rating The updated rating.
     */
    public void updateRating(RatingDTO rating) {
        try (Transaction transaction = TransactionFactory.produce()) {
            logger.fine("Start updating rating.");
            DAOFactory.getRatingDAO(transaction).updateRating(rating);
            transaction.commit();
            logger.fine("Updating rating was successful.");
            notificationProvider
                    .insert(new Notification(NotificationLevel.SUCCESS, "m_rateSuccess", NotificationContext.RATING));
        }
    }

    /**
     * Handles the deletion of a rating.
     *
     * @param rating The rating to be deleted.
     */
    public void deleteRating(RatingDTO rating) {
        try (Transaction transaction = TransactionFactory.produce()) {
            logger.fine("Started deleting the rating.");
            DAOFactory.getRatingDAO(transaction).deleteRating(rating);
            transaction.commit();
            logger.fine("Finished deleting the rating.");
            notificationProvider
                    .insert(new Notification(NotificationLevel.SUCCESS, "m_rateDeletionSuccess", NotificationContext.RATING));
        }
    }

    /**
     * Fetches the rating from the persistence layer if existing.
     *
     * @param rating The rating.
     */
    public void fetchRating(RatingDTO rating) {
        try (Transaction transaction = TransactionFactory.produce()) {
            logger.fine("Start of fetching rating.");
            DAOFactory.getRatingDAO(transaction).fetchRating(rating);
            transaction.commit();
            logger.fine("End of fetching rating.");
        }
    }
}
