package de.schwarzes_brett.backing.servlet;

import de.schwarzes_brett.logging.LoggerProducer;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for web servlets.
 *
 * @author Tim-Florian Feulner
 */
final class ServletUtil {

    private static final Logger LOGGER = LoggerProducer.get(ServletUtil.class);

    /**
     * Private constructor, as this class is a utility class.
     */
    private ServletUtil() {}

    /**
     * Sends an HTTP 404 not found response.
     *
     * @param message  The message to be sent.
     * @param response The response object.
     */
    public static void sendNotFoundResponse(String message, HttpServletResponse response) {
        try {
            final int httpNotFoundCode = 404;
            response.sendError(httpNotFoundCode, message);
            LOGGER.warning(message);
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Could not respond to a client with a HTTP 404 response.", ioe);
        }
    }

}
