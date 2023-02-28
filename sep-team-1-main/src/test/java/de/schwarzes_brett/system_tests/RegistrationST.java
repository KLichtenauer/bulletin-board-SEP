package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.system_test.STBase;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author michaelgruner
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("NewClassNamingConvention")
public class RegistrationST extends STBase {

    private static final String REGISTER_BUTTON_ID = "headerForm:header_registerButton";
    private static final String USERNAME_INPUT_ID = "registration_form:registration_usernameInput";
    private static final String FIRST_NAME_INPUT_ID = "registration_form:registration_firstNameInput";
    private static final String LAST_NAME_INPUT_ID = "registration_form:registration_lastNameInput";
    private static final String STREET_INPUT_ID = "registration_form:registration_streetInput";
    private static final String STREET_NUMBER_INPUT_ID = "registration_form:registration_streetNumberInput";
    private static final String POSTAL_CODE_INPUT_ID = "registration_form:registration_postalCodeInput";
    private static final String CITY_INPUT_ID = "registration_form:registration_cityInput";
    private static final String COUNTRY_INPUT_ID = "registration_form:registration_countryInput";
    private static final String EMAIL_INPUT_ID = "registration_form:registration_emailInput";
    private static final String PASSWORD_INPUT_ID = "registration_form:registration_passwordInput";
    private static final String REPEATED_PASSWORD_INPUT_ID = "registration_form:registration_repeatedPasswordInput";
    private static final String SUBMIT_BUTTON_ID = "registration_form:registration_submitButton";
    private static final String GLOBAL_MESSAGE_ID = "messages";
    private static final String EMAIL_MESSAGE_ID = "registration_form:registration_emailMessage";
    private static final String USERNAME_MESSAGE_ID = "registration_form:registration_usernameMessage";
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
    void testInvalidRegistration() {
        wait.until(ExpectedConditions.elementToBeClickable(By.id(REGISTER_BUTTON_ID)));
        driver.findElement(By.id(REGISTER_BUTTON_ID)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id(USERNAME_INPUT_ID)));
        fillBadInput();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(EMAIL_MESSAGE_ID)));
        final String messageUsername = driver.findElement(By.id(USERNAME_MESSAGE_ID)).getText();
        final String messageEmail = driver.findElement(By.id(EMAIL_MESSAGE_ID)).getText();
        String expectedMessageUsername = "Der Benutzername existiert schon.";
        String expectedMessageEmail = "E-Mail wird bereits verwendet.";

        assertAll(() -> assertEquals(expectedMessageUsername, messageUsername),
                  () -> assertEquals(expectedMessageEmail, messageEmail)
        );
    }

    @Test
    void testValidRegistrationWithValidation() throws MessagingException, IOException {
        wait.until(ExpectedConditions.elementToBeClickable(By.id(REGISTER_BUTTON_ID)));
        driver.findElement(By.id(REGISTER_BUTTON_ID)).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id(USERNAME_INPUT_ID)));
        fillValidInput();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id(GLOBAL_MESSAGE_ID)));
        final String globalMessage = driver.findElement(By.id(GLOBAL_MESSAGE_ID)).getText();
        String expectedGlobalMessage = "Die Registrierung war erfolgreich.";

        assertEquals(expectedGlobalMessage, globalMessage);

        String verificationLink = getVerificationLink("ChristianGross@email.com" + getTestSuffix());

        driver.get(verificationLink);

        STBaseUtil.login("Christian5" + getTestSuffix(), "hiIchBinChris", driver, wait);

        String title = driver.getTitle();
        String expectedTitle = "Willkommensseite";
        assertEquals(expectedTitle, title);
    }

    private void fillBadInput() {
        driver.findElement(By.id(USERNAME_INPUT_ID)).click();
        driver.findElement(By.id(USERNAME_INPUT_ID)).sendKeys("Stefan1" + getTestSuffix());
        driver.findElement(By.id(FIRST_NAME_INPUT_ID)).click();
        driver.findElement(By.id(FIRST_NAME_INPUT_ID)).sendKeys("Stefan");
        driver.findElement(By.id(LAST_NAME_INPUT_ID)).click();
        driver.findElement(By.id(LAST_NAME_INPUT_ID)).sendKeys("Maier");
        driver.findElement(By.id(STREET_INPUT_ID)).click();
        driver.findElement(By.id(STREET_INPUT_ID)).sendKeys("Stefansweg");
        driver.findElement(By.id(STREET_NUMBER_INPUT_ID)).click();
        driver.findElement(By.id(STREET_NUMBER_INPUT_ID)).sendKeys("1");
        driver.findElement(By.id(POSTAL_CODE_INPUT_ID)).click();
        driver.findElement(By.id(POSTAL_CODE_INPUT_ID)).sendKeys("94032");
        driver.findElement(By.id(CITY_INPUT_ID)).click();
        driver.findElement(By.id(CITY_INPUT_ID)).sendKeys("Passau");
        driver.findElement(By.id(COUNTRY_INPUT_ID)).click();
        driver.findElement(By.id(COUNTRY_INPUT_ID)).sendKeys("Deutschland");
        driver.findElement(By.id(EMAIL_INPUT_ID)).click();
        driver.findElement(By.id(EMAIL_INPUT_ID)).sendKeys("stefanmaier1@email.com" + getTestSuffix());
        driver.findElement(By.id(PASSWORD_INPUT_ID)).click();
        driver.findElement(By.id(PASSWORD_INPUT_ID)).sendKeys("123456789");
        driver.findElement(By.id(REPEATED_PASSWORD_INPUT_ID)).click();
        driver.findElement(By.id(REPEATED_PASSWORD_INPUT_ID)).sendKeys("123456789");
        driver.findElement(By.id(SUBMIT_BUTTON_ID)).click();
    }

    private void fillValidInput() {
        driver.findElement(By.id(USERNAME_INPUT_ID)).click();
        driver.findElement(By.id(USERNAME_INPUT_ID)).sendKeys("Christian5" + getTestSuffix());
        driver.findElement(By.id(FIRST_NAME_INPUT_ID)).click();
        driver.findElement(By.id(FIRST_NAME_INPUT_ID)).sendKeys("Christian");
        driver.findElement(By.id(LAST_NAME_INPUT_ID)).click();
        driver.findElement(By.id(LAST_NAME_INPUT_ID)).sendKeys("Groß");
        driver.findElement(By.id(STREET_INPUT_ID)).click();
        driver.findElement(By.id(STREET_INPUT_ID)).sendKeys("Lindental");
        driver.findElement(By.id(STREET_NUMBER_INPUT_ID)).click();
        driver.findElement(By.id(STREET_NUMBER_INPUT_ID)).sendKeys("1");
        driver.findElement(By.id(POSTAL_CODE_INPUT_ID)).click();
        driver.findElement(By.id(POSTAL_CODE_INPUT_ID)).sendKeys("94032");
        driver.findElement(By.id(CITY_INPUT_ID)).click();
        driver.findElement(By.id(CITY_INPUT_ID)).sendKeys("Passau");
        driver.findElement(By.id(COUNTRY_INPUT_ID)).click();
        driver.findElement(By.id(COUNTRY_INPUT_ID)).sendKeys("Deutschland");
        driver.findElement(By.id(EMAIL_INPUT_ID)).click();
        driver.findElement(By.id(EMAIL_INPUT_ID)).sendKeys("ChristianGross@email.com" + getTestSuffix());
        driver.findElement(By.id(PASSWORD_INPUT_ID)).click();
        driver.findElement(By.id(PASSWORD_INPUT_ID)).sendKeys("hiIchBinChris");
        driver.findElement(By.id(REPEATED_PASSWORD_INPUT_ID)).click();
        driver.findElement(By.id(REPEATED_PASSWORD_INPUT_ID)).sendKeys("hiIchBinChris");
        driver.findElement(By.id(SUBMIT_BUTTON_ID)).click();
    }

    @SuppressWarnings("checkstyle:MagicNumber")
    private String getVerificationLink(String mailAddress) throws MessagingException, IOException {
        Optional<MimeMessage> received = getMail().getReceivedMessages(mailAddress).stream().findFirst();
        assertTrue(received.isPresent());
        return received.get().getContent().toString().split("\n")[3].trim();
    }
}
