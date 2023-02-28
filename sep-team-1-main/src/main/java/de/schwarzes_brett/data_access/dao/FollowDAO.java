package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.UserDTO;

/**
 * Provides means to create and remove subscriptions to a user or an ad.
 */
public interface FollowDAO {

    /**
     * Creates a subscription from the follower to the followed user. All ads of the followed user are followed as well.
     *
     * @param follower user who wants to follow.
     * @param followed user the follower wants to follow.
     */
    void insertFollowUser(UserDTO follower, UserDTO followed);

    /**
     * Creates a subscription from the follower to an ad.
     *
     * @param follower user who wants to follow an ad
     * @param ad       ad to be followed.
     */
    void insertFollowAd(UserDTO follower, AdDTO ad);

    /**
     * Removes the subscription from the follower to the followed user.
     *
     * @param follower user who wants to cancel his subscription.
     * @param followed user the follower does not want to follow anymore.
     */
    void removeFollowUser(UserDTO follower, UserDTO followed);

    /**
     * Removes the subscription from a user to a specific ad.
     *
     * @param follower user who wants to cancel his subscription.
     * @param ad       ad which should not be followed by the user anymore.
     */
    void removeFollowAd(UserDTO follower, AdDTO ad);

    /**
     * Checks whether an ad is already followed by a user or not.
     *
     * @param ad   The ad to check whether its followed or not.
     * @param user The user which follows or not
     * @return True if ad is followed by the user, else false.
     */
    boolean isAdFollowed(AdDTO ad, UserDTO user);

    /**
     * Checks if the user is following the adCreator.
     *
     * @param adCreator The creator of the ad to follow.
     * @param user      The user which follows the adCreator or not.
     * @return True if the user follows the adCreator else false.
     */
    boolean isUserFollowed(UserDTO adCreator, UserDTO user);
}
