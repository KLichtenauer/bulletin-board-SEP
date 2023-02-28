package de.schwarzes_brett.test_util.system_test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * A wrapper for a {@code WebDriver} and its accompanying {@code WebDriverWait}.
 *
 * @param driver     The web driver.
 * @param driverWait The corresponding wait instance.
 * @author Tim-Florian Feulner
 */
record WebInstance(WebDriver driver, WebDriverWait driverWait) {}
