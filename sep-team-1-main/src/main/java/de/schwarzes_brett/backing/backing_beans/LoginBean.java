package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.CredentialsDTO;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Backing bean for the {@code login} facelet.
 *
 * @author Tim-Florian Feulner
 */
@Named
@RequestScoped
public class LoginBean implements NotificationDisplay {

    private final CredentialsDTO credentials = new CredentialsDTO();

    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;

    @Inject
    private UserSession userSession;

    @Inject
    private UserService userService;

    @Inject
    private ExternalContext externalContext;

    /**
     * Timezone of the client for converting the time to UTC.
     */
    private String timezone;

    /**
     * Default constructor.
     */
    public LoginBean() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
        // Generate message for failed login.
        notificationHelper.generateFacesMessage(NotificationContext.NONE, null);
    }

    /**
     * Performs a login attempt.
     *
     * @return The navigation target.
     */
    public String login() {
        UserDTO user = userService.login(credentials, userSession.getLocale());

        if (user == null) {
            // Login unsuccessful
            displayNotifications();
            return null;
        } else {
            // Login successful

            // Prevent session fixation.
            ((HttpServletRequest) externalContext.getRequest()).changeSessionId();

            int minuteOffset;
            try {
                minuteOffset = Integer.parseInt(timezone);
            } catch (NumberFormatException e) {
                minuteOffset = 0;
                // Could not parse timezone of client. Using UTC instead
            }

            userSession.login(user, minuteOffset);

            displayNotifications();
            return "/view/user/welcome";
        }
    }

    /**
     * Gets the {@code CredentialsDTO} instance.
     *
     * @return A {@code CredentialsDTO} instance filled with credential information.
     */
    public CredentialsDTO getCredentials() {
        return credentials;
    }

    /**
     * Gets the timezone of the client.
     *
     * @return The timezone of the client.
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Sets the timezone of the client.
     *
     * @param timezone The timezone of the client.
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

}
