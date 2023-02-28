package de.schwarzes_brett.backing.lifecycle;

import de.schwarzes_brett.business_logic.lifecycle.LogicStartStop;
import de.schwarzes_brett.logging.LoggerLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Destroyed;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;

import java.util.logging.Logger;

/**
 * Manages start and stop of the application.
 *
 * @author Tim-Florian Feulner
 */
@ApplicationScoped
public class SystemStartStop {

    /**
     * The {@code Logger} instance to be used in this class.
     */
    @Inject
    private transient Logger logger;

    @Inject
    private LogicStartStop logicStartStop;

    /**
     * Default constructor.
     */
    public SystemStartStop() {}

    /**
     * Starts the application.
     *
     * @param context Instance of the type {@code ServletContext}.
     */
    public void init(@Observes @Initialized(ApplicationScoped.class) ServletContext context) {
        logger.finer("Starting the application startup process.");

        LoggerLoader.init(context::getResourceAsStream);
        logicStartStop.init(context::getResourceAsStream);

        logger.info("Application startup complete.");
    }

    /**
     * Stops the application.
     *
     * @param context Instance of the type {@code ServletContext}.
     */
    public void destroy(@Observes @Destroyed(ApplicationScoped.class) ServletContext context) {
        logger.finer("Starting the application shutdown process.");

        logicStartStop.destroy();

        logger.info("Application shutdown complete.");
    }

}
