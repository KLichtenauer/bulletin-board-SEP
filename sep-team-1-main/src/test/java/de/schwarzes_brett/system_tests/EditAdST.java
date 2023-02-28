package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.TestEnvironment;
import de.schwarzes_brett.test_util.system_test.DualSTBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Daniel Lipp
 */
@SuppressWarnings({"checkstyle:JavadocMethod", "checkstyle:MagicNumber"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class EditAdST extends DualSTBase {
    private static final String EDIT_AD_FORM_RELEASE_DATE_INPUT = "editAdForm:releaseDate_input";
    private static final String EDIT_AD_FORM_END_DATE_INPUT = "editAdForm:endDate_input";
    private static final String EDIT_AD_FORM_RELEASE_LABEL = "editAdForm:releaseLabel";
    private static final String EDIT_AD_FORM_SAVE = "editAdForm:save";
    private static final String EDIT_AD_FORM_DELETE = "delete";
    private static final String HEADER_AD_MANAGEMENT_BUTTON = "headerForm:header_adManagementButton";
    private static final String NEW_AD_BUTTON = "adManagement_createForm:adManagement_newButton";
    private static final String EDIT_AD_FORM_TITLE = "editAdForm:title";
    private static final String EDIT_AD_FORM_DESCRIPTION = "editAdForm:description";
    private static final String AD_TITLE = "ad_title";
    private static final String AD_DESCRIPTION = "ad_description";
    private static final String AD_USERNAME = "ad_username";
    private static final String AD_END_DATE = "ad_endDate";
    private static final String PAGINATION_EDIT_AD_BTN_ID = "adManagement_paginationForm:pagination:pagination_dataTable:0:editAd";
    private static final String EDIT_AD_FORM = "editAdForm";
    private static final String EDIT_AD_FORM_CURRENCY = "editAdForm:currency";
    private static final String EDIT_AD_FORM_PRICE = "editAdForm:price";
    private final String username = "Stefan1" + getTestSuffix();
    private final String username2 = "Lisa4" + getTestSuffix();
    private WebDriver driver1;
    private WebDriverWait wait1;
    private JavascriptExecutor jsExecutor1;
    private WebDriver driver2;
    private WebDriverWait wait2;
    private JavascriptExecutor jsExecutor2;
    private String url;

    private void insertPrice(String value) {
        driver1.findElement(By.id(EDIT_AD_FORM_PRICE)).sendKeys(value);
        STBaseUtil.waitAndClick(By.id(EDIT_AD_FORM_CURRENCY), driver1, wait1);
        WebElement dropdown = driver1.findElement(By.id(EDIT_AD_FORM_CURRENCY));
        dropdown.findElement(By.xpath("//option[. = 'EUR']")).click();
        driver1.findElement(By.cssSelector("option:nth-child(44)")).click();
        STBaseUtil.waitAndClick(By.id(EDIT_AD_FORM), driver1, wait1);
    }

    @BeforeEach
    void init() {
        driver1 = getDriver(1);
        wait1 = getWait(1);
        jsExecutor1 = (JavascriptExecutor) driver1;
        driver2 = getDriver(0);
        wait2 = getWait(0);
        jsExecutor2 = (JavascriptExecutor) driver2;
    }

    @Test
    @Order(1)
    void createAd() throws InterruptedException {
        if (TestEnvironment.isLoadTest()) {
            driver1.manage().window().setSize(new Dimension(1920, 4000));
        } else {
            driver1.manage().window().setSize(new Dimension(1920, 1080));
        }
        STBaseUtil.login(username, "123456", driver1, wait1);

        String title = "Gemälde eines Sommerabends in Passau";
        String description = "Ein sehr schönes Gemälde von mir gemalt!";
        STBaseUtil.waitAndClick(By.id(HEADER_AD_MANAGEMENT_BUTTON), driver1, wait1);
        STBaseUtil.waitAndClick(By.id(NEW_AD_BUTTON), driver1, wait1);
        STBaseUtil.getCategoryElement(driver1, wait1, "Kunst" + getTestSuffix()).click();

        driver1.findElement(By.id(EDIT_AD_FORM_TITLE)).sendKeys(title);
        driver1.findElement(By.id(EDIT_AD_FORM_DESCRIPTION)).sendKeys(description);
        insertPrice("40");
        enterDate();

        submitAd();
        wait1.until(ExpectedConditions.presenceOfElementLocated(By.id(AD_TITLE)));
        assertEquals(title, driver1.findElement(By.id(AD_TITLE)).getText());
        assertEquals(description, driver1.findElement(By.id(AD_DESCRIPTION)).getText());
        assertEquals(username, driver1.findElement(By.id(AD_USERNAME)).getText());

        // store url of the ad for last test
        url = driver1.getCurrentUrl();
    }

    @Test
    @Order(2)
    void invalidAd() throws InterruptedException {
        STBaseUtil.waitAndClick(By.id(HEADER_AD_MANAGEMENT_BUTTON), driver1, wait1);
        STBaseUtil.waitAndClick(By.id(NEW_AD_BUTTON), driver1, wait1);
        STBaseUtil.getCategoryElement(driver1, wait1, "Kunst" + getTestSuffix()).click();
        insertPrice("90");
        enterDate();
        submitAd();
        assertEquals("Der Titel darf nicht leer sein.", driver1.findElement(By.cssSelector(".ui-message-error")).getText());
    }

    @Test
    @Order(3)
    void editAd() throws InterruptedException {
        String description = "Ein sehr schönes Gemälde";
        STBaseUtil.waitAndClick(By.id(HEADER_AD_MANAGEMENT_BUTTON), driver1, wait1);
        STBaseUtil.waitAndClick(By.name(PAGINATION_EDIT_AD_BTN_ID), driver1, wait1);
        replaceText(driver1.findElement(By.id(EDIT_AD_FORM_DESCRIPTION)), description);
        submitAd();
        assertEquals(description, driver1.findElement(By.id(AD_DESCRIPTION)).getText());
    }

    @Test
    @Order(4)
    void endAd() throws InterruptedException {
        STBaseUtil.waitAndClick(By.id(HEADER_AD_MANAGEMENT_BUTTON), driver1, wait1);
        STBaseUtil.waitAndClick(By.name(PAGINATION_EDIT_AD_BTN_ID), driver1, wait1);
        enterDate(EDIT_AD_FORM_END_DATE_INPUT, "2000-01-01 00:00:01");
        submitAd();
        String endDateText = driver1.findElement(By.id(AD_END_DATE)).getText();

        assertNotNull(endDateText);
        assertNotEquals("", endDateText);
    }

    @Test
    @Order(5)
    void testRaceCondition() throws InterruptedException {
        STBaseUtil.waitAndClick(By.id(HEADER_AD_MANAGEMENT_BUTTON), driver1, wait1);
        STBaseUtil.waitAndClick(By.id("adManagement_paginationForm:pagination:pagination_expiredAdsSelect"), driver1, wait1);
        STBaseUtil.waitAndClick(By.id("adManagement_paginationForm:pagination:searchBar_searchButton"), driver1, wait1);
        STBaseUtil.waitAndClick(By.name(PAGINATION_EDIT_AD_BTN_ID), driver1, wait1);

        driver2.manage().window().setSize(new Dimension(1920, 1080));
        STBaseUtil.login(username2, "abcdefg", driver2, wait2);
        driver2.get(url);
        scroll(600, 1000, jsExecutor2);
        STBaseUtil.waitAndClick(By.id("ad_messageForm:ad_messageButton"), driver2, wait2);
        driver2.findElement(By.id("message_form:message_content")).sendKeys("Passau ist schön.");

        deleteAd();
        STBaseUtil.waitAndClick(By.id("message_form:message_sendButton"), driver2, wait2);
        assertEquals("Es ist ein Fehler aufgetreten.", driver2.findElement(By.id("errorType")).getText());
        STBaseUtil.logout(driver1, wait1);
        STBaseUtil.logout(driver2, wait2);
    }


    private void enterDate() {
        enterDate(EDIT_AD_FORM_RELEASE_DATE_INPUT, "2000-01-01 00:00:00");
    }

    private void enterDate(String id, String value) {
        replaceText(driver1.findElement(By.id(id)), value);
        STBaseUtil.waitAndClick(By.id(EDIT_AD_FORM_RELEASE_LABEL), driver1, wait1);
    }

    private void submitAd() throws InterruptedException {
        scroll(1000);
        STBaseUtil.waitAndClick(By.id(EDIT_AD_FORM_SAVE), driver1, wait1);
    }

    private void deleteAd() {
        wait1.until(ExpectedConditions.presenceOfElementLocated(By.id(EDIT_AD_FORM_SAVE)));
        STBaseUtil.waitAndClick(By.id(EDIT_AD_FORM_DELETE), driver1, wait1);
        STBaseUtil.waitAndClick(By.id("confirmDelete:confirm"), driver1, wait1);
    }

    private void scroll(int millis) throws InterruptedException {
        scroll(500, millis, jsExecutor1);
    }

    private void scroll(int y, int millis, JavascriptExecutor js) throws InterruptedException {
        js.executeScript("window.scrollBy(0," + y + ")", "");
        if (millis > 0) {
            Thread.sleep(millis);
        }
    }

    private void replaceText(WebElement element, String newText) {
        JavascriptExecutor js = (JavascriptExecutor) driver1;
        js.executeScript("arguments[0].value = '" + newText + "';", element);
    }

}

