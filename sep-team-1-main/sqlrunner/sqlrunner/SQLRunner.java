package sqlrunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Simple command line utility for executing SQL commands (DDL and DML).
 *
 * @author Tim-Florian Feulner
 */
public final class SQLRunner {

    private static final Logger LOGGER = Logger.getLogger(SQLRunner.class.getName());
    private static final String DB_DRIVER = "org.postgresql.Driver";

    private SQLRunner() {}

    /**
     * The main method.
     *
     * @param args The command line arguments.
     * @throws SQLException If the execution was not successful.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    public static void main(String[] args) throws SQLException {
        String connectionUrl = args[0];
        String username = args[1];
        String password = args[2];
        String sqlCode = args[3];

        Properties props = createProperties(username, password);

        try (Connection connection = createConnection(connectionUrl, props)) {
            LOGGER.info("Executing SQL code:\n" + sqlCode);
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(sqlCode);
            }
        } catch (SQLException e) {
            LOGGER.severe("An SQLException occurred while executing the script.");
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }
    }

    private static Properties createProperties(String username, String password) {
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        props.setProperty("ssl", "true");
        props.setProperty("sslfactory", "org.postgresql.ssl.DefaultJavaSSLFactory");
        return props;
    }

    private static Connection createConnection(String connectionUrl, Properties props) throws SQLException {
        try {
            System.out.println("Loading JDBC Driver");
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found");
            throw new Error("JDBC Driver not found", e);
        }
        return DriverManager.getConnection(connectionUrl, props);
    }
}
