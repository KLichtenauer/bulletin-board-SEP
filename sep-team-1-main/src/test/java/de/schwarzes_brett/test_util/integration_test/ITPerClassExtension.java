package de.schwarzes_brett.test_util.integration_test;

import com.icegreen.greenmail.util.GreenMail;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.sql.SQLException;

/**
 * A JUnit5 extension for integration testing with a per class JUnit lifecycle.
 *
 * @author Tim-Florian Feulner
 */
public final class ITPerClassExtension implements BeforeAllCallback, AfterAllCallback {

    private final ITLifecycle lifecycle = new ITLifecycle();

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeAll(ExtensionContext extensionContext) throws SQLException, IOException, InterruptedException {
        lifecycle.init((ITBase) extensionContext.getRequiredTestInstance());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterAll(ExtensionContext extensionContext) throws IOException {
        lifecycle.destroy((ITBase) extensionContext.getRequiredTestInstance());
    }

    /**
     * Gets the mail instance to be used for accessing received mails.
     *
     * @return The mail instance.
     */
    public GreenMail getMail() {
        return lifecycle.getMail();
    }

}
