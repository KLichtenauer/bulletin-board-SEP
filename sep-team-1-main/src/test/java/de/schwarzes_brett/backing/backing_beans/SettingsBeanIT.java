package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.MalformedURLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

/**
 * Tests the right configuration of the settings bean.
 *
 * @author Valentin Damjantschitsch
 */
@EnableAutoWeld
@ActivateScopes({RequestScoped.class, ApplicationScoped.class, ViewScoped.class, SessionScoped.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
@ExtendWith(MockitoExtension.class)
class SettingsBeanIT extends ITBase {

    @Inject
    private SettingsBean settingsBean;

    @Produces
    @Default
    @Mock(serializable = true)
    private ExternalContext externalContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private FacesContext facesContext;

    @BeforeEach
    void init() {
        try {
            lenient().when(externalContext.getResource("resources/css")).thenReturn(getClass().getClassLoader().getResource("resources/css"));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Path not existing", e);
        }
    }

    @Test
    void testGetListOfStylesheets() {
        List<String> result = settingsBean.getListOfStylesheets();
        assertAll(() -> assertTrue(result.contains("BlueShell")), () -> assertTrue(result.contains("DaintreeForest")),
                  () -> assertTrue(result.contains("Sunset")));
    }
}
