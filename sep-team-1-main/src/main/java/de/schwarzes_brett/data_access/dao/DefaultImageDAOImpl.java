package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.logging.LoggerProducer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation to access the default images from file.
 * This class is a singleton to share the data that was read from file at application startup.
 *
 * @author Tim-Florian Feulner
 */
public final class DefaultImageDAOImpl implements DefaultImageDAO {

    private static final String DEFAULT_IMAGE_BASE_PATH = "WEB-INF/classes/de/schwarzes_brett/data_access/dao/default";

    private static DefaultImageDAOImpl instance = null;
    private final Map<ImageDTO.DefaultImageType, byte[]> storage = new TreeMap<>();

    private final Logger logger = LoggerProducer.get(DefaultImageDAOImpl.class);

    private DefaultImageDAOImpl() {}

    /**
     * Gets the singleton instance of this class.
     *
     * @return The sole instance.
     */
    public static DefaultImageDAOImpl getInstance() {
        if (instance == null) {
            instance = new DefaultImageDAOImpl();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void fetchImage(ImageDTO image) {
        byte[] imageData = storage.get(image.getDefaultImageType());

        boolean success = (imageData != null);
        try {
            if (success) {
                image.getRetrieveStream().write(imageData);
            }
        } catch (IOException e) {
            success = false;
        }

        if (!success) {
            String message = "Default image " + image.getDefaultImageType() + " could not be fetched.";
            logger.severe(message);
            throw new DataStorageAccessException(message);
        }
    }

    /**
     * Initializes the storage and reads default images into memory.
     *
     * @param resourceFetcher The callback to retrieve a resource file.
     */
    public void init(Function<String, InputStream> resourceFetcher) {
        logger.finest("Initializing the default image storage.");

        for (ImageDTO.DefaultImageType type : ImageDTO.DefaultImageType.values()) {
            String typeString = type.toString().charAt(0) + type.toString().substring(1).toLowerCase();
            readImage(type, DEFAULT_IMAGE_BASE_PATH + typeString + ".png", resourceFetcher);
        }

        logger.finest("Successfully initialized the default image storage.");
    }

    private void readImage(ImageDTO.DefaultImageType type, String filename, Function<String, InputStream> resourceFetcher) {
        try (InputStream input = resourceFetcher.apply(filename)) {
            storage.put(type, input.readAllBytes());
        } catch (IOException e) {
            String message = "Could not read default image " + type + " from " + filename + ".";
            logger.severe(message);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(message, e);
        }
    }

}
