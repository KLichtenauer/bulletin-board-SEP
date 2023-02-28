package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.dto.RatingDTO;

/**
 * Controls the database access for a rating. The access happens through prepared statements and filling DTOs which get iterated through the layers.
 */
public interface RatingDAO {

    /**
     * Inserts rating into database.
     *
     * @param rating The rating of a user.
     */
    void insertRating(RatingDTO rating);

    /**
     * Updates rating in database.
     *
     * @param rating The updated rating.
     */
    void updateRating(RatingDTO rating);

    /**
     * Deletes rating in database.
     *
     * @param rating The rating to be deleted.
     */
    void deleteRating(RatingDTO rating);

    /**
     * Fetch the rating for the rated from the rater of the rating.
     *
     * @param rating The rating you want to fetch.
     */
    void fetchRating(RatingDTO rating);
}
