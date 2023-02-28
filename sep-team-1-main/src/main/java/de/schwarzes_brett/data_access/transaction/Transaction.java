package de.schwarzes_brett.data_access.transaction;

/**
 * Provides a data access transaction. Currently, each DAO gets a transaction to be able to access the data.
 * After this transaction has been committed or aborted it is not to be used again.
 */
public interface Transaction extends AutoCloseable {

    /**
     * Commits transaction to the data storage and saves changes to the caches, if the used DAOs use a cache.
     */
    void commit();

    /**
     * Aborts the current transaction.
     */
    void abort();

    /**
     * Closes this resource.
     */
    void close();

}
