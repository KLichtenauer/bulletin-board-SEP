package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.business_logic.services.ApplicationSettingsService;
import de.schwarzes_brett.dto.ApplicationSettingsDTO;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Backing Bean for the footer. Forwards to settings, imprint and privacy specification.
 *
 * @author Valentin Damjantschitsch.
 */
@Named
@RequestScoped
public class FooterBean {

    @Inject
    private ApplicationSettingsService applicationSettingsService;

    private ApplicationSettingsDTO settings;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public FooterBean() {
    }

    /**
     * Getter for the settings of the footer.
     *
     * @return An instance of {@code ApplicationSettingsDTO} containing settings information.
     */
    public ApplicationSettingsDTO getSettings() {
        return settings;
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.log(Level.FINEST, "Initializing FooterBean.java");
        settings = new ApplicationSettingsDTO();
        applicationSettingsService.fetchSettingsMinimal(settings);
    }

}
