package de.schwarzes_brett.data_access.config;

import de.schwarzes_brett.logging.LoggerProducer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides access to the application configuration as configured in the configuration file.
 *
 * @author Tim-Florian Feulner
 */
public final class Config {

    private static final String CONFIG_PATH = "WEB-INF/config/config.properties";

    private static Config instance = null;

    private final Logger logger = LoggerProducer.get(Config.class);

    private Properties configProperties = null;

    private Config() {}

    /**
     * Provides the instance of this singleton class.
     *
     * @return The configuration instance.
     */
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    /**
     * Initializes the config by reading from the configuration file.
     *
     * @param resourceFetcher A function for accessing resource files.
     */
    public void init(Function<String, InputStream> resourceFetcher) {
        configProperties = new Properties();
        try (InputStream input = resourceFetcher.apply(CONFIG_PATH)) {
            if (input == null) {
                throw new FileNotFoundException("Could not find the file " + CONFIG_PATH + ".");
            }
            try (InputStreamReader configReader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                configProperties.load(configReader);
            }
            logger.fine("Application configuration loaded.");
        } catch (IOException e) {
            String errorText = "Application configuration could not be loaded from " + CONFIG_PATH + ".";
            logger.severe(errorText);
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(errorText, e);
        }
    }

    /**
     * Returns the value of the config parameter corresponding to the given key.
     *
     * @param key The identifying value of certain requested config value.
     * @return The requested config value.
     */
    public String get(String key) {
        if (configProperties == null) {
            throw new IllegalStateException("The application configuration must be initialized first.");
        } else if (key == null) {
            throw new IllegalArgumentException("The key is null.");
        } else {
            logger.finest("Accessed configuration entry with key " + key + ".");

            String result = configProperties.getProperty(key);
            if (result == null) {
                String message = "The configuration value for the key " + key + " was not found.";
                logger.severe(message);
                throw new IllegalArgumentException(message);
            } else {
                return result;
            }
        }
    }

}
