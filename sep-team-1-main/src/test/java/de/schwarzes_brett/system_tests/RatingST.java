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
public class RatingST extends STBase {
    private static final String AD_RATE_BUTTON_ID = "ad_rateForm:ad_rateButton";
    private static final String AD_RATING_SUBMIT_BUTTON_ID = "rate_form:rate_submitButton";
    private static final String USER_RATING_ID = "ad_rating";
    private static final String USERNAME_01 = "Stefan1";
    private static final String USER_PASSWORD_01 = "123456";
    private static final String USERNAME_02 = "Kilian2";
    private static final String USER_PASSWORD_02 = "654321";
    private final String skuplturPath = "view/public/ad.xhtml?id=400" + getTestSuffix();
    private WebDriver driver;
    private WebDriverWait wait;

    private void setRating(int rating) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table[@id='rate_form:rate_radioButtons']/tbody/tr/td[" + rating + "]/div/div[2]/span")));
        if (!driver.findElement(By.xpath("//table[@id='rate_form:rate_radioButtons']/tbody/tr/td[" + rating + "]/div/div[2]/span")).isSelected()) {
            driver.findElement(By.xpath("//table[@id='rate_form:rate_radioButtons']/tbody/tr/td[" + rating + "]/div/div[2]/span")).click();
        }
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(AD_RATING_SUBMIT_BUTTON_ID)));
        driver.findElement(By.id(AD_RATING_SUBMIT_BUTTON_ID)).click();
    }

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

    @Test
    @Order(1)
    void testSingleRating() {
        STBaseUtil.login(USERNAME_01 + getTestSuffix(), USER_PASSWORD_01, driver, wait);
        STBaseUtil.navigateTo(skuplturPath, driver);

        wait.until(ExpectedConditions.elementToBeClickable(By.id(AD_RATE_BUTTON_ID)));
        driver.findElement(By.id(AD_RATE_BUTTON_ID)).click();
        setRating(1);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(USER_RATING_ID)));
        String ratingOfUser = driver.findElement(By.id(USER_RATING_ID)).getText();
        Assertions.assertEquals(ratingOfUser, "1.0★");
    }

    @Test
    @Order(2)
    @SuppressWarnings("checkstyle:MagicNumber")
    void testRatingCalculation() {
        STBaseUtil.logout(driver, wait);
        STBaseUtil.login(USERNAME_02 + getTestSuffix(), USER_PASSWORD_02, driver, wait);
        STBaseUtil.navigateTo(skuplturPath, driver);

        wait.until(ExpectedConditions.elementToBeClickable(By.id(AD_RATE_BUTTON_ID)));
        driver.findElement(By.id(AD_RATE_BUTTON_ID)).click();
        setRating(4);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(USER_RATING_ID)));
        String ratingOfUser = driver.findElement(By.id(USER_RATING_ID)).getText();
        Assertions.assertEquals(ratingOfUser, "2.5★");
    }
}
