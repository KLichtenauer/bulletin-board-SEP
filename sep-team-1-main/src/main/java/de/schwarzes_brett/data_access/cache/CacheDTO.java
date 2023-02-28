package de.schwarzes_brett.data_access.cache;

/**
 * Used to store values in a cache with a time indicating if the stored value is still valid.
 *
 * @param <T>           Type of the objects that are stored.
 * @param insertionTime Time of insertion in milliseconds.
 * @param dto           Value to be stored.
 * @author Daniel Lipp
 */
record CacheDTO<T>(long insertionTime, T dto) {}
