package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.system_test.STBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
 * A system test to validate the correct displaying of messages.
 *
 * @author Valentin Damjantschitsch
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("NewClassNamingConvention")
public class MessagesST extends STBase {
    private static final String AD_MESSAGE_BUTTON_ID = "ad_messageForm:ad_messageButton";
    private static final String MESSAGE_INPUT_ID = "message_form:message_content";
    private static final String MESSAGE_CHECKBOX_PUBLIC_ID = "message_form:message_public";
    private static final String MESSAGE_SEND_BUTTON_ID = "message_form:message_sendButton";
    private static final String MESSAGE_PUBLIC = "Ist die Skulptur noch zu haben?";
    private static final String MESSAGE_ANONYM = "Aber mal im Ernst Lisa, das ist zu teuer!";
    private final String skulpturPath = "view/public/ad.xhtml?id=400" + getTestSuffix();
    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * Init method.
     */
    @BeforeEach
    @SuppressWarnings("checkstyle:MagicNumber")
    void init() {
        driver = getDriver();
        wait = getWait();
        driver.manage().window().setSize(new Dimension(1920, 1080));
    }

    private Integer countDataTableChildren() {
        WebElement dataTable = this.driver.findElement(By.xpath("//table[@id='ad_messageTable']/tbody/tr/td[2]"));
        return Integer.parseInt(dataTable.getDomProperty("childElementCount"));
    }

    @Test
    @Order(1)
    void testPublicMessage() {
        STBaseUtil.login("Kilian2" + getTestSuffix(), "654321", driver, wait);
        STBaseUtil.navigateTo(skulpturPath, driver);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(AD_MESSAGE_BUTTON_ID)));
        driver.findElement(By.id(AD_MESSAGE_BUTTON_ID)).click();

        driver.findElement(By.id(MESSAGE_INPUT_ID)).sendKeys(MESSAGE_PUBLIC);
        driver.findElement(By.id(MESSAGE_SEND_BUTTON_ID)).click();

        STBaseUtil.logout(driver, wait);
        STBaseUtil.login("Stefan1" + getTestSuffix(), "123456", driver, wait);
        STBaseUtil.navigateTo(skulpturPath, driver);
        String message = driver.findElement(By.id("ad_messageTable:0:messageEntry_content")).getText();
        Assertions.assertEquals(MESSAGE_PUBLIC, message);
    }

    @Test
    @Order(2)
    void testAnonymouslyMessage() {
        STBaseUtil.logout(driver, wait);
        STBaseUtil.login("Kilian2" + getTestSuffix(), "654321", driver, wait);
        STBaseUtil.navigateTo(skulpturPath, driver);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(AD_MESSAGE_BUTTON_ID)));
        driver.findElement(By.id(AD_MESSAGE_BUTTON_ID)).click();

        driver.findElement(By.id(MESSAGE_INPUT_ID)).sendKeys(MESSAGE_ANONYM);
        driver.findElement(By.id(MESSAGE_CHECKBOX_PUBLIC_ID)).click();
        driver.findElement(By.id(MESSAGE_SEND_BUTTON_ID)).click();

        STBaseUtil.logout(driver, wait);
        STBaseUtil.login("Stefan1" + getTestSuffix(), "123456", driver, wait);
        STBaseUtil.navigateTo(skulpturPath, driver);

        Assertions.assertEquals(1, countDataTableChildren());
    }
}
