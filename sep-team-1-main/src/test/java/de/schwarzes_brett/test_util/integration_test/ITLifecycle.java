package de.schwarzes_brett.test_util.integration_test;

import com.icegreen.greenmail.util.GreenMail;
import de.schwarzes_brett.test_util.DBServerLifecycle;
import de.schwarzes_brett.test_util.MailServerFactory;
import de.schwarzes_brett.test_util.TestDataInjector;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Manages the combined lifecycle for integration testing.
 *
 * @author Tim-Florian Feulner
 */
final class ITLifecycle {


    private static final String TEST_DATA_SCRIPT_PATH = "de/schwarzes_brett/testing_data/testingDataIT.sql";

    private final Logger logger = Logger.getLogger(ITLifecycle.class.getName());

    private final GreenMail greenMail = MailServerFactory.createMailServer();
    private final DBServerLifecycle dbServer = new DBServerLifecycle();
    private final ApplicationLifecycle lifecycle = new ApplicationLifecycle();

    public void init(ITBase itBase) throws IOException, InterruptedException, SQLException {
        logger.info("Initializing integration test.");

        greenMail.start();
        dbServer.init();
        lifecycle.init(itBase);

        try (Connection connection = dbServer.getConnection()) {
            TestDataInjector.performInjection(connection, TEST_DATA_SCRIPT_PATH);
        }
    }

    public void destroy(ITBase itBase) throws IOException {
        logger.info("Shutting down integration test.");

        lifecycle.destroy(itBase);
        dbServer.destroy();
        greenMail.stop();
    }

    public GreenMail getMail() {
        return greenMail;
    }

}
