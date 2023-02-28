package de.schwarzes_brett.test_util.system_test;

import de.schwarzes_brett.test_util.TestEnvironment;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * A base class for system tests that require two concurrent browsers.
 * Manages some internal lifecycle requirements.
 * Spawns two web driver instances.
 *
 * @author Tim-Florian Feulner
 */
public abstract class DualSTBase {

    @RegisterExtension
    private static final STExtension ST_EXTENSION = new STExtension(2);

    /**
     * Gets the current web driver.
     *
     * @param index Index of the web driver.
     * @return The web driver instance.
     */
    protected WebDriver getDriver(int index) {
        return ST_EXTENSION.getWebInstance(index).driver();
    }

    /**
     * Gets the current wait object associated with the web driver.
     *
     * @param index Index of the web driver.
     * @return The web driver wait instance.
     */
    protected WebDriverWait getWait(int index) {
        return ST_EXTENSION.getWebInstance(index).driverWait();
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
