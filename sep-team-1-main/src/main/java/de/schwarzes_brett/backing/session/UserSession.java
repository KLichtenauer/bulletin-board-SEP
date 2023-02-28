package de.schwarzes_brett.backing.session;

import de.schwarzes_brett.backing.exception.UnauthorisedAccessException;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.Role;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.logging.LoggerProducer;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Manages a user session and provides the users credentials and general data. Because of the saved role the class is important for the ability to
 * check if a user is eligible for certain actions.
 *
 * @author Daniel Lipp
 */
@Named
@SessionScoped
public class UserSession implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Logger for logging which locale was stored when initiating.
     */
    private final transient Logger logger = LoggerProducer.get(UserSession.class);

    /**
     * The user id of the current session. If not logged in, the user id is null.
     */
    private Integer userId = null;

    /**
     * Locale of the user of the current session. If not Logged in the Locale is {@code Locale.GERMAN}
     */
    private Locale locale;

    /**
     * Offset of the Timezone of the client in minutes.
     */
    private long timeOffset;

    /**
     * The current external context.
     */
    @Inject
    private transient ExternalContext externalContext;

    /**
     * An instance of the userService.
     */
    @Inject
    private UserService userService;

    /**
     * Default constructor.
     */
    public UserSession() {}

    /**
     * Initialises the Session with the locale of the current request or {@code Locale.GERMAN}
     * if there is no {@code ExternalContext}.
     *
     * @author Daniel Lipp
     */
    @PostConstruct
    public synchronized void init() {
        if (externalContext != null) {
            logger.finer("locale was set to locale of client");
            locale = externalContext.getRequestLocale();
        } else {
            locale = Locale.GERMAN;
            logger.finer("Could not get locale of the session, so default value was set.");
        }
    }

    /**
     * Getter for the user for the id. If the user is anonymous, an empty user is returned but not {@code null}.
     *
     * @return An instance of {@code UserDTO} containing information about the current user.
     */
    public synchronized UserDTO getUser() {
        if (userId != null) {
            UserDTO result = new UserDTO();
            result.setId(userId);
            if (userService.fetchUserById(result)) {
                return result;
            } else {
                userId = null;
                throw new UnauthorisedAccessException("The user was deleted.");
            }
        } else {
            UserDTO anonymousUser = new UserDTO();
            anonymousUser.setRole(Role.ANONYMOUS);
            return anonymousUser;
        }
    }

    /**
     * Returns the locale of this session. By default, it should be the locale of the request.
     *
     * @return The Locale of the current session.
     * @author Daniel Lipp
     */
    public synchronized Locale getLocale() {
        return locale;
    }

    /**
     * Getter for the timezone offset.
     *
     * @return Offset in minutes.
     */
    public synchronized long getTimeOffset() {
        return timeOffset;
    }

    /**
     * Checks the user state.
     *
     * @return True, if the user is anonymous.
     */
    public synchronized boolean isAnonymous() {
        return userId == null;
    }

    /**
     * Checks the user state.
     *
     * @return True, if the user is logged in.
     */
    public synchronized boolean isLoggedIn() {
        return userId != null;
    }

    /**
     * Checks the user state.
     *
     * @return True, if the logged in user is a admin.
     */
    public synchronized boolean isAdmin() {
        if (userId == null) {
            return false;
        } else {
            UserDTO currentUser = getUser();
            return currentUser.getRole() == Role.ADMIN;
        }
    }

    /**
     * Performs a login of a user.
     *
     * @param loginUser  The user to login in with.
     * @param timeOffset The time offset in minutes.
     */
    public synchronized void login(UserDTO loginUser, long timeOffset) {
        userId = loginUser.getId();
        this.timeOffset = timeOffset;
    }

    /**
     * Performs a logout of the currently logged in user.
     *
     * @return The navigation target.
     */
    public String logout() {
        externalContext.invalidateSession();
        externalContext.getFlash().setKeepMessages(true);
        return "/view/public/landing?faces-redirect=true";
    }

    /**
     * String representation in the form "user: {username}" or "empty session" if the user is {@code null}.
     *
     * @return string representation of this session.
     * @author Daniel Lipp
     */
    @Override
    public synchronized String toString() {
        if (userId == null) {
            return "empty session";
        } else {
            return "user: " + getUser().getCredentials().getUsername();
        }
    }

}
