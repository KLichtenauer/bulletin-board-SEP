package de.schwarzes_brett.business_logic.periodic;

import de.schwarzes_brett.backing.backing_beans.RegistrationBean;
import de.schwarzes_brett.backing.validator_converter.PasswordConverter;
import de.schwarzes_brett.data_access.db.ConnectionPoolPsql;
import de.schwarzes_brett.dto.PasswordDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerMethodExtension;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests the functionality of the unverified user cleanup. This includes test T16.
 *
 * @author Tim-Florian Feulner
 */
@EnableAutoWeld
@ExtendWith(ITPerMethodExtension.class)
@ExtendWith(MockitoExtension.class)
@ActivateScopes({RequestScoped.class, ViewScoped.class, SessionScoped.class, ApplicationScoped.class})
class VerificationCleanerIT extends ITBase {

    private static final String LANDING_PAGE_PATH = "/view/public/landing";

    @Produces
    @Default
    @Mock(serializable = true)
    private FacesContext facesContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private ExternalContext externalContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private UIComponent usernameComponent;

    @Produces
    @Default
    @Mock(serializable = true)
    private Map<String, Object> componentAttributeMap;

    @Inject
    @FacesConverter(value = "passwordConverter", managed = true)
    @Any
    @Named
    private PasswordConverter passwordConverter;

    @Inject
    private RegistrationBean registrationBean;

    @BeforeEach
    void init() {
        when(externalContext.getRequestLocale()).thenReturn(Locale.GERMAN);

        when(externalContext.getRequestScheme()).thenReturn("");
        when(externalContext.getRequestServerName()).thenReturn("");
        when(externalContext.getRequestServerPort()).thenReturn(0);
        when(externalContext.getRequestContextPath()).thenReturn("");
    }

    private PasswordDTO generatePasswordDTO(String password) {
        when(usernameComponent.getAttributes()).thenReturn(componentAttributeMap);
        when(componentAttributeMap.get("usernameValue")).thenReturn(null);

        return passwordConverter.getAsObject(facesContext, usernameComponent, password);
    }

    @Test
    void testUnverifiedUserCleanup() throws SQLException {
        final String username = "hnsdfdbfs8";

        // Register user.
        registrationBean.getUser().getCredentials().setUsername(username);
        registrationBean.getUser().getCredentials().setPassword(generatePasswordDTO("asdfasdf"));
        registrationBean.getUser().setFirstName("d");
        registrationBean.getUser().setLastName("d");
        registrationBean.getUser().setStreet("d");
        registrationBean.getUser().setStreetNumber("d");
        registrationBean.getUser().setAddressAddition("d");
        registrationBean.getUser().setPostalCode("d");
        registrationBean.getUser().setCity("d");
        registrationBean.getUser().setCountry("d");
        registrationBean.getUser().setEmail("nz4zbsrf8sezf@d.dd");
        registrationBean.getUser().setLanguage(Locale.GERMAN);
        String navigationTarget = registrationBean.submit();
        assertEquals(navigationTarget, LANDING_PAGE_PATH);

        // Modify registration date to be far in the past.
        Connection connection = null;
        try {
            connection = ConnectionPoolPsql.getInstance().getConnection();
            try (Statement statement = connection.createStatement()) {
                int updateCount = statement.executeUpdate(
                        "UPDATE schwarzes_brett.user SET verification_secret_creation_time = '2022-11-30 21:53:00+01' WHERE nickname = '" + username
                        + "';");
                assertEquals(1, updateCount);
            }
            connection.commit();
        } finally {
            ConnectionPoolPsql.getInstance().releaseConnection(connection);
        }

        // Check cleanup functionality.
        int userCountBefore = getUserCount();
        VerificationCleaner.cleanUnverifiedUsers();
        int userCountAfter = getUserCount();
        assertEquals(userCountAfter, userCountBefore - 1);
    }

    private int getUserCount() throws SQLException {
        Connection connection = null;
        int userCount;
        try {
            connection = ConnectionPoolPsql.getInstance().getConnection();
            try (Statement statement = connection.createStatement()) {
                try (ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM schwarzes_brett.user;")) {
                    result.next();
                    userCount = result.getInt(1);
                }
            }
            connection.commit();
        } finally {
            ConnectionPoolPsql.getInstance().releaseConnection(connection);
        }
        return userCount;
    }

}
