package de.schwarzes_brett.business_logic.periodic;

import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.logging.LoggerProducer;

import java.util.logging.Logger;

/**
 * Used for periodic cleanup of expired verifications in the data storage.
 *
 * @author Tim-Florian Feulner
 */
final class VerificationCleaner {

    /**
     * The rate at which {@link VerificationCleaner#cleanUnverifiedUsers()} should be called, in seconds.
     */
    public static final int EXECUTION_RATE = 60 * 60 * 2;

    /**
     * The maximum age of an unverified user, in seconds. All unverified users that are older get removed.
     */
    private static final int MAX_UNVERIFIED_USER_AGE = 60 * 60 * 48;

    private static final Logger LOGGER = LoggerProducer.get(VerificationCleaner.class);

    private VerificationCleaner() {}

    /**
     * Cleans email-verifications which are at least {@code MAX_UNVERIFIED_USER_AGE} old.
     */
    public static void cleanUnverifiedUsers() {
        LOGGER.finer("Started periodic verification cleanup.");
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getUserDAO(transaction).cleanUnverifiedUsers(MAX_UNVERIFIED_USER_AGE);
            transaction.commit();
        }
    }

}
