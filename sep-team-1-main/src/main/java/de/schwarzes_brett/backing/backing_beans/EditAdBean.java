package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.business_logic.services.AdService;
import de.schwarzes_brett.business_logic.services.UserService;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.CategoryDTO;
import de.schwarzes_brett.dto.Currency;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.dto.Limits;
import de.schwarzes_brett.dto.PriceDTO;
import de.schwarzes_brett.dto.UserDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.model.SelectItem;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Backing Bean for creating and editing an ad by a user. An ad identified by a given id can be edited or deleted.
 *
 * @author Daniel Lipp
 */
@Named
@ViewScoped
public class EditAdBean implements ImageUploadHolder, NotificationDisplay, Serializable, CategoryNavigation {

    /**
     * Serial Version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * Prefix for the thumbnail selection.
     */
    private static final String THUMBNAIL_PREFIX = "Bild" + " ";
    /**
     * Standard Timezone.
     */
    private static final ZoneId UTC = ZoneId.of("UTC");
    /**
     * The ad service.
     */
    @Inject
    private AdService adService;
    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;
    /**
     * Session of the user.
     */
    @Inject
    private transient UserSession session;
    /**
     * The instance of the image upload bean.
     */
    @Inject
    private ImageUploadBean imageUploadBean;
    /**
     * The current ad.
     */
    private AdDTO ad;
    /**
     * Used for flash scope.
     */
    @Inject
    private transient ExternalContext externalContext;
    /**
     * EditAdBean for a new ad.
     */
    private boolean newAd = true;
    /**
     * Selection Options for the thumbnail selection.
     */
    private SelectItem[] thumbnailSelection;
    /**
     * List of all currency.
     */
    private List<Currency> currencyList;
    /**
     * Thumbnail of the ad.
     */
    private String thumbnail;
    /**
     * Price of the ad for user input.
     */
    private BigDecimal price;
    /**
     * Service for fetching the complete user, if an admin wants to change the creator of an ad.
     */
    @Inject
    private UserService userService;
    /**
     * End date of the ad.
     */
    private LocalDateTime end;
    /**
     * Release date of the ad.
     */
    private LocalDateTime release;

    /**
     * Default constructor.
     */
    public EditAdBean() {
    }

    /**
     * Getter for the ad which gets edited.
     *
     * @return An instance of {@code AdDTO}.
     */
    public AdDTO getAd() {
        return ad;
    }

    /**
     * Setter for the new values for the ad which gets edited.
     *
     * @param ad An instance of {@code AdDTO} containing the new values.
     */
    public void setAd(AdDTO ad) {
        this.ad = ad;
        newAd = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

    }

    /**
     * Gets called when the saved button gets pressed. Saves the new ad or updates the old one.
     *
     * @return The page of the new/edited ad.
     */
    @SuppressWarnings({"checkstyle:MagicNumber"})
    public String save() {
        int index;
        int offset = THUMBNAIL_PREFIX.length();
        if (thumbnail == null || thumbnail.length() < offset) {
            index = -1;
        } else {
            index = Integer.parseInt(thumbnail.substring(offset)) - 1;
        }
        ad.getPrice().setValue(price == null ? new BigDecimal(0) : price);
        ad.getPrice().setHasPrice(price != null);
        if (!session.getUser().getCredentials().getUsername().equals(ad.getCreator().getCredentials().getUsername())) {
            // admin changed the creator of the ad.
            userService.fetchUserByUsername(ad.getCreator());
        }

        ad.setRelease(ZonedDateTime.of(release, UTC));
        ad.setEnd(end == null ? null : ZonedDateTime.of(end, UTC));
        if (newAd) {
            ad.setImages(imageUploadBean.getImages());
            adService.insertAd(ad, index, session.getTimeOffset());
        } else {
            adService.updateAd(ad, index, session.getTimeOffset());
        }
        return "/view/public/ad?faces-redirect=true&id=" + ad.getId();
    }


    /**
     * Gets called when the owner wants to delete the ad.
     *
     * @return The corresponding facelet for deleting an ad.
     */
    public String delete() {
        adService.deleteAd(ad);
        return "/view/user/welcome";
    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        if (ad == null) {
            ad = (AdDTO) externalContext.getFlash().get("ad");
        }
        currencyList = Arrays.asList(Currency.values());
        Collections.sort(currencyList);
        if (ad != null) {
            newAd = false;
            processAdId();
        } else {
            newAd = true;
            thumbnailSelection = new SelectItem[0];
            ad = new AdDTO();
            ad.setPrice(new PriceDTO());
            ad.setCreator(session.getUser());
            ad.setImages(new ArrayList<>());
            ad.setCategory(new CategoryDTO());
            ad.getCategory().setId(0);
            ad.setPublicData(copyContactInfo(ad.getCreator()));
            ad.getPrice().setCurrency(currencyList.get(0));
        }
        imageUploadBean.setImages(ad.getImages());
        updateThumbnailSelection();
    }

    private void updateThumbnailSelection() {
        thumbnailSelection = new SelectItem[imageUploadBean.getImages().size()];
        for (int i = 0; i < thumbnailSelection.length; ++i) {
            thumbnailSelection[i] = new SelectItem(THUMBNAIL_PREFIX + (i + 1));
            if (!newAd && ad.getImages().get(i).getId().equals(ad.getThumbnail().getId())) {
                thumbnail = thumbnailSelection[i].getValue().toString();
            }
        }
    }

    private UserDTO copyContactInfo(UserDTO original) {
        UserDTO ret = new UserDTO();
        ret.setFirstName(original.getFirstName());
        ret.setLastName(original.getLastName());
        ret.setPhone(original.getPhone());
        ret.setEmail(original.getEmail());
        ret.setCountry(original.getCountry());
        ret.setCity(original.getCity());
        ret.setPostalCode(original.getPostalCode());
        ret.setStreet(original.getStreet());
        ret.setStreetNumber(original.getStreetNumber());
        ret.setAddressAddition(original.getAddressAddition());
        return ret;
    }

    /**
     * Gets an ad to a given id.
     */
    public void processAdId() {
        adService.fetchAd(ad, session.getUser(), session.getTimeOffset());
        price = ad.getPrice().isHasPrice() ? ad.getPrice().getValue() : null;

        if (ad.getCategory().getId().equals(0)) {
            ad.getCategory().setName("");
        }

        // setting values for primefaces datepicker
        release = ad.getRelease().toLocalDateTime();
        if (ad.getEnd() != null) {
            end = ad.getEnd().toLocalDateTime();
        }
    }

    /**
     * Returns a list for the radio button selection.
     *
     * @return The list for the selection.
     */
    public SelectItem[] getRadioSelection() {
        return thumbnailSelection;
    }

    /**
     * Returns whether the EditAdBean is for a new ad.
     *
     * @return Is for a new ad.
     */
    public boolean isNewAd() {
        return newAd;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageUploadBean getImageUploadBean() {
        return imageUploadBean;
    }

    /**
     * Uploads image for ads.
     *
     * @param uploadStream The stream storing the image to be uploaded.
     */
    @Override
    public void uploadImage(InputStream uploadStream) {
        if (newAd) {
            logger.info("Uploading image for new ad.");
            imageUploadBean.uploadRaw(uploadStream);
        } else {
            logger.info("Uploading image for existing ad.");
            ImageDTO image = new ImageDTO();
            image.setStoreStream(uploadStream);
            adService.insertImage(ad, image);
            imageUploadBean.addImageToDisplay(image);
        }
        updateThumbnailSelection();
    }

    /**
     * Deletes uploaded image of ads by assigned image id.
     *
     * @param image The image to be deleted.
     */
    @Override
    public void deleteImage(ImageDTO image) {
        if (newAd) {
            logger.info("Deleting image from new ad.");
            imageUploadBean.deleteRaw(image);
        } else {
            logger.info("Deleting image from existing ad.");
            adService.deleteImage(ad, image);
            imageUploadBean.removeImageFromDisplay(image);
        }
        updateThumbnailSelection();
    }

    /**
     * Getter for the session.
     *
     * @return session of the user.
     */
    public UserSession getSession() {
        return session;
    }

    /**
     * Returns a list of all possible currency.
     *
     * @return list of all currency sorted by abbreviation.
     */
    public List<Currency> getCurrencyList() {
        return currencyList;
    }

    /**
     * Method which is executed when a category is clicked.
     *
     * @param categoryDTO selected category.
     * @return Empty String.
     */
    public String onCategoryClick(CategoryDTO categoryDTO) {
        ad.setCategory(categoryDTO);
        return "";
    }

    /**
     * Getter for the price of this ad.
     *
     * @return price of this ad.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Setter for the price of the ad.
     *
     * @param price new price of the ad.
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Getter for the String representation of the selected Thumbnail.
     *
     * @return Selected Thumbnail as a string.
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * Setter for the String representation of the selected Thumbnail.
     *
     * @param thumbnail Selected thumbnail.
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * Getter for the amount of images for an ad.
     *
     * @return The maximum amount of images a user can upload for an ad.
     */
    public int getImageCount() {
        return Limits.AD_MAX_IMAGE_COUNT;
    }

    /**
     * Getter for the release date of the ad.
     *
     * @return End date of the ad.
     */
    public LocalDateTime getRelease() {
        return release;
    }

    /**
     * Setter for the release date of the ad.
     *
     * @param release Release date of the ad.
     */
    public void setRelease(LocalDateTime release) {
        this.release = release;
    }

    /**
     * Getter for the end date of the ad.
     *
     * @return End date of the ad.
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Setter for the end date of the ad.
     *
     * @param end End date of the ad.
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

}
