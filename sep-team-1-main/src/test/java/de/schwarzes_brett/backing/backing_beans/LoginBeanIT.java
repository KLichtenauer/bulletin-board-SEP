package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.backing.validator_converter.PasswordConverter;
import de.schwarzes_brett.business_logic.dictionary.CompleteDictionary;
import de.schwarzes_brett.data_access.db.ConnectionPoolPsql;
import de.schwarzes_brett.dto.PasswordDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerMethodExtension;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
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
import org.mockito.ArgumentCaptor;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The test class for testing the login. This includes the test T3.
 *
 * @author Tim-Florian Feulner
 */
@EnableAutoWeld
@ActivateScopes({RequestScoped.class, ViewScoped.class, SessionScoped.class, ApplicationScoped.class})
@ExtendWith(ITPerMethodExtension.class)
@ExcludeBeanClasses(Dictionary.class)
@ExplicitParamInjection
@ExtendWith(MockitoExtension.class)
class LoginBeanIT extends ITBase {

    private static final String WELCOME_PAGE_FORWARD = "/view/user/welcome";

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
    @Default
    @Mock(serializable = true)
    private Map<String, Object> componentAttributeMap;

    @Produces
    @Default
    @Mock(serializable = true)
    private Flash flash;

    @Inject
    private LoginBean loginBean;

    @Inject
    private HeaderBean headerBean;

    @Inject
    private UserSession userSession;

    @Inject
    @FacesConverter(value = "passwordConverter", managed = true)
    @Any
    @Named
    private PasswordConverter passwordConverter;

    @BeforeEach
    void init() {
        lenient().when(externalContext.getRequest()).thenReturn(httpServletRequest);
        lenient().when(dictionary.get(anyString())).thenAnswer(
                invocation -> PropertyResourceBundle.getBundle("de.schwarzes_brett.backing.dictionary.i18n.phrases", Locale.GERMAN)
                                                    .getString(invocation.getArgument(0)));
        lenient().when(externalContext.getRequestLocale()).thenReturn(Locale.forLanguageTag("DE"));
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

    @ParameterizedTest
    @CsvSource({"admin,start123", "Stefan1,123456"})
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    void testValidLoginLogout(String username, String password) {
        loginBean.getCredentials().setUsername(username);
        loginBean.getCredentials().setPassword(generatePasswordDTO(username, password));

        assertEquals(WELCOME_PAGE_FORWARD, loginBean.login());

        assertEquals(username, userSession.getUser().getCredentials().getUsername());
        verify(httpServletRequest, times(1)).changeSessionId();
        verify(dictionary, times(0)).get(anyString());

        headerBean.logout();

        verify(externalContext, times(1)).invalidateSession();
    }

    @ParameterizedTest
    @CsvSource({"admina,start123", "Stefan1,123", "sdfisd,iasdfis"})
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    void testInvalidLogin(String username, String password) {
        loginBean.getCredentials().setUsername(username);
        loginBean.getCredentials().setPassword(generatePasswordDTO(username, password));

        assertNull(loginBean.login());

        ArgumentCaptor<FacesMessage> facesMessageArgument = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageArgument.capture());
        assertEquals(CompleteDictionary.get("f_login_failed", Locale.GERMAN), facesMessageArgument.getValue().getSummary());

        verify(httpServletRequest, times(0)).changeSessionId();
        verify(dictionary, times(1)).get(anyString());
    }

    @ParameterizedTest
    @CsvSource({"admin,start123,EN", "Stefan1,123456,DE", "Stefan1,123456,JP"})
    void testLocalChange(String username, String password, String localeString) throws SQLException {
        loginBean.getCredentials().setUsername(username);
        loginBean.getCredentials().setPassword(generatePasswordDTO(username, password));
        Locale locale = Locale.forLanguageTag(localeString);

        when(externalContext.getRequestLocale()).thenReturn(locale);

        loginBean.login();
        headerBean.logout();

        // Check if correct local was set in database.
        Connection connection = ConnectionPoolPsql.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement("SELECT language FROM schwarzes_brett.user WHERE nickname = ?;")) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                String savedLanguage = result.getString("language");
                assertEquals(localeString.toLowerCase(), savedLanguage.toLowerCase());
            }
        } finally {
            ConnectionPoolPsql.getInstance().releaseConnection(connection);
        }
    }

    @ParameterizedTest
    @CsvSource({"something,123456"})
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    void testUnverifiedLogin(String username, String password) {
        loginBean.getCredentials().setUsername(username);
        loginBean.getCredentials().setPassword(generatePasswordDTO(username, password));

        assertNull(loginBean.login());

        ArgumentCaptor<FacesMessage> facesMessageArgument = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageArgument.capture());
        assertEquals(CompleteDictionary.get("f_login_unverified", Locale.GERMAN), facesMessageArgument.getValue().getSummary());

        verify(httpServletRequest, times(0)).changeSessionId();
        verify(dictionary, times(1)).get(anyString());
    }

}
