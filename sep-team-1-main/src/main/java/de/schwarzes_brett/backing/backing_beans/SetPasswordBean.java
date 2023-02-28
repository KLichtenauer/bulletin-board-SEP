package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.exception.UnauthorisedAccessException;
import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.CredentialsDTO;
import de.schwarzes_brett.dto.TokenDTO;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Backing Bean for setting a new password of a user.
 *
 * @author Daniel Lipp
 */
@Named
@ViewScoped
public class SetPasswordBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Token if the user forgot his password.
     */
    private final TokenDTO token;
    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;
    /**
     * Service that handels the logic of this request.
     */
    @Inject
    private UserService service;
    /**
     * The user who wants to reset his password.
     */
    @Inject
    @ManagedProperty("#{flash.keep.user}")
    private UserDTO user;

    /**
     * The second input for the password.
     */
    private String newRepeatedPassword;

    /**
     * Flag if the input was valid and therefor the password can be changed.
     * Is false if flash was not set and no valid url params were given.
     */
    private boolean passwordChangePossible;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public SetPasswordBean() {
        token = new TokenDTO();
    }

    /**
     * Getter for the token of changed password.
     *
     * @return An instance of {@code TokenDTO}.
     */
    public TokenDTO getToken() {
        return token;
    }

    /**
     * Checks if token matches and password can be changed.
     */
    public void processToken() {
        if (user == null) {
            if (token.getToken() == null || token.getUsername() == null) {
                logger.severe("Someone tried to enter the Set Password site without permission.");
                throw new UnauthorisedAccessException("Neither flash nor token were given.");
            } else {
                passwordChangePossible = service.processToken(token);
                if (passwordChangePossible) {
                    user = new UserDTO();
                    user.setCredentials(new CredentialsDTO());
                    user.getCredentials().setUsername(token.getUsername());
                }
                logger.info("The token the User " + token.getUsername() + " used" + (passwordChangePossible ? " was " : " was not ") + "valid.");
            }
        } else {
            passwordChangePossible = true;
        }
    }

    /**
     * Getter for the changed password.
     *
     * @return Returns the new password.
     */
    public String getNewRepeatedPassword() {
        return newRepeatedPassword;
    }

    /**
     * Setter for the new repeated password.
     *
     * @param newRepeatedPassword The new repeated password to be set.
     */
    public void setNewRepeatedPassword(String newRepeatedPassword) {
        this.newRepeatedPassword = newRepeatedPassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
        notificationHelper.generateFacesMessage(NotificationContext.NONE, null);
    }

    /**
     * Gets called when the user submits his password-changes.
     *
     * @return The corresponding facelet for his user-profile.
     */
    public String submit() {
        boolean ret = service.changePwd(user);
        displayNotifications();
        return ret ? "/view/public/landing" : "";
    }

    /**
     * Returns if the password can be changed.
     *
     * @return {@code true} if either a token was in the url or the flash was set.
     */
    public boolean isPasswordChangePossible() {
        return passwordChangePossible;
    }


    /**
     * Gets the user who wants to change his password.
     *
     * @return An instance of {@code UserDTO} filled with information about the user who wants to change his password.
     */
    public UserDTO getUser() {
        return user;
    }
}
