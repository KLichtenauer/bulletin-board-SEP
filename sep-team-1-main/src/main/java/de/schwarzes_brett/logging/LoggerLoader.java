package de.schwarzes_brett.logging;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Loads the logger configuration from the filesystem.
 *
 * @author Tim-Florian Feulner
 */
public final class LoggerLoader {

    /**
     * The logger for usage in this class.
     */
    private static final Logger LOGGER = LoggerProducer.get(LoggerLoader.class);

    /**
     * The path to the logging configuration properties file.
     */
    private static final String LOGGING_CONFIG_PATH = "WEB-INF/config/logging.properties";

    /**
     * Private constructor, as this class is a utility class.
     */
    private LoggerLoader() {}

    /**
     * Initializes the logger {@code java.util.logging}.
     *
     * @param resourceFetcher The callback to retrieve a resource file.
     */
    public static void init(Function<String, InputStream> resourceFetcher) {
        try (InputStream input = resourceFetcher.apply(LOGGING_CONFIG_PATH)) {
            if (input == null) {
                throw new FileNotFoundException("Could not find the file " + LOGGING_CONFIG_PATH + ".");
            }
            LogManager.getLogManager().readConfiguration(input);
            LOGGER.fine("Logging configuration loaded.");
        } catch (IOException e) {
            LOGGER.warning("Logging configuration could not be loaded, falling back to default configuration.");
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
    }

}
