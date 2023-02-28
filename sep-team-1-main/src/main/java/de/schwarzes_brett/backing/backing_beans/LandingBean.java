package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.backing.util.NotificationHelper;
import de.schwarzes_brett.business_logic.notification.NotificationContext;
import de.schwarzes_brett.business_logic.services.AdService;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.CategoryDTO;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.PaginationDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Backing Bean for the landing page. Bean for showing ads to anonymous users.
 *
 * @author michaelgruner
 */
@Named
@ViewScoped
public class LandingBean extends PaginationBean implements Serializable, NotificationDisplay, CategoryNavigation {

    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Connection to the notification system.
     */
    @Inject
    private NotificationHelper notificationHelper;
    /**
     * The ad service.
     */
    @Inject
    private AdService adService;

    /**
     * The categoryNavigationBean of the landingBean.
     */
    @Inject
    private CategoryNavigationBean categoryNavigationBean;

    /**
     * An instance of the dictionary.
     */
    @Inject
    private transient Dictionary dictionary;

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
     * Default constructor.
     */
    public LandingBean() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {
        // Generate message for successful registration.
        notificationHelper.generateFacesMessage(NotificationContext.NONE, null);
    }

    /**
     * Getter for ads which will be listed.
     *
     * @return List of {@code AdDTO}.
     */
    public List<AdDTO> getListElements() {
        return listElements;
    }

    /**
     * Setter for ads which will be listed.
     *
     * @param listElements List of {@code AdDTO}.
     */
    public void setListElements(List<AdDTO> listElements) {
        this.listElements = listElements;
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.finest("Initializing of the landingBean.");
        setPagination(new PaginationDTO());
        getPagination().setSortBy(dictionary.get("header_name_default_adPagination"));
        listElements = adService.fetchAds(getPagination());
        displayNotifications();
        logger.finest("Initializing of the landingBean finished.");
    }

    /**
     * Reloads current bean.
     *
     * @return The beans corresponding facelet.
     */
    @Override
    public String reload() {
        logger.finest("Reload of the landingBean started.");
        logger.finer("Fetching ads from the adService");
        listElements = adService.fetchAds(getPagination());
        logger.finest("Reload of the landingBean finished.");
        return "";
    }

    /**
     * Performs the action when a category gets selected.
     *
     * @param category The chosen category.
     * @return The facelet where search-results get shown.
     */
    public String onCategoryClick(CategoryDTO category) {
        logger.finest("Fetching ads for the category " + category.getName());
        getPagination().setCategory(category);
        categoryNavigationBean.setBreadCrumb(category);
        return reload();
    }

    /**
     * Returns the correct path of the image for the thumbnail.
     *
     * @param thumbnail The thumbnail you want the image for.
     * @return The correct path for the thumbnail.
     */
    public String generateThumbnailId(ImageDTO thumbnail) {
        if (thumbnail.getId() == null) {
            return "/image-default?id=thumbnail";
        } else {
            return "/image?id=" + thumbnail.getId();
        }
    }
}
