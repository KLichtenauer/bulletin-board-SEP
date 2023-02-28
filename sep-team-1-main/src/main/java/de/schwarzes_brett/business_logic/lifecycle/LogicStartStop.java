package de.schwarzes_brett.business_logic.lifecycle;

import de.schwarzes_brett.business_logic.mail.MailService;
import de.schwarzes_brett.business_logic.periodic.MaintenanceExecutor;
import de.schwarzes_brett.data_access.exception.DataStorageUnavailableException;
import de.schwarzes_brett.data_access.lifecycle.DataAccessStartStop;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.InputStream;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Manages the start and stop of the business-logic layer.
 *
 * @author Tim-Florian Feulner
 */
@Named
@Dependent
public class LogicStartStop {

    @Inject
    private Logger logger;

    @Inject
    private MaintenanceExecutor maintenanceExecutor;

    /**
     * Default constructor.
     */
    public LogicStartStop() {}

    /**
     * Initializes the applications business-logic layer.
     *
     * @param resourceFetcher The callback to retrieve a resource file.
     */
    public void init(Function<String, InputStream> resourceFetcher) {
        logger.finest("Starting the startup process of the business logic.");

        try {
            DataAccessStartStop.init(resourceFetcher);
        } catch (DataStorageUnavailableException e) {
            String message = "Error connecting to database, possibly due to a wrong configuration.";
            logger.severe(message);
            throw new DataStorageUnavailableException(message, e);
        }

        if (!MailService.checkSMTPConfiguration()) {
            String message = "Error connecting to SMTP server, possibly due to a wrong configuration.";
            logger.severe(message);
            throw new RuntimeException(message);
        }

        maintenanceExecutor.init();
        registerShutdownHook();
        logger.finest("Business logic startup complete.");
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.finest("Starting the shutdown process of the business logic inside a shutdown hook.");
            DataAccessStartStop.destroy();
            logger.finest("Business logic shutdown complete inside a shutdown hook.");
        }));
    }

    /**
     * Stops the applications business-logic layer.
     */
    public void destroy() {
        logger.finest("Starting the shutdown process of the business logic.");
        maintenanceExecutor.destroy();
        DataAccessStartStop.destroy();
        logger.finest("Business logic shutdown complete.");
    }

}
