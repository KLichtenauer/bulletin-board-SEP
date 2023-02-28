package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.business_logic.services.ApplicationSettingsService;
import de.schwarzes_brett.dto.ApplicationSettingsDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Logger;

/**
 * Backing Bean for the imprint page.
 *
 * @author Valentin Damjantschitsch
 */
@Named
@RequestScoped
public class ImprintBean {

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
    public ImprintBean() {
    }

    /**
     * Getter of the settings values.
     *
     * @return An instance of {@code ApplicationSettingsDTO}.
     */
    public ApplicationSettingsDTO getSettings() {
        logger.finest("Get Settings for ImprintBean.java");
        ApplicationSettingsDTO settings = new ApplicationSettingsDTO();
        applicationSettingsService.fetchImprint(settings);
        return settings;
    }
}
