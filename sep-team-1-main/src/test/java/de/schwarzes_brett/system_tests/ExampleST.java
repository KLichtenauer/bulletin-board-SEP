package de.schwarzes_brett.system_tests;

import de.schwarzes_brett.test_util.system_test.STBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * An example system test to show how to use the test framework.
 *
 * @author Tim-Florian Feulner
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("NewClassNamingConvention")
final class ExampleST extends STBase {

    @Test
    void testPageLoading() {
        getWait().until(ExpectedConditions.presenceOfElementLocated(By.id("headerForm:header_logo")));
    }

}
