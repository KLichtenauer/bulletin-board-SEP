package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.ImageDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Provides a service to the backing layer to manage images. Images can be inserted, fetched and deleted.
 *
 * @author Tim-Florian Feulner
 */
@Named
@RequestScoped
public class ImageService implements Serializable {

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
    public ImageService() {}

    /**
     * Fetches an image.
     *
     * @param image The {@code ImageDTO} instance which is being filled with the fetched image.
     * @return {@code true} iff the image fetch was successful.
     */
    public boolean fetchImage(ImageDTO image) {
        logger.finest("Fetching image " + image.getId() + ".");
        try (Transaction transaction = TransactionFactory.produce()) {
            if (DAOFactory.getImageDAO(transaction).imageExists(image)) {
                DAOFactory.getImageDAO(transaction).fetchImage(image);
                transaction.commit();
                return true;
            } else {
                logger.info("Tried to access invalid image with id " + image.getId() + ".");
                transaction.abort();
                return false;
            }
        }
    }


    /**
     * Fetches a default image.
     *
     * @param image The {@code ImageDTO} instance which is being filled with the fetched image.
     */
    public void fetchDefaultImage(ImageDTO image) {
        logger.finest("Fetching default image " + image.getDefaultImageType() + ".");
        DAOFactory.getDefaultImageDAO().fetchImage(image);
    }

    /**
     * Inserts an image.
     *
     * @param image The image to be inserted.
     */
    public void insertImage(ImageDTO image) {
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getImageDAO(transaction).insertImage(image);
            transaction.commit();
        }
    }

    /**
     * Deletes an given image.
     *
     * @param image The image to be deleted.
     */
    public void deleteImage(ImageDTO image) {
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getImageDAO(transaction).deleteImage(image);
            transaction.commit();
        }
    }

}
