package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.TestEnvironment;
import de.schwarzes_brett.test_util.system_test.STBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author michaelgruner
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("NewClassNamingConvention")
public class SearchST extends STBase {

    private static final String DATA_TABLE_ID = "landing_form:landing_pagination:pagination_dataTable";
    private static final String SEARCH_INPUT_ID = "landing_form:landing_pagination:searchBar_searchInput";
    private static final String SEARCH_BUTTON_ID = "landing_form:landing_pagination:searchBar_searchButton";
    private static final String DATA_TABLE_CONTENT_X_PATH = "//*[@id=\"landing_form:landing_pagination:pagination_dataTable\"]/tbody";
    private static final String EXPIRED_ADS_SELECT_ID = "landing_form:landing_pagination:pagination_expiredAdsSelect";
    private static final String LOGO_ID = "headerForm:header_logo";
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

    @Test
    void testSearchAdTitle() {
        driver.findElement(By.id(LOGO_ID)).click();
        driver.findElement(By.id(SEARCH_INPUT_ID)).click();
        driver.findElement(By.id(SEARCH_INPUT_ID)).sendKeys("Schrank" + getTestSuffix() + " ");
        driver.findElement(By.id(SEARCH_BUTTON_ID)).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(DATA_TABLE_ID)));

        final String expectedTitle = "großer benutzter Schrank" + getTestSuffix() + " mit Türen";

        assertAll(
                () -> assertEquals(1, STBaseUtil.countDataTableChildren(driver, DATA_TABLE_CONTENT_X_PATH)),
                () -> assertEquals(expectedTitle, STBaseUtil.getTitleOfRow(driver, wait, 1, DATA_TABLE_ID))
        );

    }

    @Test
    void testSearchAdTitleWithExpired() {
        driver.findElement(By.id(LOGO_ID)).click();
        driver.findElement(By.id(SEARCH_INPUT_ID)).click();
        driver.findElement(By.id(SEARCH_INPUT_ID)).sendKeys("Schrank" + getTestSuffix() + " ");
        driver.findElement(By.id(EXPIRED_ADS_SELECT_ID)).click();
        driver.findElement(By.id(SEARCH_BUTTON_ID)).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(DATA_TABLE_ID)));

        final String expectedTitle1 = "großer benutzter Schrank" + getTestSuffix() + " mit Türen";
        final String expectedTitle2 = "kleiner schöner Schrank" + getTestSuffix() + " mit Fächer";

        assertAll(
                () -> assertEquals(2, STBaseUtil.countDataTableChildren(driver, DATA_TABLE_CONTENT_X_PATH)),
                () -> assertEquals(expectedTitle1, STBaseUtil.getTitleOfRow(driver, wait, 1, DATA_TABLE_ID)),
                () -> assertEquals(expectedTitle2, STBaseUtil.getTitleOfRow(driver, wait, 2, DATA_TABLE_ID))
        );
    }

    @Test
    void testSearchAdDescriptionWithExpired() {
        driver.findElement(By.id(LOGO_ID)).click();
        driver.findElement(By.id(SEARCH_INPUT_ID)).click();
        driver.findElement(By.id(SEARCH_INPUT_ID)).sendKeys("Holz" + getTestSuffix() + "!");
        driver.findElement(By.id(EXPIRED_ADS_SELECT_ID)).click();
        driver.findElement(By.id(SEARCH_BUTTON_ID)).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(DATA_TABLE_ID)));

        final String expectedTitle = "großer benutzter Schrank" + getTestSuffix() + " mit Türen";

        assertAll(
                () -> assertEquals(1, STBaseUtil.countDataTableChildren(driver, DATA_TABLE_CONTENT_X_PATH)),
                () -> assertEquals(expectedTitle, STBaseUtil.getTitleOfRow(driver, wait, 1, DATA_TABLE_ID))
        );
    }

    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    void testCategorySearch() {
        if (TestEnvironment.isLoadTest()) {
            // Make browser window bigger to make category clickable without scrolling.
            driver.manage().window().setSize(new Dimension(1920, 3000));
        }

        driver.findElement(By.id(LOGO_ID)).click();
        WebElement category = STBaseUtil.getCategoryElement(driver, wait, "Kunst" + getTestSuffix());
        assertNotNull(category);

        category.click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(DATA_TABLE_ID)));

        assertAll(
                () -> assertEquals(1, STBaseUtil.countDataTableChildren(driver, DATA_TABLE_CONTENT_X_PATH)),
                () -> assertEquals("Skulptur", STBaseUtil.getTitleOfRow(driver, wait, 1, DATA_TABLE_ID))
        );
    }
}
