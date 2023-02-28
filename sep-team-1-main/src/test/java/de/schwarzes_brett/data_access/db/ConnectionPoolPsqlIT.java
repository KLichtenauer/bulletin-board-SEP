package de.schwarzes_brett.data_access.db;

import de.schwarzes_brett.data_access.exception.DataStorageUnavailableException;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Jonas Elsper
 */
@EnableAutoWeld
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
public class ConnectionPoolPsqlIT extends ITBase {
    @Test
    void checkMoreThanMaxConnections() {
        ConnectionPoolPsql.getInstance().getConnection();
        ConnectionPoolPsql.getInstance().getConnection();
        assertThrows(DataStorageUnavailableException.class, () -> ConnectionPoolPsql.getInstance().getConnection());
    }
}
