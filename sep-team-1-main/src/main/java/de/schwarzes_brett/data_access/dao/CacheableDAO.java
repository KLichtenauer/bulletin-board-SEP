package de.schwarzes_brett.data_access.dao;

/**
 * All DAOs must implement this interface to save their changes to their cache, if they have one.
 *
 * @author Daniel Lipp
 */
public interface CacheableDAO {
    /**
     * Commits the changes made by this object to the cache, if this object supports caching. Otherwise, the method will do nothing.
     */
    void commitChangesToCache();
}
