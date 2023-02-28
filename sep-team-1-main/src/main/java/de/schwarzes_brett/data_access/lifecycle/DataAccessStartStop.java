package de.schwarzes_brett.data_access.lifecycle;

import de.schwarzes_brett.data_access.config.Config;
import de.schwarzes_brett.data_access.dao.DefaultImageDAOImpl;
import de.schwarzes_brett.data_access.db.ConnectionPoolPsql;
import de.schwarzes_brett.logging.LoggerProducer;

import java.io.InputStream;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Manages system start and stop on the data-access layer.
 *
 * @author Tim-Florian Feulner
 */
public final class DataAccessStartStop {

    private static final Logger LOGGER = LoggerProducer.get(DataAccessStartStop.class);

    /**
     * Private constructor, as this class is a utility class.
     */
    private DataAccessStartStop() {}

    /**
     * Initializes the data access.
     *
     * @param resourceFetcher The callback to retrieve a resource file.
     */
    public static void init(Function<String, InputStream> resourceFetcher) {
        LOGGER.finest("Starting the startup process of the data access.");
        Config.getInstance().init(resourceFetcher);
        ConnectionPoolPsql.getInstance().init();
        DBScheme.init(resourceFetcher);
        DefaultImageDAOImpl.getInstance().init(resourceFetcher);
        LOGGER.finest("Data access startup complete.");
    }

    /**
     * Stops the data access.
     */
    public static void destroy() {
        LOGGER.finest("Starting the shutdown process of the data access.");
        ConnectionPoolPsql.getInstance().destroy();
        LOGGER.finest("Data access shutdown complete.");
    }

}
