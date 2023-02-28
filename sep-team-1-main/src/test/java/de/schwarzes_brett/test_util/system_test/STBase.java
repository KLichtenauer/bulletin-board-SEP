package de.schwarzes_brett.test_util.system_test;

import de.schwarzes_brett.test_util.TestEnvironment;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * A base class for system tests. Manages some internal lifecycle requirements.
 *
 * @author Tim-Florian Feulner
 */
public abstract class STBase {

    @RegisterExtension
    private static final STExtension ST_EXTENSION = new STExtension(1);

    /**
     * Gets the current web driver.
     *
     * @return The web driver instance.
     */
    protected WebDriver getDriver() {
        return ST_EXTENSION.getWebInstance(0).driver();
    }

    /**
     * Gets the current wait object associated with the web driver.
     *
     * @return The web driver wait instance.
     */
    protected WebDriverWait getWait() {
        return ST_EXTENSION.getWebInstance(0).driverWait();
    }

    /**
     * Gets the current mail object for accessing received mails.
     *
     * @return The mail object.
     */
    protected MailAccessor getMail() {
        return ST_EXTENSION.getMail();
    }

    /**
     * Returns a suffix to identify the current test process.
     * This is needed for performing parallel load tests.
     *
     * @return The test instance suffix.
     */
    protected String getTestSuffix() {
        String processId = TestEnvironment.getProcessId();
        return processId != null ? processId : "";
    }

}
