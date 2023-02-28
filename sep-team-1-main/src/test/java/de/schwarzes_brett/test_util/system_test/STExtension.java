package de.schwarzes_brett.test_util.system_test;

import de.schwarzes_brett.test_util.TestEnvironment;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.io.IOException;
import java.sql.SQLException;

/**
 * A JUnit5 extension for system testing.
 *
 * @author Tim-Florian Feulner
 */
final class STExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, TestWatcher {

    private final STLifecycle stLifecycle;
    private boolean testSucceeded = false;

    STExtension(int webDriverCount) {
        stLifecycle = new STLifecycle(webDriverCount);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws SQLException, IOException, InterruptedException {
        String testName = "";
        if (extensionContext.getParent().isPresent()) {
            testName = extensionContext.getParent().get().getDisplayName() + ".";
        }
        testName += extensionContext.getDisplayName();
        stLifecycle.init(testName);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        stLifecycle.reset();

        if (TestEnvironment.isLoadTest()) {
            String testName = "";
            if (extensionContext.getParent().isPresent()) {
                testName = extensionContext.getParent().get().getDisplayName() + ".";
            }
            testName += extensionContext.getDisplayName();
            TimeRecorder.getInstance().recordStart(testName);
        }
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        if (TestEnvironment.isLoadTest()) {
            String testName = "";
            if (extensionContext.getParent().isPresent()) {
                testName = extensionContext.getParent().get().getDisplayName() + ".";
            }
            testName += extensionContext.getDisplayName();
            TimeRecorder.getInstance().recordEnd(testName, testSucceeded);
        }
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws IOException {
        stLifecycle.destroy();
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        testSucceeded = true;
    }

    public WebInstance getWebInstance(int index) {
        return stLifecycle.getWebInstance(index);
    }

    public MailAccessor getMail() {
        return stLifecycle.getMail();
    }

}
