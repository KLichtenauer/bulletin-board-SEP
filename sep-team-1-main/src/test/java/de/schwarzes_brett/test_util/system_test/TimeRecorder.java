package de.schwarzes_brett.test_util.system_test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class that perform time tracking for load tests.
 *
 * @author Tim-Florian Feulner
 */
final class TimeRecorder {

    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final Logger LOGGER = Logger.getLogger(TimeRecorder.class.getName());

    private static TimeRecorder instance;
    private final Map<String, Instant> startTimes = new HashMap<>();
    private Connection connection;

    private TimeRecorder() {
        Properties connectionProps = new Properties();
        Properties config = new Properties();
        readConfig(connectionProps, config);

        establishDBConnection(connectionProps, config);
        registerShutdownHook();
    }

    /**
     * Getter method for the singleton instance.
     *
     * @return The singleton instance.
     */
    public static TimeRecorder getInstance() {
        if (instance == null) {
            instance = new TimeRecorder();
        }
        return instance;
    }

    private void establishDBConnection(Properties connectionProps, Properties config) {
        loadDBDriver();
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + config.get("DB_HOST") + ":" + config.get("DB_PORT") + "/" + config.get("DB_NAME"), connectionProps);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(true);
            LOGGER.info("Opened connection for time recorder.");
        } catch (SQLException e) {
            String message = "Could not connect to database for load test execution time recording.";
            LOGGER.severe(message);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(message, e);
        }
    }

    private void readConfig(Properties connectionProps, Properties config) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("load_tests/WEB-INF/config/config.properties")) {
            if (input == null) {
                throw new RuntimeException("Could not read config.");
            }
            try (Reader configReader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                config.load(configReader);
                connectionProps.setProperty("user", (String) config.get("DB_USER"));
                connectionProps.setProperty("password", (String) config.get("DB_PASSWORD"));
                connectionProps.setProperty("ssl", (String) config.get("DB_USE_SSL"));
                connectionProps.setProperty("sslfactory", (String) config.get("DB_SSL_FACTORY"));
            }
        } catch (IOException e) {
            String message = "Could not read config.";
            LOGGER.severe(message);
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(message, e);
        }
    }

    private void loadDBDriver() {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new Error("DB Driver not found.");
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                connection.close();
                LOGGER.info("Closed connection for time recorder.");
            } catch (SQLException e) {
                LOGGER.severe("Could not close time recorder connection to database.");
            }
        }));
    }

    /**
     * Records the start time of the executed test.
     *
     * @param testName The name of the test.
     */
    public void recordStart(String testName) {
        startTimes.put(testName, Instant.now());
    }

    /**
     * Records the end time of the executed test.
     * Also writes the recorded data into the database.
     *
     * @param testName    The name of the test.
     * @param saveResults {@code true} iff the results of time recording should be saved.
     */
    public void recordEnd(String testName, boolean saveResults) {
        Instant end = Instant.now();
        Instant start = startTimes.remove(testName);

        if (saveResults && insertTestNameIntoDB(testName)) {
            Integer testId = retrieveTestIdFromDB(testName);
            if (testId != null) {
                insertTimeRecordIntoDB(testId, testName, end, start);
            }
        }
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private void insertTimeRecordIntoDB(int testId, String testName, Instant end, Instant start) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO schwarzes_brett_testing.execution_time(test_id, utc_start, utc_stop) VALUES (?, ?, ?);")) {
            statement.setInt(1, testId);
            statement.setTimestamp(2, Timestamp.from(start));
            statement.setTimestamp(3, Timestamp.from(end));

            int insertionCount = statement.executeUpdate();
            if (insertionCount != 1) {
                String message = "Did not insert time record of test " + testName + ".";
                LOGGER.severe(message);
                throw new RuntimeException(message);
            }
        } catch (SQLException e) {
            String message = "Could not insert time record of test " + testName + ".";
            LOGGER.log(Level.SEVERE, message, e);
            throw new RuntimeException(message, e);
        }
    }

    private Integer retrieveTestIdFromDB(String testName) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT id FROM schwarzes_brett_testing.test WHERE name = ?;")) {
            statement.setString(1, testName);
            try (ResultSet result = statement.executeQuery()) {
                boolean hasNext = result.next();
                if (hasNext) {
                    return result.getInt("id");
                } else {
                    LOGGER.severe("Could not find test " + testName + ".");
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not get id of test " + testName + ".", e);
            return null;
        }
    }

    private boolean insertTestNameIntoDB(String testName) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO schwarzes_brett_testing.test(name) VALUES (?) ON CONFLICT DO NOTHING;")) {
            statement.setString(1, testName);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not insert test name of test " + testName + ".", e);
            return false;
        }
    }

}
