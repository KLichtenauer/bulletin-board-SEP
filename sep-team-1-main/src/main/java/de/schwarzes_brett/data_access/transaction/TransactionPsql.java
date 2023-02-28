package de.schwarzes_brett.data_access.transaction;

import de.schwarzes_brett.data_access.dao.CacheableDAO;
import de.schwarzes_brett.data_access.db.ConnectionPoolPsql;
import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.logging.LoggerProducer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides a PostgresSql database transaction. Any DAO needs to have a transaction included.
 *
 * @author Daniel Lipp
 */
public class TransactionPsql implements Transaction {

    private final Logger logger = LoggerProducer.get(TransactionPsql.class);
    private final List<CacheableDAO> daoList = new ArrayList<>();
    private Connection connection;
    private boolean isTerminated;
    private boolean isInitiated;

    /**
     * Default constructor.
     */
    public TransactionPsql() {
        isInitiated = false;
        isTerminated = false;
    }

    private static synchronized void commitSynchronised(TransactionPsql t, List<CacheableDAO> daoList) {
        if (t.checkInitiatedAndNotTerminated()) {
            t.logger.finest("Starting commit of transaction...");
            try {
                t.connection.commit();
                t.logger.fine("Transaction was committed.");
                for (CacheableDAO cDao : daoList) {
                    cDao.commitChangesToCache();
                }
            } catch (SQLException e) {
                t.logger.log(Level.WARNING, "transaction could not be committed");
                throw new DataStorageAccessException("Error while committing transaction.", e);
            } finally {
                ConnectionPoolPsql.getInstance().releaseConnection(t.connection);
                t.logger.fine("connection released to pool");
            }
            t.terminate();
        }
    }

    /**
     * Registers a DAO to be used for the commit.
     *
     * @param dao The dao to be registered.
     */
    public void registerDAO(CacheableDAO dao) {
        daoList.add(dao);
    }

    /**
     * Getter for the current connection to the database.
     *
     * @return The current connection to the database.
     */
    public Connection getConnection() {
        if (connection == null) {
            connection = ConnectionPoolPsql.getInstance().getConnection();
            isInitiated = true;
        }
        return connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() {
        commitSynchronised(this, daoList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void abort() {
        if (checkInitiatedAndNotTerminated()) {
            logger.warning("Starting rollback of transaction.");
            try {
                connection.rollback();
                logger.fine("Rollback of transaction was successful.");
            } catch (SQLException e) {
                logger.log(Level.WARNING, "transaction could not be aborted. Error during rollback");
                // Database is probably not available since otherwise there would be no SqlException
                // -> no need to throw exception since connection is checked during releaseConnection
            } finally {
                ConnectionPoolPsql.getInstance().releaseConnection(connection);
                logger.fine("connection released to pool");
            }
            terminate();
        }
    }

    /**
     * checks if transaction was already terminated and logs if it was already terminated before the call.
     *
     * @return {@code true} if the connection was aborted or committed.
     */
    private boolean checkInitiatedAndNotTerminated() {
        if (!isInitiated) {
            logger.fine("Connection was not fetched from pool.");
            return false;
        }
        if (isTerminated) {
            logger.fine("Transaction was already committed or aborted.");
            return false;
        }
        return true;
    }

    /**
     * Marks the transaction as terminated and logs it.
     */
    private void terminate() {
        isTerminated = true;
        logger.finest("End of transaction.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        abort();
    }
}
