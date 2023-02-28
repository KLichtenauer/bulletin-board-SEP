package de.schwarzes_brett.test_util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A utility class for injecting the testing data into the test database.
 *
 * @author Tim-Florian Feulner
 */
public final class TestDataInjector {

    private static final String PROCESS_ID_PLACEHOLDER = "{ID}";
    private static final Logger LOGGER = Logger.getLogger(TestDataInjector.class.getName());

    private TestDataInjector() {}

    /**
     * Write the test data into the database.
     *
     * @param connection A connection to the database.
     * @param skriptPath The path of the sql script which gets loaded.
     * @throws IOException  If the test data script could not be opened.
     * @throws SQLException If the loaded script could not be executed.
     */
    public static void performInjection(Connection connection, String skriptPath) throws IOException, SQLException {
        LOGGER.info("Inserting testing data into database.");
        String script = loadScript(skriptPath);

        String replacement = TestEnvironment.getProcessId() != null ? TestEnvironment.getProcessId() : "";
        script = script.replace(PROCESS_ID_PLACEHOLDER, replacement);

        runScript(connection, script);
        LOGGER.info("Successfully inserted testing data.");
    }

    private static String loadScript(String filename) throws IOException {
        try (InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename)) {
            if (stream == null) {
                throw new IOException("Could not open database script " + filename);
            }
            try (InputStreamReader inputStreamReader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                    return reader.lines().collect(Collectors.joining("\n"));
                }
            }
        }
    }

    private static void runScript(Connection connection, String scriptCode) throws SQLException {
        connection.setAutoCommit(false);
        try (Statement statement = connection.createStatement()) {
            String[] queries = scriptCode.split(";");
            for (String query : queries) {
                statement.execute(query + ";");
            }
            connection.commit();
        }
    }

    /**
     * Blocks until the database schema of the application has been created.
     *
     * @param connection A connection to the database.
     * @throws SQLException         If there is an error with checking the schema existence.
     * @throws InterruptedException If the waiting is interrupted.
     */
    public static void waitForInitializedSchema(Connection connection) throws SQLException, InterruptedException {
        final int waitingTime = 50;
        boolean initilialized;

        connection.setAutoCommit(true);
        do {
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT * FROM pg_tables WHERE schemaname = 'schwarzes_brett';")) {
                    initilialized = result.next();
                }
            }
            if (!initilialized) {
                Thread.sleep(waitingTime);
            }
        } while (!initilialized);
    }

}
