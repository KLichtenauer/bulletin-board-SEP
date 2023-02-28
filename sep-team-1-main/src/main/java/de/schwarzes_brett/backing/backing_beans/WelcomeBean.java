package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.business_logic.services.AdService;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.PaginationDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Backing Bean for the Welcome-Page of a logged-in user where ads depended on the set context get shown. The context can be subscribed ads, own ads
 * or commented ads.
 *
 * @author Jonas Elsper
 */
@Named
@ViewScoped
public class WelcomeBean extends PaginationBean implements Serializable, NotificationDisplay {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The current ad selection.
     */
    private AdSelection selection;

    /**
     * The ad service.
     */
    @Inject
    private AdService adService;

    /**
     * The displayed list of ads.
     */
    private List<AdDTO> listElements;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * The user session of the currently logged-in user.
     */
    @Inject
    private UserSession userSession;

    /**
     * An instance of the dictionary.
     */
    @Inject
    private transient Dictionary dictionary;

    /**
     * Default constructor.
     */
    public WelcomeBean() {
    }

    /**
     * Gets the ads which are shown on the welcome page.
     *
     * @return The list of ads shown on the welcome page.
     */
    public List<AdDTO> getListElements() {
        return listElements;
    }

    /**
     * Sets the ads which should get shown on the welcome page.
     *
     * @param listElements The ads which get shown on the welcome page
     */
    public void setListElements(List<AdDTO> listElements) {
        this.listElements = listElements;
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.finest("Initializing of the welcomeBean.");
        setPagination(new PaginationDTO());
        getPagination().setSortBy(dictionary.get("header_name_default_adPagination"));
        selection = AdSelection.OWN;
        reload();
        logger.finest("Initializing of the welcomeBean finished.");
    }

    /**
     * Reloads current bean.
     *
     * @return The beans corresponding facelet.
     */
    @Override
    public String reload() {
        logger.finest("Reload of the welcomeBean started.");
        logger.finer("Fetching ads from the adService");
        switch (selection) {
        case OWN:
            listElements = adService.fetchAdsFromUser(getPagination(), userSession.getUser());
            break;
        case COMMENTED:
            listElements = adService.fetchCommentedAds(getPagination(), userSession.getUser());
            break;
        case SUBSCRIBED:
            listElements = adService.fetchFollowedAds(getPagination(), userSession.getUser());
            break;
        default:
            logger.log(Level.WARNING, "Selection is null. Invalid state for selection.");
        }
        logger.finest("Reload of the welcomeBean finished.");
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

    }

    /**
     * Changes the displayed content on the welcome page.
     *
     * @param selection The new displayed content the user chose to see.
     * @return The corresponding facelet of the welcome-page.
     */
    public String changeSelection(AdSelection selection) {
        logger.finest("Fetching ads for the new selection.");
        this.selection = selection;
        return reload();
    }

    /**
     * Returns the correct path of the image for the thumbnail.
     *
     * @param thumbnail The thumbnail you want the image for.
     * @return The correct path for the thumbnail.
     */
    public String getGenerateThumbnailId(ImageDTO thumbnail) {
        if (thumbnail.getId() == null) {
            return "/image-default?id=thumbnail";
        } else {
            return "/image?id=" + thumbnail.getId();
        }
    }

    /**
     * Represents the displayed content on the welcome page.
     */
    public enum AdSelection {
        /**
         * Commented ads get shown.
         */
        COMMENTED,
        /**
         * Own ads get shown.
         */
        OWN,
        /**
         * Subscribed ads get shown.
         */
        SUBSCRIBED
    }
}
