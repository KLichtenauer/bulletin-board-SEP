package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.transaction.TransactionPsql;

/**
 * Saves references of all not completed PostgresSql database transactions. All DAOs inherit of BaseDAO.
 */
public abstract class BaseDAOPsql {

    /**
     * Transaction for database access.
     */
    private final TransactionPsql transaction;

    /**
     * Creates DAO with needed transaction.
     *
     * @param transaction The transaction needed for the database access.
     */
    public BaseDAOPsql(TransactionPsql transaction) {
        this.transaction = transaction;
    }

    /**
     * Returns the transaction of the DAO.
     *
     * @return The transaction.
     */
    protected TransactionPsql getTransaction() {
        return transaction;
    }

}
