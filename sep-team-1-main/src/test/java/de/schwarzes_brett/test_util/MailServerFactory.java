package de.schwarzes_brett.test_util;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * A factory class to produce mail server instances.
 *
 * @author Tim-Florian Feulner
 */
public final class MailServerFactory {

    private MailServerFactory() {}

    /**
     * Creates a new mail server instance.
     *
     * @return The new instance.
     */
    public static GreenMail createMailServer() {
        return new GreenMail(new ServerSetup(readMailPort(), null, "smtp"));
    }

    private static int readMailPort() {
        Properties config = new Properties();
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("WEB-INF/config/config.properties")) {
            if (input == null) {
                throw new RuntimeException("Could not find config file.");
            }
            try (InputStreamReader configReader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                config.load(configReader);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not open config file.", e);
        }
        return Integer.parseInt(config.getProperty("MAIL_PORT"));
    }

}
