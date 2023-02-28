package de.schwarzes_brett.data_access.periodic;

import de.schwarzes_brett.data_access.cache.ApplicationSettingsDaoCache;
import de.schwarzes_brett.data_access.cache.UserDaoCache;
import de.schwarzes_brett.data_access.db.ConnectionPoolPsql;
import de.schwarzes_brett.logging.LoggerProducer;

import java.util.logging.Logger;

/**
 * Used for periodic maintenance of the data access layer.
 *
 * @author Tim-Florian Feulner
 */
public final class DataAccessMaintainer {

    /**
     * The rate at which {@link DataAccessMaintainer#maintain()} should be called, in seconds.
     */
    public static final int EXECUTION_RATE = 60 * 5;

    private static final Logger LOGGER = LoggerProducer.get(DataAccessMaintainer.class);

    private DataAccessMaintainer() {}

    /**
     * Maintains the data access layer. This function should be called periodically.
     */
    public static void maintain() {
        LOGGER.fine("Performing data access maintenance.");

        ConnectionPoolPsql.getInstance().checkConnections();

        int userCacheClearCount = UserDaoCache.getInstance().clearOldValues();
        LOGGER.finer("Cleaned " + userCacheClearCount + " entries from the user cache.");

        int applicationSettingsClearCount = ApplicationSettingsDaoCache.getInstance().clearOldValues();
        LOGGER.finer("Cleaned " + applicationSettingsClearCount + " entries from the application settings cache.");

        LOGGER.finer("Finished data access maintenance.");
    }

}
