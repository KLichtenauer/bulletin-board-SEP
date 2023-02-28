package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.dto.ImageDTO;

/**
 * Controls the access to a default image.
 */
public interface DefaultImageDAO {

    /**
     * Fetches a default image.
     *
     * @param image The {@code ImageDTO} instance which is being filled with the fetched image, identified by the default image type.
     */
    void fetchImage(ImageDTO image);

}
