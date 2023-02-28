package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.cache.ApplicationSettingsDaoCache;
import de.schwarzes_brett.data_access.exception.DataStorageAccessException;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.ApplicationSettingsDTO;
import de.schwarzes_brett.dto.ImageDTO;
import de.schwarzes_brett.logging.LoggerProducer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Controls the PostgresSql database access for the application settings.
 */
public class ApplicationSettingsDAOPsql extends BaseDAOPsql implements ApplicationSettingsDAO {

    private static final ApplicationSettingsDaoCache CACHE = ApplicationSettingsDaoCache.getInstance();
    private final Logger logger = LoggerProducer.get(ApplicationSettingsDAOPsql.class);
    private ApplicationSettingsDTO settingsToBeCached;
    private boolean clearCache;

    /**
     * Creates dao for getting application settings via given transaction.
     *
     * @param transaction The transaction for database access.
     */
    public ApplicationSettingsDAOPsql(TransactionPsql transaction) {
        super(transaction);
        clearCache = false;
    }

    /**
     * {@inheritDoc}
     *
     * @author Daniel Lipp
     */
    @Override
    public void commitChangesToCache() {
        if (clearCache) {
            CACHE.invalidate();
        } else if (settingsToBeCached != null) {
            CACHE.setValue(settingsToBeCached);
            settingsToBeCached = null;
        }
    }


    /**
     * {@inheritDoc}
     *
     * @author Valentin Damjantschitsch.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @Override
    public void updateApplicationSettings(ApplicationSettingsDTO settings) {
        logger.fine("Updating application settings.");
        Connection connection = getTransaction().getConnection();
        String query = "UPDATE schwarzes_brett.application_settings SET operator_name = ?, operator_contact = ?, operator_description = ?,"
                       + "imprint = ?, privacy_policy = ? , css_name = ?, operator_img = ?;";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, settings.getName());
            ps.setString(2, settings.getContact());
            ps.setString(3, settings.getDescription());
            ps.setString(4, settings.getImprint());
            ps.setString(5, settings.getPrivacyPolicy());
            ps.setString(6, settings.getStyleSheet());
            DAOPsqlUtil.insertNullableLong(7, settings.getLogo().getId(), ps);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("");
            }
            clearCache = true;
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on application settings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Updating application settings was successful.");
    }

    /**
     * {@inheritDoc}
     *
     * @author Valentin Damjantschitsch
     */
    @Override
    public void fetchSettingsMinimal(ApplicationSettingsDTO settings) {
        ApplicationSettingsDTO cachedSettings = CACHE.getValue();
        if (cachedSettings != null) {
            settings.copyFrom(cachedSettings);
            return;
        }
        logger.fine("Fetching minimal settings.");
        Connection connection = getTransaction().getConnection();
        String query = "SELECT * FROM schwarzes_brett.application_settings;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    settings.setName(rs.getString("operator_name"));
                    ImageDTO logo = new ImageDTO();
                    logo.setId(DAOPsqlUtil.extractNullableLong("operator_img", rs));
                    settings.setLogo(logo);
                    settings.setDescription(rs.getString("operator_description"));
                    settings.setContact(rs.getString("operator_contact"));
                    settings.setStyleSheet(rs.getString("css_name"));
                    settingsToBeCached = settings;
                } else {
                    logger.fine("Table for application settings is empty");
                }
            }
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on application settings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Fetching minimal settings was successful.");
    }

    /**
     * {@inheritDoc}
     *
     * @author Valentin Damjantschitsch.
     */
    @Override
    public void fetchImprint(ApplicationSettingsDTO settings) {
        logger.fine("Fetching imprint.");
        Connection connection = getTransaction().getConnection();
        String query = "SELECT imprint FROM schwarzes_brett.application_settings;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    settings.setImprint(rs.getString("imprint"));
                } else {
                    logger.fine("Table for application settings is empty");
                }
            }
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on application settings");
            throw new DataStorageAccessException(e.getMessage(), e);
        }
        logger.fine("Fetching imprint was successful.");
    }

    /**
     * {@inheritDoc}
     *
     * @author Valentin Damjantschitsch.
     */
    @Override
    public void fetchPrivacy(ApplicationSettingsDTO settings) {
        logger.fine("Fetching privacy.");
        Connection connection = getTransaction().getConnection();
        String query = "SELECT privacy_policy FROM schwarzes_brett.application_settings;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    settings.setPrivacyPolicy(rs.getString("privacy_policy"));
                } else {
                    logger.fine("Table for application settings is empty");
                }
            }
        } catch (SQLException e) {
            logger.severe("Data Storage is not available while executing query on application settings");
            throw new DataStorageAccessException(e.getMessage());
        }
        logger.fine("Fetching privacy was successful");
    }
}
