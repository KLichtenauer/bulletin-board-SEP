package de.schwarzes_brett.data_access.transaction;

import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerMethodExtension;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Daniel Lipp
 */
@EnableAutoWeld
@ExtendWith(ITPerMethodExtension.class)
public class TransactionPsqlIT extends ITBase {

    private static final int ID = 200;

    /**
     * Test if the abort method words correctly.
     *
     * @throws SQLException is thrown when an error occurs.
     */
    @Test
    public void testAbortMethod() throws SQLException {
        TransactionPsql t = (TransactionPsql) TransactionFactory.produce();
        try {
            delete(t);
        } finally {
            t.abort();
        }
        assertIdExists();
    }

    /**
     * Tests the auto abortion of a Transaction.
     *
     * @throws SQLException is thrown when the connection is lost.
     */
    @Test
    public void testAutoAbort() throws SQLException {
        try (TransactionPsql t = (TransactionPsql) TransactionFactory.produce()) {
            delete(t);
        }
        assertIdExists();
    }

    private void delete(TransactionPsql t) throws SQLException {
        Connection c = t.getConnection();
        try (PreparedStatement statement = c.prepareStatement("DELETE FROM schwarzes_brett.user where id = " + ID)) {
            statement.executeUpdate();
        }
    }

    private void assertIdExists() throws SQLException {
        TransactionPsql t = (TransactionPsql) TransactionFactory.produce();
        Connection connection = t.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT nickname FROM schwarzes_brett.user where id = " + ID);
            ResultSet set = statement.executeQuery();
            boolean ret = set.next();
            set.close();
            statement.close();
            t.commit();
            assertTrue(ret);
        } finally {
            t.abort();
        }
    }
}
