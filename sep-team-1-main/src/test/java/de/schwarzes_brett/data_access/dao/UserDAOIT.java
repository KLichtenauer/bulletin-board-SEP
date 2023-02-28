package de.schwarzes_brett.data_access.dao;

import de.schwarzes_brett.data_access.exception.DuplicateEmailAddressException;
import de.schwarzes_brett.data_access.exception.DuplicateUsernameException;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.data_access.transaction.TransactionPsql;
import de.schwarzes_brett.dto.PasswordDTO;
import de.schwarzes_brett.dto.Role;
import de.schwarzes_brett.dto.TokenDTO;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author michaelgruner
 */
@EnableAutoWeld
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDAOIT extends ITBase {

    private static final UserDTO USER = new UserDTO();
    private static final TokenDTO TOKEN = new TokenDTO();

    @BeforeAll
    static void initDTOs() {
        USER.setRole(Role.USER);
        USER.setVerificationStatus(UserDTO.VerificationStatus.REGISTERED_NOT_VERIFIED);
        USER.setLanguage(Locale.GERMAN);
        USER.getCredentials().setUsername("Michael");
        PasswordDTO password = new PasswordDTO();
        USER.getCredentials().setPassword(password);
        password.setPasswordHash("abcdefg");
        password.setPwdSalt("abcdefg");
        USER.setFirstName("Michi");
        USER.setLastName("Grüner");
        USER.setEmail("gruenermichi@email.com");
        USER.setStreet("Oberersand");
        USER.setStreetNumber("42");
        USER.setPostalCode("94032");
        USER.setCity("Passau");
        USER.setCountry("Germany");
        TOKEN.setToken("token123");
        TOKEN.setUsername("Michael");
    }

    @AfterAll
    static void cleanUp() throws SQLException {
        try (TransactionPsql transaction = (TransactionPsql) TransactionFactory.produce()) {
            Connection connection = transaction.getConnection();
            try {
                PreparedStatement statement = connection.prepareStatement("DELETE FROM schwarzes_brett.user WHERE nickname = 'Michael'");
                statement.executeUpdate();
                statement.close();
                transaction.commit();
            } finally {
                transaction.abort();
            }
        }
    }

    /**
     * Tests the insertion of the user in the database.
     *
     * @throws DuplicateEmailAddressException Not possible while testing.
     * @throws DuplicateUsernameException     Not possible while testing.
     */
    @Test
    @Order(1)
    public void testInsertUser() throws DuplicateEmailAddressException, DuplicateUsernameException {
        try (Transaction transaction = TransactionFactory.produce()) {
            USER.setContactInfoId(DAOFactory.getUserDAO(transaction).insertContactData(USER));
            DAOFactory.getUserDAO(transaction).insertUser(USER, TOKEN);
            UserDTO userMichael = new UserDTO();
            userMichael.getCredentials().setUsername("Michael");
            assertTrue(DAOFactory.getUserDAO(transaction).fetchUserByUsername(userMichael));
            transaction.commit();
        }

    }

    /**
     * Tests if the user gets verified properly.
     */
    @Test
    @Order(2)
    public void testVerifyUser() {
        try (Transaction transaction = TransactionFactory.produce()) {
            UserDTO userMichael = new UserDTO();
            userMichael.getCredentials().setUsername("Michael");
            assertAll(() -> assertTrue(DAOFactory.getUserDAO(transaction).processToken(TOKEN)),
                      () -> assertTrue(DAOFactory.getUserDAO(transaction).fetchUserByUsername(userMichael)),
                      () -> assertSame(userMichael.getVerificationStatus(), UserDTO.VerificationStatus.VERIFIED));
            transaction.commit();
        }

    }
}
