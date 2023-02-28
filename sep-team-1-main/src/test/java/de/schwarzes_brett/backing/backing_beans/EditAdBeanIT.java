package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.backing.dictionary.Dictionary;
import de.schwarzes_brett.backing.session.UserSession;
import de.schwarzes_brett.data_access.dao.DAOFactory;
import de.schwarzes_brett.data_access.transaction.Transaction;
import de.schwarzes_brett.data_access.transaction.TransactionFactory;
import de.schwarzes_brett.dto.AdDTO;
import de.schwarzes_brett.dto.Role;
import de.schwarzes_brett.dto.UserDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerMethodExtension;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.application.Application;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.jboss.weld.junit5.ExplicitParamInjection;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jboss.weld.junit5.auto.ExcludeBeanClasses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
class EditAdBeanIT extends ITBase {

    private static final int AD_ID = 100;
    private static final int USER_ID = 200;
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
    @Inject
    private UserSession userSession;
    private AdDTO ad;
    private UserDTO user;
    @Inject
    private EditAdBean adBean;

    @BeforeEach
    void init() {
        user = new UserDTO();
        user.setId(USER_ID);
        ad = new AdDTO();
        ad.setId(AD_ID);
        Flash flash = mock(Flash.class);
        lenient().when(externalContext.getFlash()).thenReturn(flash);
        when(flash.get("ad")).thenReturn(ad);
    }

    @Test
    void testLoadAdFromFlash() {
        ad = adBean.getAd();
        AdDTO exp = new AdDTO();
        exp.setId(AD_ID);
        try (Transaction t = TransactionFactory.produce()) {
            boolean isAdmin = userSession.getUser().getRole() != null && userSession.getUser().getRole().equals(Role.ADMIN);
            boolean isCreator = ad.getCreator() != null && ad.getCreator().getId().equals(userSession.getUser().getId());
            boolean isCreatorOrAdmin = isAdmin || isCreator;
            DAOFactory.getAdDAO(t).fetchAd(exp, user.getId(), isCreatorOrAdmin);
            t.commit();
        }
        boolean adsEqual = equal(exp.getId(), ad.getId())
                           && equal(exp.getTitle(), (ad.getTitle()))
                           && equal(exp.getDescription(), ad.getDescription())
                           && equal(exp.getPublicData().getContactInfoId(), ad.getPublicData().getContactInfoId())
                           && equal(exp.getCreator().getId(), ad.getCreator().getId())
                           && equal(exp.getImages(), ad.getImages())
                           && equal(exp.getThumbnail().getId(), ad.getThumbnail().getId())
                           && equal(exp.getRelease(), ad.getRelease());
        assertTrue(adsEqual);
    }

    private boolean equal(Object s1, Object s2) {
        if (s1 == null) {
            return s2 == null;
        } else {
            return s1.equals(s2);
        }
    }

}
