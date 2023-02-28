package de.schwarzes_brett.dto;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the data of an image.
 *
 * @author Valentin Damjantschitsch.
 */
public class ImageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The id of the image.
     */
    private Long id;

    /**
     * The type of this default image.
     */
    private DefaultImageType defaultImageType;

    /**
     * The storeStream of the image.
     */
    private transient InputStream storeStream;

    /**
     * The retrieveStream of the image.
     */
    private transient OutputStream retrieveStream;


    /**
     * Default constructor.
     */
    public ImageDTO() {
    }

    /**
     * Copy constructor. This constructor not copy streams.
     *
     * @param toCopy The {@code ImageDTO} to copy.
     */
    public ImageDTO(ImageDTO toCopy) {
        this.id = toCopy.id;
        this.defaultImageType = toCopy.defaultImageType;
    }

    /**
     * Getter for the id of the image.
     *
     * @return The id of the image.
     */
    public Long getId() {
        return id;
    }

    /**
     * Setter for the id of the image.
     *
     * @param id The id to be set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Getter for the default image type.
     *
     * @return The default image type of the image.
     */
    public DefaultImageType getDefaultImageType() {
        return defaultImageType;
    }

    /**
     * Setter for the default image type of the image.
     *
     * @param defaultImageType The default image type to be set.
     */
    public void setDefaultImageType(DefaultImageType defaultImageType) {
        this.defaultImageType = defaultImageType;
    }

    /**
     * Getter for the storeStream of the image.
     *
     * @return The id of the image.
     */
    public InputStream getStoreStream() {
        return storeStream;
    }

    /**
     * Setter for the storeStream of the image.
     *
     * @param storeStream The storeStream to be set.
     */
    public void setStoreStream(InputStream storeStream) {
        this.storeStream = storeStream;
    }

    /**
     * Getter for the retrieveStream of the image.
     *
     * @return The retrieveStream of the image.
     */
    public OutputStream getRetrieveStream() {
        return retrieveStream;
    }

    /**
     * Setter for the retrieveStream of the image.
     *
     * @param retrieveStream The retrieveStream to be set.
     */
    public void setRetrieveStream(OutputStream retrieveStream) {
        this.retrieveStream = retrieveStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ImageDTO imageDTO = (ImageDTO) o;
        return Objects.equals(id, imageDTO.id) && defaultImageType == imageDTO.defaultImageType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, defaultImageType, storeStream, retrieveStream);
    }

    /**
     * The type of default image.
     */
    public enum DefaultImageType {

        /**
         * The default avatar image for users.
         */
        AVATAR,

        /**
         * The default logo image of the application.
         */
        LOGO,

        /**
         * The default thumbnail image for ads.
         */
        THUMBNAIL
    }

}
