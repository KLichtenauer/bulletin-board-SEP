package de.schwarzes_brett.test_util.system_test;

import de.schwarzes_brett.test_util.TestEnvironment;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Manages the lifecycle for a web driver. The type of the web driver, i.e. what browser is used,
 * can be adjusted by setting the environment variable {@code SYSTEM_TEST_BROWSER}.
 * By default, Google Chrome is used.
 *
 * @author Tim-Florian Feulner
 */
final class WebDriverLifecycle {

    private WebInstance webInstance;

    public void init() {
        String browser = System.getenv("SYSTEM_TEST_BROWSER");
        if (browser == null) {
            browser = "";
        }

        switch (browser.toLowerCase()) {
        case "firefox":
            webInstance = WebDriverFactory.createFirefoxWebInstance();
            break;

        case "edge":
            webInstance = WebDriverFactory.createEdgeWebInstance();
            break;

        case "chrome":
        default:
            webInstance = WebDriverFactory.createChromeWebInstance();
            break;
        }
    }

    public void reset() {
        webInstance.driver().get(TestEnvironment.getBaseURL());
        webInstance.driverWait().until(ExpectedConditions.titleContains("Landing"));
    }

    public void destroy() {
        webInstance.driver().close();
    }

    public WebInstance getWebInstance() {
        return webInstance;
    }

}
