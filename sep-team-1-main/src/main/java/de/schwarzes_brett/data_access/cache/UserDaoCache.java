package de.schwarzes_brett.data_access.cache;

import de.schwarzes_brett.dto.UserDTO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A cache for the users that are fetched by their id. Can be used to reduced loading times.
 *
 * @author Daniel Lipp
 */
public final class UserDaoCache extends DAOCache<UserDTO> {

    private static final UserDaoCache INSTANCE = new UserDaoCache();

    private UserDaoCache() {
        super();
    }

    /**
     * Getter for the cache instance.
     *
     * @return Only instance of this cache.
     */
    @SuppressFBWarnings("MS_EXPOSE_REP")
    public static UserDaoCache getInstance() {
        return INSTANCE;
    }

    /**
     * Fills the user if it was stored in this cache.
     *
     * @param user User who is identified by his id.
     * @return {@code true} if the given user was stored in this cache.
     */
    public boolean fillUser(UserDTO user) {
        UserDTO cached = getValue(user.getId());
        if (cached == null) {
            return false;
        } else {
            user.copyFrom(cached);
            return true;
        }
    }

    /**
     * Updates the cache with the given user.
     *
     * @param user User to be stored in the cache.
     */
    public void storeUser(UserDTO user) {
        setValue(user.getId(), user);
    }

    /**
     * Removes the given user from the cache if possible.
     *
     * @param user User to be removed from cache.
     */
    public void invalidate(UserDTO user) {
        deleteValue(user.getId());
    }
}
