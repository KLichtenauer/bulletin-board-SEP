package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.PaginationDTO;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Backing Bean for the global user administration for the administrator. User profiles can be searched, edited and created.
 *
 * @author Kilian Lichtenauer
 */
@Named
@ViewScoped
public class UserAdministrationBean extends PaginationBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Used for flash scope.
     */
    @Inject
    private transient ExternalContext externalContext;

    /**
     * The user service.
     */
    @Inject
    private UserService userService;

    /**
     * The displayed list of users.
     */
    private List<UserDTO> listElements;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * An instance of the dictionary.
     */
    @Inject
    private transient Dictionary dictionary;

    /**
     * Default constructor.
     */
    public UserAdministrationBean() {
    }


    /**
     * Getter for users which will be listed.
     *
     * @return List of {@code UserDTO}.
     */
    public List<UserDTO> getListElements() {
        return listElements;
    }

    /**
     * Setter for users which will be listed.
     *
     * @param listElements List of {@code UserDTO}.
     */
    public void setListElements(List<UserDTO> listElements) {
        this.listElements = listElements;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.finest("Initializing of the userAdministrationBean.");
        setPagination(new PaginationDTO());
        getPagination().setSortBy(dictionary.get("header_name_default_userPagination"));
        listElements = userService.fetchUsers(getPagination());
    }

    /**
     * Reloads current bean.
     *
     * @return The beans corresponding facelet.
     */
    @Override
    public String reload() {
        logger.fine("Reloading adAdministration and fetching users.");
        listElements = userService.fetchUsers(getPagination());
        return "";
    }


    /**
     * Gets called when the admin wants to create a new user.
     *
     * @return The corresponding facelet for creating a user.
     */
    public String newUser() {
        return "/view/public/registration";
    }

    /**
     * Gets called when the admin wants to edit a user profile.
     *
     * @param user The username of the profile the admin wants to change.
     * @return The corresponding facelet for editing a user profile.
     */
    public String goToUserProfile(UserDTO user) {
        externalContext.getFlash().put("user", user);
        return "/view/user/profile";
    }

}
