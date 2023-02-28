package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.PaginationDTO;
import de.schwarzes_brett.dto.Role;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a service to the backing layer to manage advertisements. Ads can be inserted, updated, fetched and deleted. Multiple ads get loaded for
 * listed presentation depending on selected category, user or pagination-page.
 */
@Named
@RequestScoped
public class AdService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public AdService() {
    }

    /**
     * Handles the insertion of a new ad.
     *
     * @param ad    The ad to be inserted.
     * @param index Index of the thumbnail. If no thumbnail is selected value should be -1.
     * @author Daniel Lipp
     */
    public void insertAd(AdDTO ad, int index) {
        try (Transaction t = TransactionFactory.produce()) {

            logger.fine("inserting contact data of the ad.");
            Long contactId = DAOFactory.getUserDAO(t).insertContactData(ad.getPublicData());
            ad.getPublicData().setContactInfoId(contactId);

            logger.fine("inserting the new ad.");
            DAOFactory.getAdDAO(t).insertAd(ad);
            if (ad.getImages() != null && ad.getImages().size() > 0 && index != -1) {
                DAOFactory.getAdDAO(t).updateThumbnail(ad, index);
            }
            t.commit();
        }
    }

    /**
     * Converts the time to UTC before inserting the ad.
     *
     * @param ad            The ad to be inserted.
     * @param index         Index of the thumbnail. If no thumbnail is selected value should be -1.
     * @param minutesOffset Offset of the user in minutes.
     * @author Daniel Lipp
     */
    public void insertAd(AdDTO ad, int index, long minutesOffset) {
        ad.setRelease(ad.getRelease().plusMinutes(minutesOffset));
        if (ad.getEnd() != null) {
            ad.setEnd(ad.getEnd().plusMinutes(minutesOffset));
        }
        insertAd(ad, index);
    }

    /**
     * Performs the update on a given ad identified by the given id.
     *
     * @param ad    The ad containing the updates.
     * @param index Index of the Thumbnail. If no thumbnail is selected value should be -1.
     * @author Daniel Lipp
     */
    public void updateAd(AdDTO ad, int index) {
        try (Transaction t = TransactionFactory.produce()) {
            DAOFactory.getUserDAO(t).updateContactInfo(ad.getPublicData());
            DAOFactory.getUserDAO(t).fetchUserByUsername(ad.getCreator());

            DAOFactory.getAdDAO(t).updateAd(ad);
            if (ad.getImages() != null && ad.getImages().size() > 0 && index != -1) {
                DAOFactory.getAdDAO(t).updateThumbnail(ad, index);
            }
            t.commit();
        }
    }

    /**
     * Converts the time to UTC before updating the ad.
     *
     * @param ad            The ad to be inserted.
     * @param index         Index of the thumbnail. If no thumbnail is selected value should be -1.
     * @param minutesOffset Offset of the user in minutes.
     * @author Daniel Lipp
     */
    public void updateAd(AdDTO ad, int index, long minutesOffset) {
        ad.setRelease(ad.getRelease().plusMinutes(minutesOffset));
        if (ad.getEnd() != null) {
            ad.setEnd(ad.getEnd().plusMinutes(minutesOffset));
        }
        updateAd(ad, index);
    }

    /**
     * Performs the deletion of an ad, identified by the given id.
     *
     * @param ad The ad to be deleted.
     * @author Daniel Lipp
     */
    public void deleteAd(AdDTO ad) {
        try (Transaction t = TransactionFactory.produce()) {
            DAOFactory.getAdDAO(t).deleteAd(ad);
            t.commit();
        }
    }

    /**
     * Handles the ad fetching through data access layer. The ad is identified by the given id.
     *
     * @param ad   The instance of {@code AdDTO} which is getting filled with the fetched ad.
     * @param user The user who wants to see the ad
     * @author Jonas Elsper
     */
    public void fetchAd(AdDTO ad, UserDTO user) {
        Transaction trans = TransactionFactory.produce();
        try {
            Integer id = user.getId();
            boolean isAdmin = user.getRole() != null && user.getRole().equals(Role.ADMIN);
            boolean isCreator = ad.getCreator() != null && ad.getCreator().getId().equals(id);
            boolean isCreatorOrAdmin = isAdmin || isCreator;
            DAOFactory.getAdDAO(trans).fetchAd(ad, id, isCreatorOrAdmin);
            trans.commit();
        } finally {
            trans.abort();
        }
    }

    /**
     * Handles the ad fetching through data access layer. The ad is identified by the given id.
     *
     * @param ad            The instance of {@code AdDTO} which is getting filled with the fetched ad.
     * @param user          The user who wants to see the ad.
     * @param minutesOffset Offset of the user in minutes.
     * @author Daniel Lipp
     */
    public void fetchAd(AdDTO ad, UserDTO user, long minutesOffset) {
        fetchAd(ad, user);
        if (ad.getRelease() != null) {
            ad.setRelease(ad.getRelease().minusMinutes(minutesOffset));
        }
        if (ad.getEnd() != null) {
            ad.setEnd(ad.getEnd().minusMinutes(minutesOffset));
        }
    }

    /**
     * Fetches multiple ads created from a specific user.
     *
     * @param user       The user of whom the ads get fetched.
     * @param pagination The {@code PaginationDTO} instance for listing the ads.
     * @return List of ads from the user.
     * @author Jonas Elsper
     */
    public List<AdDTO> fetchAdsFromUser(PaginationDTO pagination, UserDTO user) {
        checkSortByNotNull(pagination);
        List<AdDTO> ads;
        logger.fine("Fetching Ads from the DAO started.");
        Transaction transaction = TransactionFactory.produce();
        try {
            ads = DAOFactory.getAdDAO(transaction).fetchAdsFromUser(pagination, user);
            pagination.setLastPageNumber(DAOFactory.getAdDAO(transaction).fetchOwnAdsLastPageNumber(pagination, user));
            logger.fine("Fetching Ads from the DAO finished.");
            transaction.commit();
        } catch (DataStorageAccessException e) {
            logger.severe("An error occurred in fetchAds.");
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } finally {
            transaction.abort();
        }
        return ads;
    }

    /**
     * Fetches all ads.
     *
     * @param pagination The {@code PaginationDTO} instance for listing the ads.
     * @return List of ads for the landing page.
     * @author michaelgruener
     */
    public List<AdDTO> fetchAds(PaginationDTO pagination) {
        checkSortByNotNull(pagination);
        List<AdDTO> ads;
        logger.fine("Fetching Ads from the DAO started.");
        Transaction transaction = TransactionFactory.produce();
        try {
            ads = DAOFactory.getAdDAO(transaction).fetchAds(pagination);
            pagination.setLastPageNumber(DAOFactory.getAdDAO(transaction).fetchLastPageNumber(pagination));
            logger.fine("Fetching Ads from the DAO finished.");
            transaction.commit();
        } catch (DataStorageAccessException e) {
            logger.severe("An error occurred in fetchAds.");
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } finally {
            transaction.abort();
        }
        return ads;
    }

    /**
     * Fetches all ads, also those which are not published yet.
     *
     * @param pagination The {@code PaginationDTO} instance for listing the ads.
     * @return List of ads for the landing page.
     * @author Valentin Damjantschitsch
     */
    public List<AdDTO> fetchAllAds(PaginationDTO pagination) {
        checkSortByNotNull(pagination);
        List<AdDTO> ads;
        logger.fine("Fetching Ads from the DAO started.");
        Transaction transaction = TransactionFactory.produce();
        try {
            ads = DAOFactory.getAdDAO(transaction).fetchAdsWithUnreleased(pagination);
            pagination.setLastPageNumber(DAOFactory.getAdDAO(transaction).fetchLastPageNumberWithUnreleased(pagination));
            logger.fine("Fetching Ads from the DAO finished.");
            transaction.commit();
        } catch (DataStorageAccessException e) {
            logger.severe("An error occurred in fetchAds.");
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } finally {
            transaction.abort();
        }
        return ads;
    }

    /**
     * Fetches all ads that the given user is following.
     *
     * @param pagination The {@code PaginationDTO} instance for listing the ads.
     * @param user       The user requesting the followed ads list.
     * @return List of ads to display.
     * @author Jonas Elsper
     */
    public List<AdDTO> fetchFollowedAds(PaginationDTO pagination, UserDTO user) {
        checkSortByNotNull(pagination);
        List<AdDTO> ads;
        logger.fine("Fetching Ads from the DAO started.");
        Transaction transaction = TransactionFactory.produce();
        try {
            ads = DAOFactory.getAdDAO(transaction).fetchFollowedAds(pagination, user);
            pagination.setLastPageNumber(DAOFactory.getAdDAO(transaction).fetchFollowedAdsLastPageNumber(pagination, user));
            logger.fine("Fetching Ads from the DAO finished.");
            transaction.commit();
        } catch (DataStorageAccessException e) {
            logger.severe("An error occurred in fetchAds.");
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } finally {
            transaction.abort();
        }
        return ads;
    }

    /**
     * Fetches all ads that have been commented by the user.
     *
     * @param pagination The {@code PaginationDTO} instance for listing the ads.
     * @param user       The user requesting the commented ads list.
     * @return List of ads to display.
     * @author Jonas Elsper
     */
    public List<AdDTO> fetchCommentedAds(PaginationDTO pagination, UserDTO user) {
        checkSortByNotNull(pagination);
        List<AdDTO> ads;
        logger.fine("Fetching Ads from the DAO started.");
        Transaction transaction = TransactionFactory.produce();
        try {
            ads = DAOFactory.getAdDAO(transaction).fetchCommentedAds(pagination, user);
            pagination.setLastPageNumber(DAOFactory.getAdDAO(transaction).fetchCommentedAdsLastPageNumber(pagination, user));
            logger.fine("Fetching Ads from the DAO finished.");
            transaction.commit();
        } catch (DataStorageAccessException e) {
            logger.severe("An error occurred in fetchAds.");
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        } finally {
            transaction.abort();
        }
        return ads;
    }

    /**
     * Checks if the id of a given ad is valid.
     *
     * @param ad The ad of which the id has to be checked.
     * @return True if the price is valid.
     * @author Jonas Elsper
     */
    public boolean isIdValid(AdDTO ad) {
        Transaction trans = TransactionFactory.produce();
        try {
            boolean isValid = DAOFactory.getAdDAO(trans).isAdIdValid(ad);
            if (!isValid) {
                trans.abort();
            } else {
                trans.commit();
            }
            return isValid;
        } finally {
            trans.abort();
        }
    }

    /**
     * Checks if the price of a given ad is valid.
     *
     * @param ad The ad of which the price has to be checked.
     * @return True if the price is valid.
     * @author Daniel Lipp
     */
    public boolean isPriceValid(AdDTO ad) {
        return ad.getPrice() == null || ad.getPrice().getValue().abs().equals(ad.getPrice().getValue());
    }

    /**
     * Deletes a given image from an ad.
     *
     * @param ad    The ad an image should get deleted of.
     * @param image The image that should get deleted.
     * @author Daniel Lipp
     */
    public void deleteImage(AdDTO ad, ImageDTO image) {
        try (Transaction t = TransactionFactory.produce()) {
            DAOFactory.getAdDAO(t).deleteImage(ad, image);
            t.commit();
        }
    }

    /**
     * Inserts a given image to an ad.
     *
     * @param ad    The ad an image should get inserted into.
     * @param image The image that should get inserted.
     * @author Daniel Lipp
     */
    public void insertImage(AdDTO ad, ImageDTO image) {
        try (Transaction t = TransactionFactory.produce()) {
            DAOFactory.getImageDAO(t).insertImage(image);
            DAOFactory.getAdDAO(t).insertImage(ad, image);
            t.commit();
        }
    }

    /**
     * @author michaelgruener
     */
    @SuppressWarnings({"checkstyle:JavadocMethod"})
    private void checkSortByNotNull(PaginationDTO pagination) {
        if (pagination.getSortBy() == null) {
            String message = "SortBy of Pagination is null";
            logger.severe(message);
            throw new IllegalStateException(message);
        }
    }
}
