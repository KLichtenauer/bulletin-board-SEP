package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.backing.validator_converter.PasswordConverter;
import de.schwarzes_brett.data_access.db.ConnectionPoolPsql;
import de.schwarzes_brett.dto.PasswordDTO;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerMethodExtension;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.application.Application;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.faces.convert.FacesConverter;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jboss.weld.junit5.auto.ExcludeBeanClasses;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * The test class for testing the profile. This includes the test T12.
 *
 * @author Tim-Florian Feulner
 */
@EnableAutoWeld
@ActivateScopes({RequestScoped.class, ViewScoped.class, SessionScoped.class, ApplicationScoped.class})
@ExtendWith(ITPerMethodExtension.class)
@ExcludeBeanClasses(Dictionary.class)
@ExplicitParamInjection
@ExtendWith(MockitoExtension.class)
class ProfileBeanIT extends ITBase {

    @Produces
    @Default
    @Mock(serializable = true)
    private FacesContext facesContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private UIViewRoot uiViewRoot;

    @Produces
    @Default
    @Mock(serializable = true)
    private Application application;

    @Produces
    @Default
    @Mock(serializable = true)
    private ExternalContext externalContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private HttpServletRequest httpServletRequest;

    @Produces
    @Default
    @Mock(serializable = true)
    private Dictionary dictionary;

    @Produces
    @Default
    @Mock(serializable = true)
    private UIComponent usernameComponent;

    @Produces
    @Default
    @Mock(serializable = true)
    private UIInput usernameInput;

    @Produces
    @ManagedProperty("#{flash.keep.user}")
    @SuppressWarnings("PMD.SingularField")
    private UserDTO profileUser;

    @Produces
    @Default
    @Mock(serializable = true)
    private Map<String, Object> componentAttributeMap;

    @Inject
    private LoginBean loginBean;

    @Inject
    private UserSession userSession;

    @Inject
    private ProfileBean profileBean;

    @Inject
    @FacesConverter(value = "passwordConverter", managed = true)
    @Any
    @Named
    private PasswordConverter passwordConverter;

    @Produces
    @Default
    @Mock(serializable = true)
    private Flash flash;

    @BeforeEach
    void init() {
        lenient().when(externalContext.getRequest()).thenReturn(httpServletRequest);
        lenient().when(dictionary.get(anyString())).thenAnswer(
                invocation -> PropertyResourceBundle.getBundle("de.schwarzes_brett.backing.dictionary.i18n.phrases", Locale.GERMAN)
                                                    .getString(invocation.getArgument(0)));
        lenient().when(externalContext.getRequestLocale()).thenReturn(Locale.GERMAN);
        lenient().when(externalContext.getFlash()).thenReturn(flash);
    }

    @AfterEach
    void reset() {
        Mockito.reset(externalContext, httpServletRequest, usernameComponent, usernameInput, componentAttributeMap);
    }

    private PasswordDTO generatePasswordDTO(String username, String password) {
        when(usernameComponent.getAttributes()).thenReturn(componentAttributeMap);
        when(componentAttributeMap.get("usernameValue")).thenReturn("login:username");
        when(facesContext.getViewRoot()).thenReturn(uiViewRoot);
        when(uiViewRoot.findComponent("login:username")).thenReturn(usernameInput);
        when(usernameInput.getValue()).thenReturn(username);

        return passwordConverter.getAsObject(facesContext, usernameComponent, password);
    }

    void performLogin(String username, String password) {
        loginBean.getCredentials().setUsername(username);
        loginBean.getCredentials().setPassword(generatePasswordDTO(username, password));
        loginBean.login();
    }

    @ParameterizedTest
    @CsvSource({"Hans,123456"})
    @SuppressFBWarnings("URF_UNREAD_FIELD")
    void testUserDeletionCascadesAdDeletion(String username, String password) throws SQLException {
        performLogin(username, password);

        profileUser = userSession.getUser();
        profileBean.deleteProfile();

        Connection connection = ConnectionPoolPsql.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM schwarzes_brett.ad WHERE id = 500;")) {
            try (ResultSet result = statement.executeQuery()) {
                assertFalse(result.next());
            }
        } finally {
            ConnectionPoolPsql.getInstance().releaseConnection(connection);
        }
    }

}
