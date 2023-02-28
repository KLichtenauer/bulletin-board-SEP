package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.exception.UnauthorisedAccessException;
import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.Role;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Backing bean for the {@code profile} facelet.
 *
 * @author Tim-Florian Feulner
 */
@Named
@ViewScoped
public class ProfileBean implements ImageUploadHolder, NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The list of all selectable user roles.
     */
    private static final List<Role> ROLE_LIST = Stream.of(Role.values()).filter(Predicate.not(Predicate.isEqual(Role.ANONYMOUS))).toList();

    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;

    /**
     * The user service.
     */
    @Inject
    private UserService userService;

    /**
     * The current user.
     */
    @Inject
    @ManagedProperty("#{flash.keep.user}")
    private UserDTO user;

    /**
     * The original user data.
     */
    private UserDTO oldUser;

    /**
     * The instance of the image upload bean.
     */
    @Inject
    private ImageUploadBean imageUploadBean;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * The current external context.
     */
    @Inject
    private transient ExternalContext externalContext;

    /**
     * The instance of the user session.
     */
    @Inject
    private UserSession userSession;

    /**
     * Default constructor.
     */
    public ProfileBean() {}

    /**
     * Initializes this backing bean.
     */
    @PostConstruct
    public void init() {
        boolean success = userService.fetchUserByUsername(user);
        if (!success) {
            String message = "Could not access the profile of non-existent user " + user.getCredentials().getUsername() + ".";
            logger.severe(message);
            throw new UnauthorisedAccessException(message);
        }

        oldUser = new UserDTO(user);

        if (user.getAvatar().getId() != null) {
            imageUploadBean.addImageToDisplay(user.getAvatar());
        }
    }

    /**
     * Gets the user data for this profile that is to be edited.
     *
     * @return The current user data.
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * Gets the old user data.
     *
     * @return The old user data.
     */
    public UserDTO getOldUser() {
        return oldUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
        // Generate message for save feedback.
        notificationHelper.generateFacesMessage(NotificationContext.NONE, null);
        notificationHelper.generateFacesMessage(NotificationContext.USERNAME, null);
        notificationHelper.generateFacesMessage(NotificationContext.EMAIL, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageUploadBean getImageUploadBean() {
        return imageUploadBean;
    }

    /**
     * Uploads an avatar image.
     *
     * @param uploadStream The image data to upload.
     */
    @Override
    public void uploadImage(InputStream uploadStream) {
        logger.info("Uploading avatar image.");
        ImageDTO avatar = new ImageDTO();
        avatar.setStoreStream(uploadStream);
        user.setAvatar(avatar);
        userService.insertAvatar(user);
        imageUploadBean.addImageToDisplay(avatar);
    }

    /**
     * Deletes the avatar image.
     *
     * @param image The image to delete.
     */
    @Override
    public void deleteImage(ImageDTO image) {
        logger.info("Deleting avatar image.");
        userService.deleteAvatar(user);
        imageUploadBean.removeImageFromDisplay(image);
    }

    /**
     * Gets called when the user wants to delete his profile (i.e. his user account).
     *
     * @return The navigation target.
     */
    public String deleteProfile() {
        Integer loggedInUserId = userSession.getUser().getId();
        boolean success = userService.deleteUser(user);
        displayNotifications();

        if (success) {
            // Perform user logout if deleted user that is currently logged in.
            if (user.getId().equals(loggedInUserId)) {
                return userSession.logout();
            } else {
                return "/view/admin/userAdministration";
            }
        } else {
            return null;
        }
    }

    /**
     * Saves changes of the profile.
     *
     * @return The navigation target.
     */
    public String save() {
        logger.info("Saving user profile of user " + user.getCredentials().getUsername() + ".");
        UserService.UpdateUserResult result = userService.updateUser(user, userSession.getUser(), oldUser, getVerificationUrl());
        displayNotifications();

        switch (result) {
        case SUCCESS -> {
            oldUser = new UserDTO(user);
            return null;
        }
        case ERROR -> {
            return null;
        }
        case LOGOUT -> {
            return userSession.logout();
        }
        default -> {
            throw new Error("Case of enum UserService.UpdateUserResult not covered.");
        }
        }
    }

    private String getVerificationUrl() {
        return externalContext.getRequestScheme() + "://" + externalContext.getRequestServerName() + ":" + externalContext.getRequestServerPort()
               + externalContext.getRequestContextPath() + "/view/public/verifyEmail.xhtml";
    }

    /**
     * Navigates to the {@code setPassword} facelet for the respective user.
     *
     * @return The corresponding facelet for changing password.
     */
    public String changePassword() {
        externalContext.getFlash().put("user", user);
        return "/view/public/setPassword?faces-redirect=true";
    }

    /**
     * Gets the list of all selectable roles.
     *
     * @return The role list.
     */
    public List<Role> getRoleList() {
        return ROLE_LIST;
    }

    /**
     * Checks that the profile is not the profile of the default admin.
     *
     * @return {@code true} iff the profile is not the one of the default admin.
     */
    public boolean isNotDefaultAdminProfile() {
        return user.getId() != UserDTO.DEFAULT_ADMIN_ID;
    }

}
