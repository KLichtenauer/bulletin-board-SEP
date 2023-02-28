package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.system_test.STBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * A system test to validate the correct behaviour for login and logout.
 *
 * @author Valentin Damjantschitsch
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("NewClassNamingConvention")
public final class LoginLogoutST extends STBase {

    private static final String LOGOUT_BUTTON_ID = "headerForm:header_logoutButton";
    private static final String LOGIN_BUTTON_ID = "headerForm:header_loginButton";
    private static final String MESSAGES_ID = "messages";
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    @SuppressWarnings("checkstyle:MagicNumber")
    void init() {
        driver = getDriver();
        wait = getWait();
        driver.manage().window().setSize(new Dimension(1920, 1080));

    }

    private void failedLogin() {
        getDriver().findElement(By.id("login:username")).click();
        getDriver().findElement(By.id("login:username")).sendKeys("Stefan1");
        getDriver().findElement(By.id("login:password")).sendKeys("123");
        getDriver().findElement(By.id("login:loginButton")).click();
    }

    @Test
    @Order(1)
    void testFailedLogin() {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id(LOGIN_BUTTON_ID)));
        getDriver().findElement(By.id(LOGIN_BUTTON_ID)).click();
        failedLogin();
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id(MESSAGES_ID)));
        WebElement element = driver.findElement(By.id(MESSAGES_ID));
        Assertions.assertNotNull(element);
    }

    @Test
    @Order(2)
    void testSuccessfulLogin() {
        STBaseUtil.login("Stefan1" + getTestSuffix(), "123456", driver, wait);
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id(LOGOUT_BUTTON_ID)));
        Assertions.assertEquals(driver.getTitle(), "Willkommensseite");
    }

    @Test
    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(3)
    void testSuccessfulLogout() {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id(LOGOUT_BUTTON_ID)));
        STBaseUtil.logout(driver, wait);
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id(LOGIN_BUTTON_ID)));
        Assertions.assertEquals(driver.getTitle(), "Landing Page");
    }
}
