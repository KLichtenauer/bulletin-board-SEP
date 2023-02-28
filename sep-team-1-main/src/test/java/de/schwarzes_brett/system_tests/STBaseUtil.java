package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.TestEnvironment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Basic utility class for often needed operations like logging in as user or admin.
 *
 * @author Kilian Lichtenauer
 */
final class STBaseUtil {

    private static final String AD_PAGE_URL = "view/public/ad.xhtml?id=";
    private static final String AD_PAGE_FOLLOW_BUTTON_ID = "ad_followForm:ad_followButton";
    private static final String FOLLOW_PAGE_FOLLOW_AD_CHECKBOX_ID = "follow_form:follow_followAd";
    private static final String FOLLOW_PAGE_SUBMIT_BUTTON_ID = "follow_form:follow_saveButton";
    private static final String LOGIN_BUTTON_ID = "headerForm:header_loginButton";
    private static final String LOGOUT_BUTTON_ID = "headerForm:header_logoutButton";

    private STBaseUtil() {}

    /**
     * Performs the procedure of logging in as a user with given user-data.
     *
     * @param username The username of the user which gets logged in.
     * @param password The password of the user which gets logged in.
     * @param driver   The driver on which the procedure should get performed on.
     * @param wait     The WebDriverWait instance used for waiting till certain conditions are met.
     */
    public static void login(String username, String password, WebDriver driver, WebDriverWait wait) {
        waitAndClick(By.id(LOGIN_BUTTON_ID), driver, wait);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("login:username")));
        driver.findElement(By.id("login:username")).click();
        driver.findElement(By.id("login:username")).sendKeys(username);
        driver.findElement(By.id("login:password")).sendKeys(password);
        driver.findElement(By.id("login:loginButton")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(LOGOUT_BUTTON_ID)));
    }

    /**
     * Performs the procedure of logging in as the admin "admin".
     *
     * @param driver The driver on which the procedure should get performed on.
     * @param wait   The WebDriverWait instance used for waiting till certain conditions are met.
     */
    public static void loginAsAdmin(WebDriver driver, WebDriverWait wait) {
        login("admin", "start123", driver, wait);
    }

    /**
     * Performs the procedure of logging out.
     *
     * @param driver The driver on which the procedure should get performed on.
     * @param wait   The WebDriverWait instance used for waiting till certain conditions are met.
     */
    public static void logout(WebDriver driver, WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(LOGOUT_BUTTON_ID)));
        driver.findElement(By.id(LOGOUT_BUTTON_ID)).click();
    }


    /**
     * Performs a redirect to the url: {@code landingPageUrl} + filePath where the filepath consists of
     * "the facelets folder"/"the facelets name".xhtml.
     *
     * @param filePath The filepath of the facelets to which it will be redirected.
     * @param driver   The driver on which the procedure should get performed on.
     */
    public static void navigateTo(String filePath, WebDriver driver) {
        driver.get(TestEnvironment.getBaseURL() + filePath);
    }

    /**
     * Returns the WebElement of the category with the given name.
     *
     * @param driver Driver with a site with the category navigation.
     * @param wait   The wait object for the driver.
     * @param name   Name of the desired category.
     * @return WebElement of the category with the given name.
     */
    public static WebElement getCategoryElement(WebDriver driver, WebDriverWait wait, String name) {
        By locator = By.className("ui-button-text");
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        List<WebElement> categoryElements = driver.findElements(locator);
        boolean categoryExists = false;
        WebElement foundCategory = null;
        for (int i = 0; i < categoryElements.size() && !categoryExists; i++) {
            categoryExists = categoryElements.get(i).getText().equals(name);
            if (categoryExists) {
                foundCategory = categoryElements.get(i);
            }
        }
        return foundCategory;
    }

    /**
     * Follows or unfollows a certain ad for a user. The user must already be logged in! After execution, driver is on the follow page.
     *
     * @param adId   The id of the ad to follow.
     * @param driver The executing WebDriver.
     * @param wait   The WebDriverWait instance used for waiting until certain conditions are fulfilled.
     */
    public static void followOrUnfollowAd(int adId, WebDriver driver, WebDriverWait wait) {
        navigateTo(AD_PAGE_URL + adId, driver);
        By adPageFollowButtonLocator = By.id(AD_PAGE_FOLLOW_BUTTON_ID);
        wait.until(ExpectedConditions.presenceOfElementLocated(adPageFollowButtonLocator));
        WebElement adPageFollowButton = driver.findElement(adPageFollowButtonLocator);
        wait.until(ExpectedConditions.elementToBeClickable(adPageFollowButton));
        adPageFollowButton.click();
        WebElement followPageFollowAdCheckbox = driver.findElement(By.id(FOLLOW_PAGE_FOLLOW_AD_CHECKBOX_ID));
        wait.until(ExpectedConditions.elementToBeClickable(followPageFollowAdCheckbox));
        followPageFollowAdCheckbox.click();
        WebElement followPageSubmitButton = driver.findElement(By.id(FOLLOW_PAGE_SUBMIT_BUTTON_ID));
        wait.until(ExpectedConditions.elementToBeClickable(followPageSubmitButton));
        followPageSubmitButton.click();
    }

    /**
     * Waits until a webelement with specific {@code By} is clickable and then clicks it.
     *
     * @param by     The element which gets clicked.
     * @param driver The executing WebDriver.
     * @param wait   The WebDriverWait instance used for waiting until certain conditions are fulfilled.
     */
    public static void waitAndClick(By by, WebDriver driver, WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfElementLocated(by));
        wait.until(ExpectedConditions.elementToBeClickable(by));
        driver.findElement(by).click();
    }

    /**
     * Counts the rows of a data table.
     *
     * @param driver         The executing WebDriver.
     * @param dataTableXPath The XPath of the data table.
     * @return Amount of rows.
     */
    public static Integer countDataTableChildren(WebDriver driver, String dataTableXPath) {
        WebElement dataTable = driver.findElement(By.xpath(dataTableXPath));
        return Integer.parseInt(dataTable.getDomProperty("childElementCount"));
    }

    /**
     * Gets the title of an ad in a certain row of a data table.
     *
     * @param driver      The executing WebDriver.
     * @param wait        The wait of the executing WebDriver.
     * @param index       Index of the wanted row.
     * @param dataTableID ID of the data table.
     * @return The title of the ad in a certain row
     */
    public static String getTitleOfRow(WebDriver driver, WebDriverWait wait, int index, String dataTableID) {
        String xpath = "//*[@id=\"" + dataTableID + "\"]/tbody/tr[" + index + "]/td[2]";
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        return driver.findElement(By.xpath(xpath)).getText();
    }

}
