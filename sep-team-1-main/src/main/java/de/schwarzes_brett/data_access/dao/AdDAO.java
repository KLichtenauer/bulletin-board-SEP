package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.PaginationDTO;
import de.schwarzes_brett.dto.UserDTO;

import java.util.List;

/**
 * Controls the database access for an ad. The access happens through prepared statements and filling DTOs which get iterated through
 * the layers.
 */
public interface AdDAO {

    /**
     * Inserts ad in database.
     *
     * @param ad The ad to be inserted.
     */
    void insertAd(AdDTO ad);

    /**
     * Updates ad in database.
     *
     * @param ad The updated ad.
     */
    void updateAd(AdDTO ad);

    /**
     * Deletes ad in database.
     *
     * @param ad The ad to be deleted.
     */
    void deleteAd(AdDTO ad);

    /**
     * Fetches ad from database.
     *
     * @param ad               The {@code AdDTO} instance contains the needed adID and is getting filled with fetched ad.
     * @param userId           The id of the user who calls the ad.
     * @param isAdminOrCreator True if user is the creator of the ad or is an admin.
     */
    void fetchAd(AdDTO ad, Integer userId, boolean isAdminOrCreator);

    /**
     * Fetches ads from a specific user from the database.
     *
     * @param user       The user of whom the ads get fetched.
     * @param pagination The {@code PaginationDTO} in which the ads get saved.
     * @return List of {@code AdDTO} containing fetched ads.
     */
    List<AdDTO> fetchAdsFromUser(PaginationDTO pagination, UserDTO user);

    /**
     * Fetches all ads from the database.
     *
     * @param pagination The {@code PaginationDTO} in which the ads get saved.
     * @return List of {@code AdDTO} containing fetched ads.
     */
    List<AdDTO> fetchAds(PaginationDTO pagination);

    /**
     * Fetches all ads from the database.
     *
     * @param pagination The {@code PaginationDTO} in which the ads get saved.
     * @return List of {@code AdDTO} containing fetched ads.
     */
    List<AdDTO> fetchAdsWithUnreleased(PaginationDTO pagination);

    /**
     * Fetches ads that are followed for the given user.
     *
     * @param pagination The {@code PaginationDTO} in which the information of the pagination is saved.
     * @param user       The user for which the ads are fetched.
     * @return List of {@code AdDTO} containing fetched ads.
     */
    List<AdDTO> fetchFollowedAds(PaginationDTO pagination, UserDTO user);

    /**
     * Fetches ads that were commented by the given user.
     *
     * @param pagination The {@code PaginationDTO} in which the information of the pagination is saved.
     * @param user       The user for which the ads are fetched.
     * @return List of {@code AdDTO} containing fetched ads.
     */
    List<AdDTO> fetchCommentedAds(PaginationDTO pagination, UserDTO user);

    /**
     * Checks in the database if ad has a valid id.
     *
     * @param ad Contains the id to be checked.
     * @return If the id is valid.
     */
    boolean isAdIdValid(AdDTO ad);

    /**
     * Inserts a given image to an ad.
     *
     * @param ad    The ad of which an image should get inserted into.
     * @param image The image which should get inserted.
     */
    void insertImage(AdDTO ad, ImageDTO image);

    /**
     * Deletes a given image from an ad.
     *
     * @param ad    The ad of which an image should get deleted of.
     * @param image The image which should get deleted.
     */
    void deleteImage(AdDTO ad, ImageDTO image);

    /**
     * Updates the thumbnail to the given picture.
     *
     * @param ad    ad to be with the new thumbnail.
     * @param index Index of new thumbnail int the list of images of the ad.
     */
    void updateThumbnail(AdDTO ad, int index);

    /**
     * Fetches the number of pages for a given {@code PaginationDTO}.
     *
     * @param pagination The {@code PaginationDTO} in which the information of the pagination is saved.
     * @return The number of pages.
     */
    int fetchLastPageNumber(PaginationDTO pagination);

    /**
     * Fetches the number of pages for a given {@code PaginationDTO}.
     *
     * @param pagination The {@code PaginationDTO} in which the information of the pagination is saved.
     * @return The number of pages.
     */
    int fetchLastPageNumberWithUnreleased(PaginationDTO pagination);

    /**
     * Fetches the number of pages for a given {@code PaginationDTO}.
     *
     * @param pagination The {@code PaginationDTO} in which the information of the pagination is saved.
     * @param user       The {@code UserDTO} in which the information about the logged-in user is saved.
     * @return The number of pages.
     */
    int fetchOwnAdsLastPageNumber(PaginationDTO pagination, UserDTO user);

    /**
     * Fetches the number of pages for a given {@code PaginationDTO}.
     *
     * @param pagination The {@code PaginationDTO} in which the information of the pagination is saved.
     * @param user       The {@code UserDTO} in which the information about the logged-in user is saved.
     * @return The number of pages.
     */
    int fetchFollowedAdsLastPageNumber(PaginationDTO pagination, UserDTO user);

    /**
     * Fetches the number of pages for a given {@code PaginationDTO}.
     *
     * @param pagination The {@code PaginationDTO} in which the information of the pagination is saved.
     * @param user       The {@code UserDTO} in which the information about the logged-in user is saved.
     * @return The number of pages.
     */
    int fetchCommentedAdsLastPageNumber(PaginationDTO pagination, UserDTO user);

    /**
     * Fetches all followers of an ad.
     *
     * @param ad The ad to fetch its followers.
     * @return All followers of the ad.
     */
    List<UserDTO> fetchAdFollower(AdDTO ad);
}
