package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.system_test.STBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Daniel Lipp
 */
@SuppressWarnings({"checkstyle:JavadocMethod", "checkstyle:MagicNumber"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public final class SecurityST extends STBase {

    private static final String EDIT_AD_BTN = "adManagement_paginationForm:pagination:pagination_dataTable:0:editAd";
    private static final String ID_TITLE = "editAdForm:tile";
    private static final String ID_DESCRIPTION = "editAdForm:description";
    private static final String EDIT_AD_SAVE_BUTTON_ID = "editAdForm:save";
    private static final String AD_PAGE_TITLE_ID = "ad_title";
    private static final String HEADER_MANAGEMENT_BTN_ID = "headerForm:header_adManagementButton";
    private static final String AD_PAGE_DESCRIPTION_ID = "ad_description";
    private WebDriver driver;
    private WebDriverWait wait;

    private void check404Site() {
        assertEquals("Dieser Inhalt existiert nicht", driver.findElement(By.id("errorType")).getText());
    }

    @BeforeEach
    void init() {
        driver = getDriver();
        wait = getWait();
    }

    @Test
    @Order(1)
    void testAnonymAdminSite() {
        STBaseUtil.navigateTo("view/user/editAd.xhtml", driver);
        check404Site();
    }

    @Test
    @Order(2)
    void testAnonymUserSite() {
        STBaseUtil.navigateTo("view/admin/adManagement.xhtml", driver);
        check404Site();
    }

    @Test
    @Order(3)
    void testUserAdminSite() {
        STBaseUtil.login("Stefan1" + getTestSuffix(), "123456", driver, wait);
        STBaseUtil.navigateTo("view/admin/adManagement.xhtml", driver);
        check404Site();
    }

    @Test
    @Order(4)
    void testJavaScriptInjection() throws InterruptedException {
        String expected = "ich habe meinen schadcode eingeschleust";
        STBaseUtil.waitAndClick(By.id(HEADER_MANAGEMENT_BTN_ID), driver, wait);
        STBaseUtil.waitAndClick(By.id(EDIT_AD_BTN), driver, wait);
        String command = String.format("<script>document.getElementById(\"%s\").innerHTML = \"%s\"; </script>", ID_TITLE, expected);
        driver.findElement(By.id(ID_DESCRIPTION)).sendKeys(command);
        ((JavascriptExecutor) driver).executeScript("window.scrollBy(0,500)", "");
        Thread.sleep(1000);
        STBaseUtil.waitAndClick(By.id(EDIT_AD_SAVE_BUTTON_ID), driver, wait);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(AD_PAGE_TITLE_ID)));
        assertNotEquals(expected, driver.findElement(By.id(AD_PAGE_TITLE_ID)).getText());
        assertTrue(driver.findElement(By.id(AD_PAGE_DESCRIPTION_ID)).getText().contains("<script>"));
    }
}
