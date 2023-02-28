package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.ResetPasswordDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.ExternalContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Backing Bean for resetting the password of a user.
 *
 * @author Daniel Lipp
 */
@Named
@RequestScoped
public class ResetPasswordBean implements NotificationDisplay {

    private final ResetPasswordDTO resetPassword;
    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;
    @Inject
    private UserService userService;
    /**
     * The current external context.
     */
    @Inject
    private transient ExternalContext externalContext;

    /**
     * Default constructor.
     */
    public ResetPasswordBean() {
        resetPassword = new ResetPasswordDTO();
    }

    /**
     * Getter for the password which replaces the old one.
     *
     * @return An instance of {@code ResetPasswordDTO}.
     */
    public ResetPasswordDTO getResetPasswordDTO() {
        return resetPassword;
    }

    /**
     * Displays any information the user should get informed of.
     */
    @Override
    public void displayNotifications() {
        notificationHelper.generateFacesMessage(NotificationContext.NONE, null);
    }

    /**
     * Gets called when the user submits his password-reset.
     *
     * @return Stays on the same page.
     */
    public String submit() {
        resetPassword.setLanguage(externalContext.getRequestLocale());
        boolean ret = userService.sendMailWithResetPasswordLink(resetPassword, getResetPwdUrl());
        displayNotifications();
        return ret ? "/view/public/landing" : "";
    }

    private String getResetPwdUrl() {
        return externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName() + ":" + externalContext.getRequestServerPort()
               + externalContext.getRequestContextPath() + "/view/public/setPassword.xhtml";
    }
}
