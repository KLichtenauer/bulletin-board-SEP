package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.ImageService;
import de.schwarzes_brett.dto.ImageDTO;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Backing bean for the composite component {@code imageUpload}.
 * By implementing the interface {@link ImageUploadHolder}, a backing bean can add the functionality for image upload.
 *
 * @author Tim-Florian Feulner
 */
@Named
@ViewScoped
public class ImageUploadBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;

    /**
     * The uploaded images that are displayed.
     */
    private List<ImageDTO> images = new ArrayList<>();

    /**
     * The upload target.
     */
    private transient Part uploadImage;

    /**
     * The instance of the image service.
     */
    @Inject
    private ImageService imageService;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public ImageUploadBean() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
        // Generate message for failed image upload.
        notificationHelper.generateFacesMessage(NotificationContext.IMAGE, "uploadButton");
    }

    /**
     * Stores the images persistently.
     * This method is to be used when the uploaded image is not (yet) associated with other persistent data.
     *
     * @param uploadStream The stream that contains the uploaded data.
     */
    public void uploadRaw(InputStream uploadStream) {
        ImageDTO newImage = new ImageDTO();
        newImage.setStoreStream(uploadStream);

        imageService.insertImage(newImage);

        try {
            uploadStream.close();
        } catch (IOException e) {
            String message = "Failed to upload image.";
            logger.warning(message);
            logger.log(Level.WARNING, e.getMessage(), e);
            notificationHelper.generateFacesMessage(NotificationContext.IMAGE, "f_image_upload_failed");
            displayNotifications();
            return;
        }

        addImageToDisplay(newImage);
    }

    /**
     * Adds an image to be shown to the user.
     *
     * @param image The image to be displayed.
     */
    public void addImageToDisplay(ImageDTO image) {
        images.add(image);
    }

    /**
     * Deletes the given image that was uploaded via {@link ImageUploadBean#uploadRaw(InputStream)}
     * from the persistent data storage.
     *
     * @param image The image to be deleted.
     */
    public void deleteRaw(ImageDTO image) {
        imageService.deleteImage(image);
        removeImageFromDisplay(image);
    }

    /**
     * Removes an image from the display, i.e. it is then not shown to the user anymore.
     *
     * @param image The image to be removed.
     */
    public void removeImageFromDisplay(ImageDTO image) {
        images.remove(image);
    }

    /**
     * Gets the images that are displayed.
     *
     * @return List of images.
     */
    public List<ImageDTO> getImages() {
        return images;
    }

    /**
     * Sets the images that are uploaded.
     *
     * @param images The images to be displayed.
     */
    public void setImages(List<ImageDTO> images) {
        this.images = images;
    }

    /**
     * Gets the upload target.
     *
     * @return The object into which uploaded data is written.
     */
    public Part getUploadImage() {
        return uploadImage;
    }

    /**
     * Sets the upload target.
     *
     * @param uploadImage The object into which uploaded data is written.
     */
    public void setUploadImage(Part uploadImage) {
        this.uploadImage = uploadImage;
    }

}
