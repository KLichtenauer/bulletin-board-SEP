package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.dto.ApplicationSettingsDTO;

/**
 * Controls the database access for the application settings. The access happens through prepared statements and filling DTOs which get iterated
 * through the layers.
 */
public interface ApplicationSettingsDAO extends CacheableDAO {

    /**
     * Updates applications settings at the database.
     *
     * @param settings The new settings.
     */
    void updateApplicationSettings(ApplicationSettingsDTO settings);

    /**
     * Fetches current application settings.
     * This method does not fetch the imprint and the privacy text.
     *
     * @param settings The {@code ApplicationSettingsDTO} which is getting filled with current settings.
     */
    void fetchSettingsMinimal(ApplicationSettingsDTO settings);

    /**
     * Fetches the current imprint from the application settings.
     *
     * @param settings Instance of {@code ApplicationSettingsDTO} which has to be filled.
     */
    void fetchImprint(ApplicationSettingsDTO settings);

    /**
     * Fetches the current privacy text from the application settings.
     *
     * @param settings Instance of {@code ApplicationSettingsDTO} which has to be filled.
     */
    void fetchPrivacy(ApplicationSettingsDTO settings);
}
