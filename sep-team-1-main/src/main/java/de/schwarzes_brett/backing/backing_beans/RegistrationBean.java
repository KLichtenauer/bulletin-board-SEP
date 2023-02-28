package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;

/**
 * Backing Bean for the registration of a user.
 *
 * @author michaelgruner
 */
@Named
@RequestScoped
public class RegistrationBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * The userDTO to transfer the inputs.
     */
    private final UserDTO user = new UserDTO();
    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;
    /**
     * An instance of the userService.
     */
    @Inject
    private UserService userService;
    /**
     * An instance of the userSession.
     */
    @Inject
    private UserSession userSession;

    /**
     * The external context.
     */
    @Inject
    private transient ExternalContext externalContext;

    /**
     * Default constructor.
     */
    public RegistrationBean() {
    }

    /**
     * Gets the user to be registered.
     *
     * @return An instance of {@code UserDTO} filled with information about the user to be registered.
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
        // Generate message for failed registration because of username race condition.
        notificationHelper.generateFacesMessage(NotificationContext.USERNAME, "registration_form:registration_emailInput");

        // Generate message for failed registration because of email race condition.
        notificationHelper.generateFacesMessage(NotificationContext.EMAIL, "registration_form:registration_emailInput");

        // Generate message for failed registration because email could not be sent.
        notificationHelper.generateFacesMessage(NotificationContext.NONE, null);
    }

    /**
     * Gets called when the user submits his registration.
     *
     * @return The corresponding facelet for the registration.
     */
    public String submit() {
        user.setLanguage(userSession.getLocale());
        String path = externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName() + ":"
                      + externalContext.getRequestServerPort() + externalContext.getRequestContextPath() + "/view/public/verifyEmail.xhtml";
        if (!userService.insertUser(user, path)) {
            displayNotifications();
            return "";
        }
        return "/view/public/landing";
    }
}
