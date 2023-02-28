package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.exception.UnauthorisedAccessException;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.TokenDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.logging.Logger;

/**
 * Backing Bean for the verification of a user. Works by sending a link with given url to the user via email. When clicked, the token contained in the
 * url gets checked and if it matches the saved token the verification is valid.
 *
 * @author Daniel Lipp
 */
@Named
@RequestScoped
public class VerifyEmailBean implements NotificationDisplay {

    private final TokenDTO token;

    @Inject
    private UserService service;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    private boolean success;

    /**
     * Default constructor.
     */
    public VerifyEmailBean() {
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
        if (token.getUsername() == null || token.getToken() == null) {
            logger.severe("Someone tried to access the page without params.");
            throw new UnauthorisedAccessException("Diese Seite kann nur mit url parametern aufgerufen werden");
        }
        success = service.processToken(token);
        logger.info("User " + token.getUsername() + " could " + (success ? " be " : " not be ") + "verified.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

    }

    /**
     * Returns if the verification was successful.
     *
     * @return {@code true} if verification was successful.
     */
    public boolean isSuccess() {
        return success;
    }
}
