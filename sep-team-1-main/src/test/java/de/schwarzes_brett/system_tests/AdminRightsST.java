package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.system_test.STBase;
import org.junit.jupiter.api.AfterEach;
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

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A system test to give and remove a user admin rights.
 *
 * @author Jonas Elsper
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("NewClassNamingConvention")
public final class AdminRightsST extends STBase {

    private static final String DATA_TABLE_ID = "userAdministration_form:userAdministration_pagination:pagination_dataTable";
    private static final String SEARCH_INPUT_ID = "userAdministration_form:userAdministration_pagination:searchBar_searchInput";
    private static final String SEARCH_BUTTON_ID = "userAdministration_form:userAdministration_pagination:searchBar_searchButton";
    private static final String EDIT_USER_BUTTON_ID = "userAdministration_form:userAdministration_pagination:pagination_dataTable:0"
                                                      + ":userAdministration_editButton";
    private static final String ROLE_SELECT_ONE_MENU_ID = "profile:role";
    private static final String SAVE_BUTTON_ID = "profile:saveButton";
    private static final String PROFILE_BUTTON_ID = "headerForm:header_profileButton";
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    @SuppressWarnings("checkstyle:MagicNumber")
    void init() {
        driver = getDriver();
        wait = getWait();
        this.driver.manage().window().setSize(new Dimension(1920, 1080));
        STBaseUtil.login("Kilian2" + getTestSuffix(), "654321", driver, wait);
    }

    @AfterEach
    void reset() {
        STBaseUtil.logout(driver, wait);
    }

    @Test
    @Order(1)
    void giveRights() {
        // Go to userAdministration
        STBaseUtil.navigateTo("view/admin/userAdministration.xhtml", driver);
        // Open profile of Stefan1
        openProfile();
        // Change role from User to Admin and save changes
        changeRole("Administrator");
        // Logout and login in with Stefan1
        STBaseUtil.logout(driver, wait);
        STBaseUtil.login("Stefan1" + getTestSuffix(), "123456", driver, wait);
        // check on profile page if he has admin role
        driver.findElement(By.id(PROFILE_BUTTON_ID)).click();
        WebElement roleDropdown = driver.findElement(By.id(ROLE_SELECT_ONE_MENU_ID));
        wait.until(ExpectedConditions.elementToBeClickable(roleDropdown));
        String actual = roleDropdown.findElement(By.xpath("//option[. = 'Administrator']")).getAttribute("selected");
        assertTrue(Boolean.parseBoolean(actual));
    }

    @Test
    @Order(2)
    void removeRights() {
        // Go to userAdministration
        STBaseUtil.navigateTo("view/admin/userAdministration.xhtml", driver);
        // Open profile of Stefan1
        openProfile();
        // Change role from User to Admin and save changes
        String user = "Benutzer";
        changeRole(user);
        // check on profile page if he has user role
        STBaseUtil.navigateTo("view/admin/userAdministration.xhtml", driver);
        openProfile();
        WebElement roleDropdown = driver.findElement(By.id(ROLE_SELECT_ONE_MENU_ID));
        wait.until(ExpectedConditions.elementToBeClickable(roleDropdown));
        String actual = roleDropdown.findElement(By.xpath("//*[@id=\"profile:role\"]/option[1]")).getAttribute("selected");
        assertTrue(Boolean.parseBoolean(actual));
    }

    private void changeRole(String role) {
        WebElement roleDropdown = driver.findElement(By.id(ROLE_SELECT_ONE_MENU_ID));
        wait.until(ExpectedConditions.elementToBeClickable(roleDropdown));
        roleDropdown.click();
        roleDropdown.findElement(By.xpath("//option[. = '" + role + "']")).click();
        driver.findElement(By.id(SAVE_BUTTON_ID)).click();
    }

    private void openProfile() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id(SEARCH_INPUT_ID)));
        driver.findElement(By.id(SEARCH_INPUT_ID)).sendKeys("Stefan1" + getTestSuffix());
        driver.findElement(By.id(SEARCH_BUTTON_ID)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(DATA_TABLE_ID)));
        driver.findElement(By.id(EDIT_USER_BUTTON_ID)).click();
    }
}
