package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.business_logic.services.AdService;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.PaginationDTO;
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
 * User can see his created listItems and manage these by editing these for example.
 *
 * @author Jonas Elsper
 */
@Named
@ViewScoped
public class AdManagementBean extends PaginationBean implements NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The session of the logged-in user.
     */
    @Inject
    private UserSession userSession;

    /**
     * ExternalContext used for flash scope.
     */
    @Inject
    private transient ExternalContext externalContext;


    /**
     * The ad service.
     */
    @Inject
    private AdService adService;

    /**
     * The displayed list of listItems.
     */
    private List<AdDTO> listElements;

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
    public AdManagementBean() {
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.finest("Initializing of the adManagement bean.");
        setPagination(new PaginationDTO());
        getPagination().setSortBy(dictionary.get("header_name_default_adPagination"));
        reload();
        logger.finest("Initializing of the adManagement bean finished.");
    }

    /**
     * Getter for listItems which will be listed.
     *
     * @return List of {@code AdDTO}.
     */
    public List<AdDTO> getListElements() {
        return listElements;
    }

    /**
     * Setter for listItems which will be listed.
     *
     * @param listElements List of {@code AdDTO}.
     */
    public void setListElements(List<AdDTO> listElements) {
        this.listElements = listElements;
    }

    /**
     * Gets called when a user wants to create an ad.
     *
     * @return The corresponding facelet for creating an ad.
     */
    public String newAd() {
        logger.finest("Forwarded to the editAd page to create a new ad.");
        externalContext.getFlash().put("ad", null);
        return "/view/user/editAd";
    }

    /**
     * Reloads current bean.
     *
     * @return The beans corresponding facelet.
     */
    @Override
    public String reload() {
        logger.finest("Starting to reload the ads of the pagination.");
        listElements = adService.fetchAdsFromUser(getPagination(), userSession.getUser());
        logger.finest("Reloaded the ads of the pagination successfully.");
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

    }

    /**
     * Gets called when a user wants to edit an ad.
     *
     * @param ad The ad that should be edited.
     * @return The facelet for editing listItems.
     */
    public String editAd(AdDTO ad) {
        externalContext.getFlash().put("ad", ad);
        return "/view/user/editAd";
    }
}
