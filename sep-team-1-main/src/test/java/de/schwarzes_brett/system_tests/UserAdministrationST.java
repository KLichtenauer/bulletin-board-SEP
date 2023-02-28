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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("NewClassNamingConvention")
public class UserAdministrationST extends STBase {

    private final String usernameStefan = "Stefan1" + getTestSuffix();
    private final String usernameMaria = "Maria3" + getTestSuffix();
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
        driver.manage().window().setSize(new Dimension(1269, 1200));
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
    void searchUser() {
        searchingForUser(usernameStefan);
        String username = driver.findElement(By.xpath("//*[@id=\"userAdministration_form:"
                                                      + "userAdministration_pagination:pagination_dataTable\"]/tbody/tr[1]/td[1]")).getText();
        assertEquals(username, usernameStefan);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(2)
    @Test
    void createUser() {
        STBaseUtil.waitAndClick(By.id("headerForm:header_userAdministrationButton"), driver, wait);
        //Click create new user button.
        STBaseUtil.waitAndClick(By.id("userAdministration_form:createUser"), driver, wait);
        //Inserting user data.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("registration_form:registration_usernameInput")));
        fillingRegistrationFormular();
        STBaseUtil.waitAndClick(By.id("registration_form:registration_submitButton"), driver, wait);
        By messageLocator = By.xpath("//*[@id=\"messages\"]/li");
        wait.until(ExpectedConditions.presenceOfElementLocated(messageLocator));
        String userGotRegisteredMsg = driver.findElement(messageLocator).getText();
        //Searching if user exists.
        searchingForUser(usernameMaria);
        String userNameOfFirstPagination = driver.findElement(By.xpath("//*[@id=\"userAdministration_form:"
                                                                       + "userAdministration_pagination:pagination_dataTable\"]/tbody/tr/td[1]"))
                                                 .getText();
        assertAll(() -> assertEquals("Die Registrierung war erfolgreich.", userGotRegisteredMsg),
                  () -> assertEquals(usernameMaria, userNameOfFirstPagination));
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(3)
    @Test
    void editUserData() {
        searchingForUser(usernameMaria);
        By editButtonLocator = By.id("userAdministration_form:userAdministration_pagination:pagination_dataTable:0:userAdministration_editButton");
        wait.until(ExpectedConditions.presenceOfElementLocated(editButtonLocator));
        driver.findElement(editButtonLocator).click();
        driver.findElement(By.id("profile:street")).clear();
        driver.findElement(By.id("profile:street")).sendKeys("Marienhof");
        driver.findElement(By.id("profile:streetNumber")).clear();
        driver.findElement(By.id("profile:streetNumber")).sendKeys("430");
        driver.findElement(By.id("profile:saveButton")).click();
        //Searching for user.
        searchingForUser(usernameMaria);
        driver.findElement(By.id("userAdministration_form:userAdministration_pagination:pagination_dataTable:0:userAdministration_editButton"))
              .click();

        String streetName = driver.findElement(By.id("profile:street")).getAttribute("value");
        String streetNumber = driver.findElement(By.id("profile:streetNumber")).getAttribute("value");
        assertAll(() -> assertEquals("430", streetNumber),
                  () -> assertEquals("Marienhof", streetName));
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(4)
    @Test
    void deleteUser() {
        //Searching and selecting user.
        searchingForUser(usernameMaria);
        //Choosing user.
        STBaseUtil.waitAndClick(By.id("userAdministration_form:userAdministration_pagination:pagination_dataTable:0:userAdministration_editButton"),
                                driver, wait);
        //Clicking button for deleting profile.
        STBaseUtil.waitAndClick(By.id("deleteProfile_popup_button"), driver, wait);
        STBaseUtil.waitAndClick(By.id("deleteProfile_form:deleteProfile_button"), driver, wait);
        String userDeletedMsg = driver.findElement(By.xpath("//*[@id=\"messages\"]/li")).getText();
        //Searching if user still exists.
        searchingForUser(usernameMaria);
        String usernameOfFirstPaginationResult = driver
                .findElement(By.xpath("//*[@id=\"userAdministration_form:"
                                      + "userAdministration_pagination:pagination_dataTable\"]/tbody/tr[1]/td[1]")).getText();
        assertAll(() -> assertEquals("Das Benutzerprofil wurde erfolgreich gelöscht.", userDeletedMsg),
                  () -> assertEquals("", usernameOfFirstPaginationResult));

    }

    private void fillingRegistrationFormular() {
        driver.findElement(By.id("registration_form:registration_usernameInput")).sendKeys(usernameMaria);
        driver.findElement(By.id("registration_form:registration_firstNameInput")).sendKeys("Maria");
        driver.findElement(By.id("registration_form:registration_lastNameInput")).sendKeys("Kraft");
        driver.findElement(By.id("registration_form:registration_streetInput")).sendKeys("Marienhof");
        driver.findElement(By.id("registration_form:registration_streetNumberInput")).sendKeys("42");
        driver.findElement(By.id("registration_form:registration_postalCodeInput")).sendKeys("94032");
        driver.findElement(By.id("registration_form:registration_cityInput")).sendKeys("Passau");
        driver.findElement(By.id("registration_form:registration_countryInput")).sendKeys("Deutschland");
        driver.findElement(By.id("registration_form:registration_emailInput")).sendKeys("mariakraft@email.com" + getTestSuffix());
        driver.findElement(By.id("registration_form:registration_passwordInput")).click();
        driver.findElement(By.id("registration_form:registration_passwordInput")).sendKeys("7891011");
        driver.findElement(By.id("registration_form:registration_repeatedPasswordInput")).click();
        driver.findElement(By.id("registration_form:registration_repeatedPasswordInput")).sendKeys("7891011");
    }

    private void searchingForUser(String username) {
        STBaseUtil.waitAndClick(By.id("headerForm:header_userAdministrationButton"), driver, wait);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("userAdministration_form:userAdministration_pagination:searchBar_searchInput")));
        driver.findElement(By.id("userAdministration_form:userAdministration_pagination:searchBar_searchInput")).sendKeys(username);
        driver.findElement(By.id("userAdministration_form:userAdministration_pagination:searchBar_searchButton")).click();
    }
}
