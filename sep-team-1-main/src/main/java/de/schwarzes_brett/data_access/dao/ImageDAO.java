package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.ImageDoesNotExistException;
import de.schwarzes_brett.dto.ImageDTO;

/**
 * Controls the database access for an image. The access happens through prepared statements and filling DTOs which get iterated through the layers.
 */
public interface ImageDAO {

    /**
     * Checks whether an image with a given id exists.
     *
     * @param image The image to search.
     * @return {@code true} iff the image exists.
     */
    boolean imageExists(ImageDTO image);

    /**
     * Fetches an image from the database.
     *
     * @param image The {@code ImageDTO} instance which is being filled with the fetched image, identified by the id.
     */
    void fetchImage(ImageDTO image) throws ImageDoesNotExistException;

    /**
     * Handles the insertion of an image from the database.
     *
     * @param image The image to be inserted, identified by the id.
     */
    void insertImage(ImageDTO image);

    /**
     * Handles the deletion of an image from the database.
     *
     * @param image The image to be deleted, identified by the id.
     */
    void deleteImage(ImageDTO image);

    /**
     * Cleans up unused images in the database that are over {@code maxAge} seconds old.
     *
     * @param maxAge The maximum age of an unused image, in seconds.
     */
    void cleanUnusedImages(int maxAge);

}
