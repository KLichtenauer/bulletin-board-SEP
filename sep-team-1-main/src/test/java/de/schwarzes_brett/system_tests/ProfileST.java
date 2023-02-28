package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.system_test.STBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Testing profile specific functionality.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileST extends STBase {

    private static final String OLD_PASSWORD_OF_STEFAN = "123456";
    private static final String NEW_PASSWORD_OF_STEFAN = "KLWT2iR+";
    private static final String PASSWORD_OF_LISA = "abcdefg";
    private static final String HEADER_PROFILE_BTN = "headerForm:header_profileButton";
    private static final String PROFILE_STREET_NUMBER = "profile:streetNumber";
    private static final String PROFILE_SAVE_BTN = "profile:saveButton";
    private static final String HEADER_LOGOUT_BTN = "headerForm:header_logoutButton";
    private static final String HEADER_LOGIN_BTN = "headerForm:header_loginButton";
    private static final String LOGIN_USERNAME = "login:username";
    private static final String LOGIN_PASSWORD = "login:password";
    private static final String LOGIN_SUBMIT_BTN = "login:loginButton";
    private final String usernameStefan = "Stefan1" + getTestSuffix();
    private final String usernameLisa = "Lisa4" + getTestSuffix();
    private WebDriver driver;
    private WebDriverWait wait;
    private String currentPasswordOfStefan = OLD_PASSWORD_OF_STEFAN;

    /**
     * Initializing the tests by setting the {@code WebDriver} and {@code WebDriverWait} values and logging in as an admin.
     * Additionally, the tests sets the {@code currentPasswordOfStefan} depending on the tag of the test because when running the whole class the
     * password of Stefan changes.
     *
     * @param testInfo Used for checking if the currently running test has any tags.
     */
    @SuppressWarnings("checkstyle:MagicNumber")
    @BeforeEach
    public void init(TestInfo testInfo) {
        driver = getDriver();
        wait = getWait();
        driver.manage().window().setSize(new Dimension(1269, 1200));
        currentPasswordOfStefan = testInfo.getTags().contains("changedPassword") ? NEW_PASSWORD_OF_STEFAN : OLD_PASSWORD_OF_STEFAN;
        STBaseUtil.login(usernameStefan, currentPasswordOfStefan, driver, wait);
    }

    /**
     * Logs out the current user depending on the tag of the test. In tests with tag "noLogout" no user is logged in, thus no logout.
     *
     * @param testInfo Used for checking if the currently running test has any tags.
     */
    @AfterEach
    public void logout(TestInfo testInfo) {
        if (testInfo.getTags().contains("noLogout")) {
            return;
        }
        STBaseUtil.logout(driver, wait);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(1)
    @Test
    void editProfile() {
        STBaseUtil.waitAndClick(By.id(HEADER_PROFILE_BTN), driver, wait);
        STBaseUtil.waitAndClick(By.id(PROFILE_STREET_NUMBER), driver, wait);
        driver.findElement(By.id(PROFILE_STREET_NUMBER)).clear();
        driver.findElement(By.id(PROFILE_STREET_NUMBER)).sendKeys("6");
        STBaseUtil.waitAndClick(By.id(PROFILE_SAVE_BTN), driver, wait);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("messages")));
        String updateMessage = driver.findElement(By.id("messages")).getText();
        STBaseUtil.waitAndClick(By.id(HEADER_PROFILE_BTN), driver, wait);
        String streetNumber = driver.findElement(By.id(PROFILE_STREET_NUMBER)).getAttribute("value");
        assertAll(() -> assertEquals(updateMessage, "Profilinformationen gespeichert."),
                  () -> assertEquals("6", streetNumber));

    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(2)
    @Test
    void changePasswordFailed() {
        STBaseUtil.waitAndClick(By.id(HEADER_PROFILE_BTN), driver, wait);
        STBaseUtil.waitAndClick(By.id("passwordChange_form:passwordChange"), driver, wait);
        STBaseUtil.waitAndClick(By.id("setPasswordForm:inputPwd"), driver, wait);
        driver.findElement(By.id("setPasswordForm:inputPwd")).sendKeys("123");
        driver.findElement(By.id("setPasswordForm:inputPwdRepeat")).sendKeys("123");
        STBaseUtil.waitAndClick(By.id("setPasswordForm:submitBtn"), driver, wait);
        String passwordErrorMsg = driver.findElement(By.id("setPasswordForm:msgPwd")).getText();
        assertEquals("Das Passwort muss mindestens 6 Zeichen lang sein.", passwordErrorMsg);
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(3)
    @Test
    void editProfileFailed() {
        STBaseUtil.waitAndClick(By.id(HEADER_PROFILE_BTN), driver, wait);
        STBaseUtil.waitAndClick(By.id("profile:city"), driver, wait);
        driver.findElement(By.id("profile:city")).clear();
        STBaseUtil.waitAndClick(By.id("profile:postalCode"), driver, wait);
        driver.findElement(By.id("profile:postalCode")).clear();
        STBaseUtil.waitAndClick(By.id(PROFILE_SAVE_BTN), driver, wait);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(PROFILE_SAVE_BTN)));
        String updatedCityErrorMsg = driver
                .findElement(By.xpath("//*[@id=\"profile\"]/table/tbody/tr[8]/td[3]/span")).getText();
        String updatedPostalCodeErrorMsg = driver
                .findElement(By.xpath("//*[@id=\"profile\"]/table/tbody/tr[7]/td[3]/span")).getText();
        assertAll(() -> assertEquals("Es wurde kein Ort angegeben.", updatedCityErrorMsg),
                  () -> assertEquals("Es wurde keine Postleitzahl angegeben.", updatedPostalCodeErrorMsg));

    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(4)
    @Test
    void changePassword() {
        STBaseUtil.waitAndClick(By.id(HEADER_PROFILE_BTN), driver, wait);
        STBaseUtil.waitAndClick(By.id("passwordChange_form:passwordChange"), driver, wait);
        STBaseUtil.waitAndClick(By.id("setPasswordForm:inputPwd"), driver, wait);
        driver.findElement(By.id("setPasswordForm:inputPwd")).sendKeys(NEW_PASSWORD_OF_STEFAN);
        driver.findElement(By.id("setPasswordForm:inputPwdRepeat")).sendKeys(NEW_PASSWORD_OF_STEFAN);
        currentPasswordOfStefan = NEW_PASSWORD_OF_STEFAN;
        STBaseUtil.waitAndClick(By.id("setPasswordForm:submitBtn"), driver, wait);

        //Checking for profile faces msg.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("messages")));
        List<WebElement> profileFacesMessages = driver.findElements(By.xpath("//*[@id=\"messages\"]"));
        boolean passwordGotChangedMsg = !profileFacesMessages.isEmpty() && profileFacesMessages
                .get(0).getText().equals("Das Password wurde erfolgreich geändert.");
        STBaseUtil.waitAndClick(By.id(HEADER_LOGOUT_BTN), driver, wait);
        STBaseUtil.waitAndClick(By.id(HEADER_LOGIN_BTN), driver, wait);

        //Checking if old password still works.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(LOGIN_SUBMIT_BTN)));
        driver.findElement(By.id(LOGIN_USERNAME)).click();
        driver.findElement(By.id(LOGIN_USERNAME)).sendKeys(usernameStefan);
        driver.findElement(By.id(LOGIN_PASSWORD)).click();
        driver.findElement(By.id(LOGIN_PASSWORD)).sendKeys(OLD_PASSWORD_OF_STEFAN);
        driver.findElement(By.id(LOGIN_SUBMIT_BTN)).click();
        List<WebElement> loginFacesMessages = driver.findElements(By.xpath("//*[@id=\"messages\"]"));
        boolean errorMessageLoginGetsShown = !loginFacesMessages.isEmpty() && loginFacesMessages
                .get(0).getText().equals("Der Login ist fehlgeschlagen. Falsche Zugangsdaten.");

        //Checking if new password works.
        driver.findElement(By.id(LOGIN_PASSWORD)).sendKeys(NEW_PASSWORD_OF_STEFAN);
        STBaseUtil.waitAndClick(By.id(LOGIN_SUBMIT_BTN), driver, wait);
        WebElement logoutElement = driver.findElement(By.id(HEADER_LOGOUT_BTN));

        assertAll(() -> assertTrue(passwordGotChangedMsg),
                  () -> assertTrue(errorMessageLoginGetsShown),
                  () -> assertNotNull(logoutElement));
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    @Order(5)
    @Test
    @Tag("noLogout")
    @Tag("changedPassword")
    void deleteProfile() {
        //Subscribing to an ad as Lisa4.
        STBaseUtil.logout(driver, wait);
        STBaseUtil.login(usernameLisa, PASSWORD_OF_LISA, driver, wait);
        STBaseUtil.followOrUnfollowAd(Integer.parseInt("100" + getTestSuffix()), driver, wait);
        STBaseUtil.logout(driver, wait);
        STBaseUtil.login(usernameStefan, currentPasswordOfStefan, driver, wait);
        //Deleting profile of Stefan1.
        STBaseUtil.waitAndClick(By.id(HEADER_PROFILE_BTN), driver, wait);
        STBaseUtil.waitAndClick(By.id("deleteProfile_popup_button"), driver, wait);
        STBaseUtil.waitAndClick(By.id("deleteProfile_form:deleteProfile_button"), driver, wait);
        STBaseUtil.waitAndClick(By.id(HEADER_LOGIN_BTN), driver, wait);
        STBaseUtil.waitAndClick(By.id(LOGIN_USERNAME), driver, wait);
        driver.findElement(By.id(LOGIN_USERNAME)).sendKeys(usernameStefan);
        driver.findElement(By.id(LOGIN_PASSWORD)).sendKeys(currentPasswordOfStefan);
        STBaseUtil.waitAndClick(By.id(LOGIN_SUBMIT_BTN), driver, wait);
        List<WebElement> loginFacesMessages = driver.findElements(By.xpath("//*[@id=\"messages\"]"));
        boolean errorMessageFound = !loginFacesMessages.isEmpty() && loginFacesMessages
                .get(0).getText().equals("Der Login ist fehlgeschlagen. Falsche Zugangsdaten.");
        //Lisa4 checking subscribed ads.
        STBaseUtil.login(usernameLisa, PASSWORD_OF_LISA, driver, wait);
        STBaseUtil.waitAndClick(By.id("welcome_subscribedForm:welcome_subscribedButton"), driver, wait);
        String titleOfFirstAd = driver
                .findElement(By.xpath("//*[@id=\"welcome_paginationForm:welcome_pagination:pagination_dataTable\"]/tbody/tr/td[2]"))
                .getText();
        assertTrue(errorMessageFound);
        assertEquals("", titleOfFirstAd);
    }
}
