package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.dto.ImageDTO;

import java.io.InputStream;

/**
 * Must be implemented by backing beans that support image upload functionality.
 *
 * @author Tim-Florian Feulner
 */
public interface ImageUploadHolder {

    /**
     * Gets the currently used {@link ImageUploadBean} instance.
     *
     * @return The current instance.
     */
    ImageUploadBean getImageUploadBean();

    /**
     * Uploads an image.
     * The concrete behavior is dependent on the current site.
     *
     * @param inputStream The image data.
     */
    void uploadImage(InputStream inputStream);

    /**
     * Deletes an image.
     * The concrete behavior is dependent on the current site.
     *
     * @param image The image to delete.
     */
    void deleteImage(ImageDTO image);

}
