package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.business_logic.services.AdService;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.CategoryDTO;
import de.schwarzes_brett.dto.ImageDTO;
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
 * Bean for the ad administration where an admin can search, alter and delete ads.
 *
 * @author Valentin Damjantschitsch.
 */
@Named
@ViewScoped
public class AdAdministrationBean extends PaginationBean implements NotificationDisplay, Serializable, CategoryNavigation {


    @Serial
    private static final long serialVersionUID = 1L;

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
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Used for flash scope.
     */
    @Inject
    private transient ExternalContext externalContext;

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
     * Default constructor.
     */
    public AdAdministrationBean() {
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
        logger.finest("Initializing of the adAdministrationBean.");
        setPagination(new PaginationDTO());
        getPagination().setSortBy(dictionary.get("header_name_default_adPagination"));
        listElements = adService.fetchAllAds(getPagination());
        logger.finest("Initializing of the adAdministrationBean finished.");
    }

    /**
     * Reloads current bean.
     *
     * @return The beans corresponding facelet.
     */
    @Override
    public String reload() {
        logger.finest("Reload of the adAdministrationBean started.");
        logger.finer("Fetching ads from the adService");
        listElements = adService.fetchAds(getPagination());
        logger.finest("Reload of the adAdministrationBean finished.");
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

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
     * Gets called when an admin wants to edit a user's ad.
     *
     * @param ad The ad to be edited.
     * @return The facelet for editing ads.
     */
    public String editAd(AdDTO ad) {
        externalContext.getFlash().put("ad", ad);
        return "/view/user/editAd";
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
