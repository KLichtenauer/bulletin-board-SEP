package de.schwarzes_brett.business_logic.periodic;

import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.logging.LoggerProducer;

import java.util.logging.Logger;

/**
 * Used for periodic cleanup of unused images in the data storage.
 *
 * @author Tim-Florian Feulner
 */
final class ImageCleaner {

    /**
     * The rate at which {@link ImageCleaner#cleanUnusedImages()} should be called, in seconds.
     */
    public static final int EXECUTION_RATE = 60 * 20;

    /**
     * The maximum age of an unused image, in seconds. All unused images that are older than this removed.
     */
    private static final int MAX_UNUSED_IMAGE_AGE = 60 * 60 * 24;

    private static final Logger LOGGER = LoggerProducer.get(ImageCleaner.class);

    private ImageCleaner() {}

    /**
     * Cleans unused images in the data storage.
     */
    public static void cleanUnusedImages() {
        LOGGER.fine("Cleaning unused images.");
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getImageDAO(transaction).cleanUnusedImages(MAX_UNUSED_IMAGE_AGE);
            transaction.commit();
        }
    }

}
