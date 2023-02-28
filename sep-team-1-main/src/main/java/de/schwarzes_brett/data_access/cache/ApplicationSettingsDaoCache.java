package de.schwarzes_brett.data_access.cache;

import de.schwarzes_brett.dto.ApplicationSettingsDTO;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A cache for the application Settings. Can be used to reduced loading times.
 *
 * @author Daniel Lipp
 */
public final class ApplicationSettingsDaoCache extends DAOCache<ApplicationSettingsDTO> {
    private static final ApplicationSettingsDaoCache INSTANCE = new ApplicationSettingsDaoCache();

    private static final Integer ID = 1;

    private ApplicationSettingsDaoCache() {
        super();
    }

    /**
     * Getter for the cache instance.
     *
     * @return Only instance of this cache.
     */
    @SuppressFBWarnings("MS_EXPOSE_REP")
    public static ApplicationSettingsDaoCache getInstance() {
        return INSTANCE;
    }

    /**
     * Getter for the stored value of the application settings.
     *
     * @return The stored application settings or {@code null} if cache is empty;
     */
    public ApplicationSettingsDTO getValue() {
        return super.getValue(ID);
    }

    /**
     * Setter for the stored value of the application Settings. Is valid until the cache time for this class has passed.
     *
     * @param value New value for the application settings.
     */
    public void setValue(ApplicationSettingsDTO value) {
        super.setValue(ID, value);
    }

    /**
     * Invalidates the cached settings.
     */
    public void invalidate() {
        super.deleteValue(ID);
    }
}
