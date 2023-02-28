package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.system_test.STBase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * System Test for the follow ad / user functionality.
 *
 * @author Jonas Elsper
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(Lifecycle.PER_CLASS)
public final class AbonnementST extends STBase {
    private static final String FOLLOW_SUBMIT_BUTTON_ID = "follow_form:follow_saveButton";
    private static final String WELCOME_ABONNEMENTS_BUTTON_ID = "welcome_subscribedForm:welcome_subscribedButton";
    private static final String WELCOME_DATA_TABLE_ID = "welcome_paginationForm:welcome_pagination:pagination_dataTable";
    private static final String WELCOME_PAGINATION_SEARCH_INPUT_ID = "welcome_paginationForm:welcome_pagination:searchBar_searchInput";
    private static final String WELCOME_PAGINATION_EXPIRED_ADS_SELECT_ID = "welcome_paginationForm:welcome_pagination:pagination_expiredAdsSelect";
    private static final String WELCOME_PAGINATION_SEARCH_BUTTON_ID = "welcome_paginationForm:welcome_pagination:searchBar_searchButton";
    private static final String FOLLOW_AD_CHECKBOX_ID = "follow_form:follow_followAd";
    private static final String FOLLOW_USER_CHECKBOX_ID = "follow_form:follow_followUser";
    private static final String AD_PAGE_FOLLOW_BUTTON_ID = "ad_followForm:ad_followButton";
    private static final String LANDING_PAGE_URL = "view/public/landing.xhtml";
    private static final String WELCOME_PAGE_URL = "view/user/welcome.xhtml";
    private static final String AD_PAGE_URL = "view/public/ad.xhtml?id=";
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    @SuppressWarnings("checkstyle:MagicNumber")
    void init() {
        this.driver = this.getDriver();
        this.wait = this.getWait();
        this.driver.manage().window().setSize(new Dimension(1920, 1080));
        STBaseUtil.navigateTo(LANDING_PAGE_URL, driver);
    }

    @Test
    @Order(1)
        // /T220/
    void testFollowAd() {
        STBaseUtil.login("Lisa4" + getTestSuffix(), "abcdefg", driver, wait);
        STBaseUtil.followOrUnfollowAd(getAdId(), driver, wait);
        Assertions.assertEquals("true", this.driver.findElement(By.id(FOLLOW_AD_CHECKBOX_ID)).getAttribute("checked"));
        STBaseUtil.navigateTo(WELCOME_PAGE_URL, driver);
        this.waitAndClick(By.id(WELCOME_ABONNEMENTS_BUTTON_ID));
        this.waitAndClick(By.id(WELCOME_PAGINATION_SEARCH_INPUT_ID));
        this.driver.findElement(By.id(WELCOME_PAGINATION_SEARCH_INPUT_ID)).sendKeys("Schrank" + this.getTestSuffix());
        this.waitAndClick(By.id(WELCOME_PAGINATION_SEARCH_BUTTON_ID));
        this.wait.until(ExpectedConditions.presenceOfElementLocated(By.id(WELCOME_DATA_TABLE_ID)));
        Assertions.assertEquals("großer benutzter Schrank" + getTestSuffix() + " mit Türen", this.getTitleOfRow(1));
    }

    @Test
    @Order(2)
        // /T230/
    void testUnfollowAd() {
        STBaseUtil.followOrUnfollowAd(getAdId(), driver, wait);
        Assertions.assertNull(this.driver.findElement(By.id(FOLLOW_AD_CHECKBOX_ID)).getAttribute("checked"));
        STBaseUtil.navigateTo(WELCOME_PAGE_URL, driver);
        this.waitAndClick(By.id(WELCOME_ABONNEMENTS_BUTTON_ID));
        this.waitAndClick(By.id(WELCOME_PAGINATION_SEARCH_INPUT_ID));
        this.driver.findElement(By.id(WELCOME_PAGINATION_SEARCH_INPUT_ID)).sendKeys("Schrank" + this.getTestSuffix());
        this.waitAndClick(By.id(WELCOME_PAGINATION_SEARCH_BUTTON_ID));
        this.wait.until(ExpectedConditions.presenceOfElementLocated(By.id(WELCOME_DATA_TABLE_ID)));
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.countDataTableChildren()),
                () -> Assertions.assertEquals("", this.getTitleOfRow(1))
        );
    }

    @Test
    @Order(3)
    @SuppressWarnings("checkstyle:MagicNumber")
        // /T240/
    void testFollowUser() {
        followOrUnfollowUser();
        Assertions.assertEquals("true", this.driver.findElement(By.id(FOLLOW_USER_CHECKBOX_ID)).getAttribute("checked"));
        STBaseUtil.navigateTo(WELCOME_PAGE_URL, driver);
        this.waitAndClick(By.id(WELCOME_ABONNEMENTS_BUTTON_ID));
        this.waitAndClick(By.id(WELCOME_PAGINATION_EXPIRED_ADS_SELECT_ID));
        this.waitAndClick(By.id(WELCOME_PAGINATION_SEARCH_BUTTON_ID));
        this.wait.until(ExpectedConditions.presenceOfElementLocated(By.id(WELCOME_DATA_TABLE_ID)));
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, this.countDataTableChildren()),
                () -> Assertions.assertEquals("großer benutzter Schrank" + getTestSuffix() + " mit Türen", this.getTitleOfRow(1)),
                () -> Assertions.assertEquals("kleiner schöner Schrank" + getTestSuffix() + " mit Fächer", this.getTitleOfRow(2))
        );
    }

    @Test
    @Order(4)
    @SuppressWarnings("checkstyle:MagicNumber")
        // /T250/
    void testUnfollowUser() {
        followOrUnfollowUser();
        Assertions.assertNull(this.driver.findElement(By.id(FOLLOW_USER_CHECKBOX_ID)).getAttribute("checked"));
        STBaseUtil.navigateTo(WELCOME_PAGE_URL, driver);
        this.waitAndClick(By.id(WELCOME_ABONNEMENTS_BUTTON_ID));
        this.waitAndClick(By.id(WELCOME_PAGINATION_EXPIRED_ADS_SELECT_ID));
        this.waitAndClick(By.id(WELCOME_PAGINATION_SEARCH_BUTTON_ID));
        this.wait.until(ExpectedConditions.presenceOfElementLocated(By.id(WELCOME_DATA_TABLE_ID)));
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, this.countDataTableChildren()),
                () -> Assertions.assertEquals("", this.getTitleOfRow(1))
        );
    }

    private void followOrUnfollowUser() {
        STBaseUtil.navigateTo(AD_PAGE_URL + getAdId(), driver);
        WebElement adPageFollowButton = driver.findElement(By.id(AD_PAGE_FOLLOW_BUTTON_ID));
        wait.until(ExpectedConditions.elementToBeClickable(adPageFollowButton));
        adPageFollowButton.click();
        this.waitAndClick(By.id(FOLLOW_USER_CHECKBOX_ID));
        this.waitAndClick(By.id(FOLLOW_SUBMIT_BUTTON_ID));
        this.wait.until(ExpectedConditions.elementToBeClickable(By.id(FOLLOW_USER_CHECKBOX_ID)));
    }

    private int getAdId() {
        return Integer.parseInt("100" + getTestSuffix());
    }

    private Integer countDataTableChildren() {
        return STBaseUtil.countDataTableChildren(driver, "//*[@id=\"welcome_paginationForm:welcome_pagination:pagination_dataTable\"]/tbody");
    }

    private String getTitleOfRow(int index) {
        return STBaseUtil.getTitleOfRow(driver, wait, index, WELCOME_DATA_TABLE_ID);
    }

    private void waitAndClick(By by) {
        STBaseUtil.waitAndClick(by, driver, wait);
    }
}
