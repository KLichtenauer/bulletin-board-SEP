package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.business_logic.services.ApplicationSettingsService;
import de.schwarzes_brett.dto.ApplicationSettingsDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Logger;

/**
 * Backing Bean for the privacy page.
 *
 * @author Valentin Damjantschitsch.
 */
@Named
@RequestScoped
public class PrivacyBean {

    @Inject
    private ApplicationSettingsService applicationSettingsService;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public PrivacyBean() {
    }

    /**
     * Getter for the beans settings.
     *
     * @return An instance of {@code CategoryDTO} containing the settings values.
     */
    public ApplicationSettingsDTO getSettings() {
        logger.finest("Get Settings for PrivacyBean.java");
        ApplicationSettingsDTO settings = new ApplicationSettingsDTO();
        applicationSettingsService.fetchPrivacy(settings);
        return settings;
    }
}
