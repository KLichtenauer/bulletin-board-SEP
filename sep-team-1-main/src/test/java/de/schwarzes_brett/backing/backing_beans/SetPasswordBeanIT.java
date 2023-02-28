package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.business_logic.services.PasswordHashService;
import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.CredentialsDTO;
import de.schwarzes_brett.dto.PasswordDTO;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerMethodExtension;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.application.Application;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jboss.weld.junit5.auto.ExcludeBeanClasses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The test class for testing the Edit Ad page. This includes the test T11.
 *
 * @author Daniel Lipp
 */
@EnableAutoWeld
@ExtendWith(ITPerMethodExtension.class)
@ExtendWith(MockitoExtension.class)
@ActivateScopes({RequestScoped.class, ViewScoped.class, SessionScoped.class, ApplicationScoped.class})
@ExcludeBeanClasses(Dictionary.class)
@ExplicitParamInjection
public class SetPasswordBeanIT extends ITBase {

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

    @Inject
    private SetPasswordBean setPwdBean;

    @Produces
    @ManagedProperty("#{flash.keep.user}")
    @SuppressWarnings("PMD.SingularField")
    private UserDTO user = new UserDTO();

    @Test
    void testUserIsReadFromFlash() {
        assertEquals(user, setPwdBean.getUser());
    }

    @Test
    void testPasswordIsChanged() {
        PasswordHashService pwdService = new PasswordHashService();
        PasswordDTO pwdDTO = pwdService.createPasswordDTO("password");
        user = new UserDTO();
        user.setCredentials(new CredentialsDTO());
        String username = "Lisa4";
        user.getCredentials().setUsername(username);
        user.getCredentials().setPassword(pwdDTO);
        setPwdBean.submit();
        try (Transaction t = TransactionFactory.produce()) {
            UserDTO storedUser = new UserDTO();
            storedUser.setCredentials(new CredentialsDTO());
            storedUser.getCredentials().setUsername(username);
            assert DAOFactory.getUserDAO(t).fetchUserByUsername(storedUser) : "Could not fetch user";
            t.commit();
            PasswordDTO p = storedUser.getCredentials().getPassword();
            assertEquals(pwdDTO.getPasswordHash(), p.getPasswordHash());
            assertEquals(pwdDTO.getPwdSalt(), p.getPwdSalt());
        }
    }

}
