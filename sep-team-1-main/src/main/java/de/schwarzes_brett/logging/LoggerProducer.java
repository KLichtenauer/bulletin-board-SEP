package de.schwarzes_brett.logging;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Produces loggers for a specific context.
 *
 * @author Tim-Florian Feulner
 */
public class LoggerProducer {

    /**
     * Default constructor.
     */
    public LoggerProducer() {}

    /**
     * Produces a logger for a specific class.
     *
     * @param targetClass The class that uses the logger.
     * @return The produced logger.
     */
    public static Logger get(Class<?> targetClass) {
        Logger logger = Logger.getLogger(targetClass.getName());
        logger.setLevel(Level.FINEST);
        return logger;
    }

    /**
     * Produces a logger at a given CDI {@link InjectionPoint}.
     *
     * @param injectionPoint The information of the injection point.
     * @return The produced logger.
     */
    @Produces
    public Logger produce(InjectionPoint injectionPoint) {
        if (injectionPoint == null) {
            // This should not happen if CDI functions correctly.
            throw new RuntimeException("Injection point of producer method is null.");
        } else {
            return get(injectionPoint.getMember().getDeclaringClass());
        }
    }

}
