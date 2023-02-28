package de.schwarzes_brett.business_logic.services;

import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.ApplicationSettingsDTO;
import de.schwarzes_brett.dto.ImageDTO;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Provides a service to the backing layer to manage application settings. Forwards the store and update operations from backing to access layer.
 */
@Named
@Dependent
public class ApplicationSettingsService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    /**
     * Default constructor.
     */
    public ApplicationSettingsService() {
    }

    /**
     * Updates the application settings.
     *
     * @param settings The updated settings which have to be set.
     * @author Valentin Damjantschitsch.
     */
    public void updateSettings(ApplicationSettingsDTO settings) {
        logger.fine("Updating settings.");
        Transaction transaction = TransactionFactory.produce();
        try {
            DAOFactory.getApplicationSettingsDAO(transaction).updateApplicationSettings(settings);
            transaction.commit();
        } finally {
            transaction.abort();
        }
    }

    /**
     * Gets current application settings.
     * This method does not fetch the imprint and the privacy text.
     *
     * @param settings Instance of {@code ApplicationSettingsDTO} which has to be filled.
     * @author Valentin Damjantschitsch.
     */
    public void fetchSettingsMinimal(ApplicationSettingsDTO settings) {
        logger.fine("Fetching application settings.");
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getApplicationSettingsDAO(transaction).fetchSettingsMinimal(settings);
            transaction.commit();
        }
    }

    /**
     * Loads the current imprint from the application settings.
     *
     * @param settings Instance of {@code ApplicationSettingsDTO} which has to be filled.
     * @author Valentin Damjantschitsch.
     */
    public void fetchImprint(ApplicationSettingsDTO settings) {
        logger.fine("Fetching imprint.");
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getApplicationSettingsDAO(transaction).fetchImprint(settings);
            transaction.commit();
        }
    }

    /**
     * Loads the current privacy text from the application settings.
     *
     * @param settings Instance of {@code ApplicationSettingsDTO} which has to be filled.
     * @author Valentin Damjantschitsch.
     */
    public void fetchPrivacy(ApplicationSettingsDTO settings) {
        logger.fine("Fetching privacy.");
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getApplicationSettingsDAO(transaction).fetchPrivacy(settings);
            transaction.commit();
        }
    }

    /**
     * Inserts a logo for the application.
     *
     * @param applicationSettings The application settings containing the logo to be set.
     * @author Valentin Damjantschitsch.
     */
    public void insertLogo(ApplicationSettingsDTO applicationSettings) {
        logger.fine("Inserting logo.");
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getImageDAO(transaction).insertImage(applicationSettings.getLogo());
            DAOFactory.getApplicationSettingsDAO(transaction).updateApplicationSettings(applicationSettings);
            transaction.commit();
        }
    }

    /**
     * Deletes the logo of the application.
     *
     * @param applicationSettings The settings of the application.
     * @author Valentin Damjantschitsch.
     */
    public void deleteLogo(ApplicationSettingsDTO applicationSettings) {
        logger.fine("Deleting privacy.");
        try (Transaction transaction = TransactionFactory.produce()) {
            DAOFactory.getImageDAO(transaction).deleteImage(applicationSettings.getLogo());
            applicationSettings.setLogo(new ImageDTO());
            DAOFactory.getApplicationSettingsDAO(transaction).updateApplicationSettings(applicationSettings);
            transaction.commit();
        }
    }
}
