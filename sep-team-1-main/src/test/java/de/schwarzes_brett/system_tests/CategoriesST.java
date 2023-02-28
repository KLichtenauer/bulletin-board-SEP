package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.TestEnvironment;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A system test for blackbox-testing the functionality of the category tree.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SuppressWarnings("NewClassNamingConvention")
public class CategoriesST extends STBase {

    private final String kunstWithSuffix = "Kunst" + getTestSuffix();
    private final String spielzeugeWithSuffix = "Spielzeuge" + getTestSuffix();
    private final String skulpturWithSuffix = "Skulptur" + getTestSuffix();
    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * Initializing the tests by setting the {@code WebDriver} and {@code WebDriverWait} values and logging in as an admin.
     */
    @BeforeEach
    @SuppressWarnings("checkstyle:MagicNumber")
    public void init() {
        driver = getDriver();
        wait = getWait();
        if (TestEnvironment.isLoadTest()) {
            driver.manage().window().setSize(new Dimension(1269, 6000));
        } else {
            driver.manage().window().setSize(new Dimension(1269, 1200));
        }
        STBaseUtil.loginAsAdmin(driver, wait);
    }

    /**
     * Logs out the current user.
     */
    @AfterEach
    public void tearDown() {
        STBaseUtil.logout(driver, wait);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(1)
    @Test
    void createCategory() {
        STBaseUtil.waitAndClick(By.id("headerForm:header_categoryAdministrationButton"), driver, wait);
        STBaseUtil.waitAndClick(By.id("categoryAdministration_form:categoryAdministration_newButton"), driver, wait);
        STBaseUtil.waitAndClick(By.id("editCategory_formular:editCategory_name"), driver, wait);
        driver.findElement(By.id("editCategory_formular:editCategory_name")).sendKeys(spielzeugeWithSuffix);
        driver.findElement(By.id("editCategory_formular:editCategory_saveButton")).click();
        // Checking if category exists.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("categoryAdministration_form:categoryAdministration_newButton")));
        boolean categoryExists = (STBaseUtil.getCategoryElement(driver, wait, spielzeugeWithSuffix) != null);
        assertTrue(categoryExists);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(2)
    @Test
    void faultyCreateCategory() {
        STBaseUtil.waitAndClick(By.id("headerForm:header_categoryAdministrationButton"), driver, wait);
        STBaseUtil.waitAndClick(By.id("categoryAdministration_form:categoryAdministration_newButton"), driver, wait);
        STBaseUtil.waitAndClick(By.id("editCategory_formular:editCategory_saveButton"), driver, wait);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("editCategory_formular:editCategory_saveButton")));
        String errorMsg = driver.findElement(By.xpath("//*[@id=\"editCategory_formular\"]/table/tbody/tr[2]/td[3]")).getText();
        assertEquals("Der Titel darf nicht leer sein.", errorMsg);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(3)
    @Test
    void deleteCategory() {
        STBaseUtil.waitAndClick(By.id("headerForm:header_categoryAdministrationButton"), driver, wait);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("categoryAdministration_form:categoryAdministration_newButton")));
        WebElement foundCategory = STBaseUtil.getCategoryElement(driver, wait, kunstWithSuffix);
        if (foundCategory != null) {
            foundCategory.click();
        }
        STBaseUtil.waitAndClick(By.id("editCategory_formular:deleteCategory"), driver, wait);
        STBaseUtil.waitAndClick(By.id("editCategory_formular:acceptDeletion_form:acceptDeletion"), driver, wait);
        boolean categoryExists = (STBaseUtil.getCategoryElement(driver, wait, kunstWithSuffix) != null);
        STBaseUtil.waitAndClick(By.id("headerForm:header_welcomeButton"), driver, wait);
        STBaseUtil.waitAndClick(By.id("welcome_paginationForm:welcome_pagination:searchBar_searchInput"), driver, wait);
        driver.findElement(By.id("welcome_paginationForm:welcome_pagination:searchBar_searchInput")).sendKeys(skulpturWithSuffix);
        STBaseUtil.waitAndClick(By.id("welcome_paginationForm:welcome_pagination:searchBar_searchButton"), driver, wait);
        String titleOfFirstAd = driver
                .findElement(By.xpath("//*[@id=\"welcome_paginationForm:welcome_pagination:pagination_dataTable\"]/tbody/tr/td[2]"))
                .getText();
        assertAll(() -> assertFalse(categoryExists),
                  () -> assertEquals("", titleOfFirstAd));
    }
}
