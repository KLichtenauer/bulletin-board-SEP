package de.schwarzes_brett.data_access.transaction;

import de.schwarzes_brett.logging.LoggerProducer;

import java.util.logging.Logger;

/**
 * Factory for creating a {@code TransactionPsql} instance. Gets called by the service classes to get a transaction
 * for the needed DAOs.
 *
 * @author Daniel Lipp
 */
public final class TransactionFactory {

    private static final Logger LOGGER = LoggerProducer.get(TransactionFactory.class);

    private TransactionFactory() {}

    /**
     * Factory method for creating a {@code TransactionPsql} instance.
     *
     * @return The produced instance.
     */
    public static Transaction produce() {
        TransactionPsql transaction = new TransactionPsql();
        LOGGER.finest("TransactionPsql was created.");
        return transaction;
    }

}
