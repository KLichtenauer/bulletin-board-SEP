package de.schwarzes_brett.test_util.system_test;

import com.icegreen.greenmail.util.GreenMail;
import de.schwarzes_brett.test_util.DBServerLifecycle;
import de.schwarzes_brett.test_util.MailServerFactory;
import de.schwarzes_brett.test_util.TestDataInjector;
import de.schwarzes_brett.test_util.TestEnvironment;
import de.schwarzes_brett.test_util.TomcatServerLifecycle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Manages the combined lifecycle for system testing.
 *
 * @author Tim-Florian Feulner
 */
final class STLifecycle {

    private static final String TEST_DATA_SCRIPT_PATH = "de/schwarzes_brett/testing_data/testingDataST.sql";

    private final Logger logger = Logger.getLogger(STLifecycle.class.getName());
    private final DBServerLifecycle dbServer = new DBServerLifecycle();
    private final TomcatServerLifecycle tomcatServer = new TomcatServerLifecycle();
    private final List<WebDriverLifecycle> webLifecycles = new ArrayList<>();
    private GreenMail greenMail;

    STLifecycle(int webDriverCount) {
        for (int i = 0; i < webDriverCount; ++i) {
            webLifecycles.add(new WebDriverLifecycle());
        }
    }

    public void init(String testName) throws IOException, InterruptedException, SQLException {
        logger.info("Initializing system test.");

        if (TestEnvironment.isSystemTest()) {
            if (greenMail == null) {
                greenMail = MailServerFactory.createMailServer();
            }
            greenMail.start();
            dbServer.init();
            tomcatServer.init(testName);

            try (Connection connection = dbServer.getConnection()) {
                TestDataInjector.waitForInitializedSchema(connection);
                TestDataInjector.performInjection(connection, TEST_DATA_SCRIPT_PATH);
            }
        }

        for (WebDriverLifecycle webLifecycle : webLifecycles) {
            webLifecycle.init();
        }
    }

    public void destroy() throws IOException {
        logger.info("Shutting down system test.");

        for (WebDriverLifecycle webLifecycle : webLifecycles) {
            webLifecycle.destroy();
        }

        if (TestEnvironment.isSystemTest()) {
            tomcatServer.destroy();
            dbServer.destroy();
            greenMail.stop();
        }
    }

    public void reset() {
        for (WebDriverLifecycle webLifecycle : webLifecycles) {
            webLifecycle.reset();
        }
    }

    public WebInstance getWebInstance(int index) {
        return webLifecycles.get(index).getWebInstance();
    }

    public MailAccessor getMail() {
        if (TestEnvironment.isSystemTest()) {
            MailAccessor.getInstance().useLocalGreenMail(greenMail);
        } else {
            MailAccessor.getInstance().useRemoteIMAP();
        }
        return MailAccessor.getInstance();
    }

}
