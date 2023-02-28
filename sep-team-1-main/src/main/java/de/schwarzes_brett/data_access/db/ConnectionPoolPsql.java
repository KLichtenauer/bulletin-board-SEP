package de.schwarzes_brett.data_access.db;

import de.schwarzes_brett.data_access.config.Config;
import de.schwarzes_brett.data_access.exception.DataStorageUnavailableException;
import de.schwarzes_brett.logging.LoggerProducer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages connections to the PostgresSql database. Connections are given to transactions and used by DAOs to access database.
 *
 * @author Jonas Elsper
 */
public final class ConnectionPoolPsql {

    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final long TIMEOUT_MILLIS = 3000;
    private static ConnectionPoolPsql instance;
    private final Logger logger = LoggerProducer.get(ConnectionPoolPsql.class);
    private final Queue<Connection> connections = new LinkedList<>();
    private final List<Connection> borrowedConnections = new LinkedList<>();
    private Properties connectionProps;


    private ConnectionPoolPsql() {}

    /**
     * Getter for the connection pool instance.
     *
     * @return The connection pool instance.
     */
    public static synchronized ConnectionPoolPsql getInstance() {
        if (instance == null) {
            instance = new ConnectionPoolPsql();
        }
        return instance;
    }

    /**
     * Initializes the connection pool and all connections.
     */
    public synchronized void init() {
        Config config = Config.getInstance();
        createConnProps(config);
        loadDriver();
        int maxConnections = Integer.parseInt(config.get("DB_CONNECTION_COUNT"));
        for (int i = 0; i < maxConnections; i++) {
            logger.log(Level.FINEST, "Start creating Connection " + (i + 1) + ".");
            connections.offer(createConnection());
            logger.log(Level.FINEST, "Created Connection " + (i + 1) + " successfully.");
        }
    }

    private void createConnProps(Config config) {
        connectionProps = new Properties();
        connectionProps.setProperty("user", config.get("DB_USER"));
        connectionProps.setProperty("password", config.get("DB_PASSWORD"));
        connectionProps.setProperty("ssl", config.get("DB_USE_SSL"));
        connectionProps.setProperty("sslfactory", config.get("DB_SSL_FACTORY"));
    }

    private void loadDriver() {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "DB Driver not found.");
            throw new Error("DB Driver not found.", e);
        }
    }

    private Connection createConnection() {
        Config config = Config.getInstance();
        Connection conn;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:postgresql://" + config.get("DB_HOST") + ":" + config.get("DB_PORT") + "/" + config.get("DB_NAME"), connectionProps);
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Could not create a new connection.", e);
            throw new DataStorageUnavailableException(e);
        }
        return conn;
    }

    /**
     * Destroys all connections in and from the connection pool.
     */
    public synchronized void destroy() {
        for (Connection conn : connections) {
            logger.log(Level.FINEST, "Try to close connection.");
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Failed to close Connection.", e);
                }
                logger.log(Level.FINEST, "Closed connection successfully.");
            }
        }
        for (Connection conn : borrowedConnections) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Borrowed Connection from Connection Pool could not be rollback successfully.", e);
                }
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Borrowed Connection from Connection Pool could not be closed.", e);
                }
            }
        }
        connections.clear();
        borrowedConnections.clear();
    }

    /**
     * Returns an active connection from the connection pool.
     *
     * @return An active connection to the database.
     */
    public synchronized Connection getConnection() {
        long startMillis = System.currentTimeMillis();
        long stopMillis = startMillis + TIMEOUT_MILLIS;
        while (connections.isEmpty() && System.currentTimeMillis() < stopMillis) {
            try {
                wait(stopMillis - System.currentTimeMillis());
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Thread was interrupted while waiting for connection.");
            }
        }
        if (!connections.isEmpty()) {
            Connection conn = connections.poll();
            borrowedConnections.add(conn);
            return conn;
        } else {
            logger.log(Level.SEVERE, "No Connection available in the Pool");
            throw new DataStorageUnavailableException("No Connection available in the Pool");
        }
    }

    /**
     * Releases an active connection to the connection pool.
     * The Connection gets validated and rolled back before it gets released.
     *
     * @param conn The connection to be released.
     */
    public synchronized void releaseConnection(Connection conn) {
        logger.finest("Start trying to release Connection to Connection Pool." + " available conns: " + connections.size());
        if (borrowedConnections.remove(conn)) {
            connections.offer(checkConnection(conn));
            logger.finest("Release Connection to Connection Pool successful." + " available conns: " + connections.size());
        } else {
            try {
                conn.close();
                logger.finest("Release Connection to Connection Pool successful." + " available conns: " + connections.size());
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Failed to close released Connection. Connection was closed because Connection Pool is full", e);
            }
        }
    }

    /**
     * Validates all available Connections from the Connection Pool and replace them, if they are invalid.
     */
    public synchronized void checkConnections() {
        for (int i = 0; i < connections.size(); i++) {
            Connection conn = connections.poll();
            conn = checkConnection(conn);
            connections.offer(conn);
        }
    }

    private Connection checkConnection(Connection conn) {
        if (conn == null) {
            conn = createConnection();
        } else {
            boolean isValid = false;
            try {
                isValid = conn.isValid(2);
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Connection could not be validated.", e);
            }
            if (!isValid) {
                conn = createConnection();
            } else {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Connection could not be rolled back", e);
                    conn = createConnection();
                }
            }
        }
        return conn;
    }
}
