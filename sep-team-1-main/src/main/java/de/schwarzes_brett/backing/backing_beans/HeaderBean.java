package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.business_logic.services.ApplicationSettingsService;
import de.schwarzes_brett.dto.ApplicationSettingsDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Backing Bean for the header. Offers multiple functionalities like forwarding to landing page, login, logout, go to profile, show help etc.
 *
 * @author Valentin Damjantschitsch.
 */
@Named
@ViewScoped
public class HeaderBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Current user session.
     */
    @Inject
    private UserSession userSession;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Responsible for the ability to fetch application settings.
     */
    @Inject
    private ApplicationSettingsService applicationSettingsService;

    /**
     * Current settings of the application.
     */
    private ApplicationSettingsDTO settings;

    @Inject
    private transient ExternalContext externalContext;

    /**
     * Default constructor.
     */
    public HeaderBean() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

    }

    /**
     * Gets called when the user wants to log out.
     *
     * @return The corresponding facelet for the landing page.
     */
    public String logout() {
        return userSession.logout();
    }

    /**
     * Getter for the settings.
     *
     * @return The settings of the application.
     */
    public ApplicationSettingsDTO getSettings() {
        return settings;
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.log(Level.FINE, "Initializing HeaderBean.java");
        settings = new ApplicationSettingsDTO();
        applicationSettingsService.fetchSettingsMinimal(settings);
    }

    /**
     * Redirects to the profile page.
     *
     * @return The endpoint of the redirect.
     */
    public String goToProfile() {
        externalContext.getFlash().put("user", userSession.getUser());
        return "/view/user/profile?faces-redirect=true";
    }

    /**
     * Returns the correct link for the logo of the application.
     *
     * @return The generated link.
     */
    public String generateLogoLink() {
        if (settings.getLogo().getId() == null) {
            return "/image-default?id=logo";
        } else {
            return "/image?id=" + settings.getLogo().getId();
        }
    }
}
