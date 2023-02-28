package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.business_logic.services.ApplicationSettingsService;
import de.schwarzes_brett.dto.ApplicationSettingsDTO;
import de.schwarzes_brett.dto.ImageDTO;
import jakarta.annotation.PostConstruct;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.shaded.commons.io.FilenameUtils;

import java.io.File;
import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Performing changes to settings, by admins.
 *
 * @author Valentin Damjantschitsch.
 */
@Named
@ViewScoped
public class SettingsBean implements ImageUploadHolder, NotificationDisplay, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The settings service.
     */
    @Inject
    private ApplicationSettingsService applicationSettingsService;

    /**
     * Used to upload the logo.
     */
    @Inject
    private ImageUploadBean imageUploadBean;

    /**
     * The current settings.
     */
    private ApplicationSettingsDTO settings;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Used to get the path for resources.
     */
    @Inject
    private transient ExternalContext externalContext;

    /**
     * Default constructor.
     */
    public SettingsBean() {
    }

    /**
     * Getter for the settings which should be edited.
     *
     * @return An instance of {@code ApplicationSettingsDTO}.
     */
    public ApplicationSettingsDTO getSettings() {
        return settings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void displayNotifications() {

    }

    /**
     * Saves the applied changes to the settings.
     */
    public void save() {

    }

    /**
     * Initializes the bean.
     */
    @PostConstruct
    public void init() {
        logger.log(Level.FINEST, "Initializing SettingsBean.java");
        settings = new ApplicationSettingsDTO();
        applicationSettingsService.fetchSettingsMinimal(settings);
        applicationSettingsService.fetchImprint(settings);
        applicationSettingsService.fetchPrivacy(settings);

        if (settings.getLogo().getId() != null) {
            imageUploadBean.addImageToDisplay(settings.getLogo());
        }

        logger.log(Level.FINEST, "Initializing SettingsBean.java done");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageUploadBean getImageUploadBean() {
        return imageUploadBean;
    }

    /**
     * Updates the settings of the application.
     *
     * @return The target address.
     */
    public String updateSettings() {
        logger.log(Level.FINEST, "Updating SettingsBean.java");
        applicationSettingsService.updateSettings(settings);
        logger.log(Level.FINEST, "Updating SettingsBean.java done");
        return "/view/admin/settings?faces-redirect=true";
    }

    /**
     * Uploads the logo.
     *
     * @param uploadStream The image data to upload.
     */
    @Override
    public void uploadImage(InputStream uploadStream) {
        logger.info("Uploading logo.");
        ImageDTO logo = new ImageDTO();
        logo.setStoreStream(uploadStream);
        settings.setLogo(logo);
        applicationSettingsService.insertLogo(settings);
        imageUploadBean.addImageToDisplay(logo);
    }

    /**
     * Deletes the logo.
     *
     * @param image The logo image to delete.
     */
    @Override
    public void deleteImage(ImageDTO image) {
        logger.info("Deleting logo.");
        applicationSettingsService.deleteLogo(settings);
        imageUploadBean.removeImageFromDisplay(image);
    }

    /**
     * Scans the CustomStyles folder for css stylesheets.
     *
     * @return A list of all style sheets.
     */
    public List<String> getListOfStylesheets() {
        String path;
        try {
            path = externalContext.getResource("resources/css").getPath();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Path not existing", e);
        }
        File folder = new File(path + "/CustomStyles");
        File[] listOfFiles = folder.listFiles();
        List<String> styleSheets = new ArrayList<>();
        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.getName().endsWith(".css")) {
                    styleSheets.add(FilenameUtils.removeExtension(listOfFile.getName()));
                }
            }
            return styleSheets;
        } else {
            throw new RuntimeException("No Stylesheets Available");
        }
    }
}
