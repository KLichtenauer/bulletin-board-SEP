package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.logging.LoggerProducer;

import java.util.logging.Logger;

/**
 * Provides different DAOs.
 *
 * @author Daniel Lipp
 */
public final class DAOFactory {

    private static final Logger LOGGER = LoggerProducer.get(DAOFactory.class);

    private DAOFactory() {}

    /**
     * Factory method for new {@code AdDOA} instance.
     *
     * @param transaction The transaction needed for the database access.
     * @return Instance of the fetched {@code AdDOA} object.
     */
    public static AdDAO getAdDAO(Transaction transaction) {
        TransactionPsql transactionPsql = (TransactionPsql) transaction;
        AdDAOPsql dao = new AdDAOPsql(transactionPsql);
        LOGGER.finest("Created new AdDAOPsql");
        return dao;
    }

    /**
     * Factory method for new {@code ApplicationSettingsDAO} instance.
     *
     * @param transaction The transaction needed for the database access.
     * @return Instance of the fetched {@code  ApplicationSettingsDAO} object.
     */
    public static ApplicationSettingsDAO getApplicationSettingsDAO(Transaction transaction) {
        TransactionPsql transactionPsql = (TransactionPsql) transaction;
        ApplicationSettingsDAOPsql dao = new ApplicationSettingsDAOPsql(transactionPsql);
        transactionPsql.registerDAO(dao);
        LOGGER.finest("Created new ApplicationSettingsDAOPsql");
        return dao;
    }

    /**
     * Factory method for new {@code CategoryDAO} instance.
     *
     * @param transaction The transaction needed for the database access.
     * @return Instance of the fetched {@code  CategoryDAO} object.
     */
    public static CategoryDAO getCategoryDAO(Transaction transaction) {
        TransactionPsql transactionPsql = (TransactionPsql) transaction;
        CategoryDAOPsql dao = new CategoryDAOPsql(transactionPsql);
        LOGGER.finest("Created new CategoryDAOPsql");
        return dao;
    }

    /**
     * Factory method for new {@code MessageDAO} instance.
     *
     * @param transaction The transaction needed for the database access.
     * @return Instance of the fetched {@code  MessageDAO} object.
     */
    public static MessageDAO getMessageDAO(Transaction transaction) {
        TransactionPsql transactionPsql = (TransactionPsql) transaction;
        MessageDAOPsql dao = new MessageDAOPsql(transactionPsql);
        LOGGER.finest("Created new MessageDAOPsql");
        return dao;
    }

    /**
     * Factory method for new {@code UserDAO} instance.
     *
     * @param transaction The transaction needed for the database access.
     * @return Instance of the fetched {@code  UserDAO} object.
     */
    public static UserDAO getUserDAO(Transaction transaction) {
        TransactionPsql transactionPsql = (TransactionPsql) transaction;
        UserDAOPsql dao = new UserDAOPsql(transactionPsql);
        transactionPsql.registerDAO(dao);
        LOGGER.finest("Created new UserDAOPsql");
        return dao;
    }

    /**
     * Factory method for new {@code RatingDAO} instance.
     *
     * @param transaction The transaction needed for the database access.
     * @return Instance of the fetched {@code  UserDAO} object.
     */
    public static RatingDAO getRatingDAO(Transaction transaction) {
        TransactionPsql transactionPsql = (TransactionPsql) transaction;
        RatingDAOPsql dao = new RatingDAOPsql(transactionPsql);
        LOGGER.finest("Created new RatingDAOPsql");
        return dao;
    }

    /**
     * Factory method for new {@code ImageDAO} instance.
     *
     * @param transaction The transaction needed for the database access.
     * @return Instance of the fetched {@code  UserDAO} object.
     */
    public static ImageDAO getImageDAO(Transaction transaction) {
        TransactionPsql transactionPsql = (TransactionPsql) transaction;
        ImageDAOPsql dao = new ImageDAOPsql(transactionPsql);
        LOGGER.finest("Created new ImageDAOPsql");
        return dao;
    }

    /**
     * Factory method for new {@code FollowDAO} instance.
     *
     * @param transaction The transaction needed for the database access.
     * @return Instance of the fetched {@code  UserDAO} object.
     */
    public static FollowDAO getFollowDAO(Transaction transaction) {
        TransactionPsql transactionPsql = (TransactionPsql) transaction;
        FollowDAOPsql dao = new FollowDAOPsql(transactionPsql);
        LOGGER.finest("Created new FollowDAOPsql");
        return dao;
    }

    /**
     * Factory method for new {@code DefaultImageDAO} instance.
     *
     * @return Instance of the fetched {@code DefaultImageDAO} object.
     */
    public static DefaultImageDAO getDefaultImageDAO() {
        LOGGER.finest("Accessed DefaultImageDAOImpl instance");
        return DefaultImageDAOImpl.getInstance();
    }

}
