package de.schwarzes_brett.test_util.system_test;

import de.schwarzes_brett.test_util.TestEnvironment;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * A factory for web drivers of different browsers that are necessary for Selenium testing.
 * Supported browsers are Google Chrome, Mozilla Firefox and Microsoft Edge.
 *
 * @author Tim-Florian Feulner
 */
final class WebDriverFactory {

    /**
     * The number of seconds to wait with {@link WebDriverFactory#wait}.
     */
    private static final int WAITING_SECONDS = 10;

    private WebDriverFactory() {}

    public static WebInstance createChromeWebInstance() {
        WebDriver driver = createChromeDriver();
        return new WebInstance(driver, new WebDriverWait(driver, Duration.ofSeconds(WAITING_SECONDS)));
    }

    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setAcceptInsecureCerts(true);
        if (System.getenv("GITLAB_CI") != null || System.getenv("GITHUB_ACTIONS") != null || TestEnvironment.isLoadTest()) {
            options.setHeadless(true);
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable.gpu");
        }
        return new ChromeDriver(options);
    }

    public static WebInstance createFirefoxWebInstance() {
        WebDriver driver = createFirefoxDriver();
        return new WebInstance(driver, new WebDriverWait(driver, Duration.ofSeconds(WAITING_SECONDS)));
    }

    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.setAcceptInsecureCerts(true);
        if (System.getenv("GITLAB_CI") != null || System.getenv("GITHUB_ACTIONS") != null || TestEnvironment.isLoadTest()) {
            options.setHeadless(true);
        }
        return new FirefoxDriver(options);
    }

    public static WebInstance createEdgeWebInstance() {
        WebDriver driver = createEdgeDriver();
        return new WebInstance(driver, new WebDriverWait(driver, Duration.ofSeconds(WAITING_SECONDS)));
    }

    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        options.setAcceptInsecureCerts(true);
        if (System.getenv("GITLAB_CI") != null || System.getenv("GITHUB_ACTIONS") != null || TestEnvironment.isLoadTest()) {
            options.setHeadless(true);
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable.gpu");
        }
        return new EdgeDriver(options);
    }

}
