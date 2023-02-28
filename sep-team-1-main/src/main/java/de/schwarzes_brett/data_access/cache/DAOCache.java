package de.schwarzes_brett.data_access.cache;

import java.time.Instant;
import java.util.HashMap;

/**
 * A cache to reduce loading times. The time a stored value is valid can be set when creating this object. Extending classes should be singleton.
 *
 * @param <T> Type of the stored values.
 * @author Daniel Lipp
 */
public abstract class DAOCache<T> {

    /**
     * The default time for which stored values are valid if no other time is specified when specifying a subclass.
     */
    protected static final long DEFAULT_CACHE_TIME = 5 * 60 * 1000;
    private final HashMap<Integer, CacheDTO<T>> keyValueStore;
    private final long cacheTime;

    /**
     * Creates a new object with the default cache time.
     */
    protected DAOCache() {
        keyValueStore = new HashMap<>();
        cacheTime = DEFAULT_CACHE_TIME;
    }

    /**
     * Creates a new cache with the given cache storage time.
     *
     * @param cacheTime The time for which an entry is valid in milliseconds.
     */
    protected DAOCache(long cacheTime) {
        keyValueStore = new HashMap<>();
        this.cacheTime = cacheTime;
    }

    /**
     * Returns the value stored for the given id.
     *
     * @param id Key to be fetched.
     * @return Value stored for the given key. If no value is stored, then {@code null} is returned.
     */
    protected synchronized T getValue(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Id can't be null");
        }
        CacheDTO<T> cachedValue = keyValueStore.get(id);
        if (cachedValue == null || Instant.now().toEpochMilli() - cachedValue.insertionTime() > cacheTime) {
            return null;
        } else {
            return cachedValue.dto();
        }
    }

    /**
     * Stores a value in the cache for the given id.
     *
     * @param id    Key for which the value is stored.
     * @param value Value to be stored.
     */
    protected synchronized void setValue(Integer id, T value) {
        CacheDTO<T> cacheValue = new CacheDTO<>(Instant.now().toEpochMilli(), value);
        keyValueStore.put(id, cacheValue);
    }

    /**
     * Removes a value from the cache.
     *
     * @param id Key of the value to be removed.
     */
    protected synchronized void deleteValue(Integer id) {
        keyValueStore.remove(id);
    }

    /**
     * Clears all values that are too old to be fetched.
     *
     * @return Amount of unused old values that were removed.
     */
    public synchronized int clearOldValues() {
        int deleted = 0;
        for (Integer key : keyValueStore.keySet()) {
            T value = getValue(key);
            if (value == null) {
                // value is null or too old
                deleteValue(key);
                ++deleted;
            }
        }
        return deleted;
    }
}
