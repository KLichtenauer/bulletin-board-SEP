package de.schwarzes_brett.test_util;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Manages an embedded test database.
 *
 * @author Tim-Florian Feulner
 */
public class DBServerLifecycle {

    private final Logger logger = Logger.getLogger(DBServerLifecycle.class.getName());

    private EmbeddedPostgres pg;
    private int databasePort;

    /**
     * Initializes and starts this test database.
     *
     * @throws InterruptedException If the wait for the port to be free was interrupted.
     * @throws IOException          If the config could not be read or if the database could not be started.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public void init() throws InterruptedException, IOException {
        readDatabasePort();

        // Wait until database port is not blocked.
        while (isDatabasePortBlocked()) {
            Thread.sleep(100);
        }

        pg = EmbeddedPostgres.builder().setPort(databasePort).start();
        logger.info("EmbeddedPostgres started on port " + databasePort + ".");
    }

    private void readDatabasePort() throws IOException {
        Properties config = new Properties();
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("WEB-INF/config/config.properties")) {
            if (input == null) {
                throw new RuntimeException("Could not find config file.");
            }
            try (InputStreamReader configReader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                config.load(configReader);
            }
        }
        databasePort = Integer.parseInt(config.getProperty("DB_PORT"));
    }

    private boolean isDatabasePortBlocked() {
        try (Socket ignored = new Socket("localhost", databasePort)) {
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Shuts down this test database.
     *
     * @throws IOException If some error occurs.
     */
    public void destroy() throws IOException {
        pg.close();
        logger.info("EmbeddedPostgres stopped.");
    }

    /**
     * Provides an SQL connection to the test database.
     *
     * @return A connection to the database.
     * @throws SQLException If an access error occures.
     */
    public Connection getConnection() throws SQLException {
        return pg.getPostgresDatabase().getConnection();
    }

}
