package de.schwarzes_brett.data_access.lifecycle;

import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.logging.LoggerProducer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sets up the database schema if it’s empty or not ready
 * to use the system.
 *
 * @author Daniel Lipp
 */
public final class DBScheme {

    private static final Logger LOGGER = LoggerProducer.get(DBScheme.class);
    private static final String BASE_PATH = "WEB-INF/classes/de/schwarzes_brett/data_access/lifecycle/sql/";
    private static final String DB_INIT_PATH = BASE_PATH + "schemeInit.sql";

    private DBScheme() {}

    /**
     * Initializes the db scheme.
     *
     * @param resourceFetcher The callback to retrieve a resource file.
     */
    public static void init(Function<String, InputStream> resourceFetcher) {
        String sqlScript;
        try (InputStream input = resourceFetcher.apply(DB_INIT_PATH)) {
            if (input == null) {
                throw new FileNotFoundException("Could not find the file " + DB_INIT_PATH + ".");
            }
            sqlScript = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            LOGGER.fine("DB init loaded.");
        } catch (IOException e) {
            String errorText = "DB init could not be loaded from " + DB_INIT_PATH + ".";
            LOGGER.severe(errorText);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(errorText, e);
        }
        LOGGER.fine("Starting setup.");
        try (TransactionPsql transaction = (TransactionPsql) TransactionFactory.produce()) {
            Connection connection = transaction.getConnection();
            if (!schemaExists(connection)) {
                LOGGER.info("Schema did not exist so it is created.");
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(sqlScript);
                    LOGGER.info("Schema was created.");
                }
            }
            transaction.commit();
        } catch (SQLException | DataStorageAccessException e) {
            String errorText = "DB schema could not be initialised.";
            LOGGER.severe(errorText);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(errorText, e);
        }
        LOGGER.finest("DB schema setup complete");
    }

    /**
     * Checks if the schema already exists.
     *
     * @param connection connection to run the statement on.
     * @return True if the schema already exists.
     * @throws SQLException is thrown when access to db is lost.
     */
    private static boolean schemaExists(Connection connection) throws SQLException {
        LOGGER.fine("Check if schema exists.");
        boolean exists = false;
        try (Statement statement = connection.createStatement()) {
            try (ResultSet set = statement.executeQuery(
                    "SELECT EXISTS (SELECT 1 FROM information_schema.tables "
                    + "WHERE table_schema = 'schwarzes_brett' AND table_name = 'contact_data');")) {
                if (set.next()) {
                    exists = set.getBoolean(1);
                }
            }
        }
        LOGGER.fine("Schema exist = " + exists);
        return exists;
    }

}
