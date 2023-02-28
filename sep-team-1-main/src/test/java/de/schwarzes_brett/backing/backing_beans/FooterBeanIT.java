package de.schwarzes_brett.backing.backing_beans;

import de.schwarzes_brett.dto.ApplicationSettingsDTO;
import de.schwarzes_brett.test_util.integration_test.ITBase;
import de.schwarzes_brett.test_util.integration_test.ITPerClassExtension;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Testing environment for the footer bean.
 *
 * @author Valentin Damjantschitsch.
 */
@EnableAutoWeld
@ActivateScopes({RequestScoped.class, ApplicationScoped.class, ViewScoped.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(ITPerClassExtension.class)
@ExtendWith(MockitoExtension.class)
class FooterBeanIT extends ITBase {

    /**
     * Faces context.
     */
    @Produces
    @Default
    @Mock(serializable = true)
    private FacesContext facesContext;

    @Produces
    @Default
    @Mock(serializable = true)
    private ExternalContext externalContext;

    @Inject
    private FooterBean footerBean;

    /**
     * Tests, if applicationSettings get initialized as expected.
     */
    @Test
    void init() {
        ApplicationSettingsDTO applicationSettings;

        // In footerBean is @PostConstruct - therefore init() with fetchSettingsMinimal() gets called.
        applicationSettings = footerBean.getSettings();

        // Assertions.
        assertNotNull(applicationSettings.getContact());
        assertNotNull(applicationSettings.getDescription());
        assertNotNull(applicationSettings.getName());
        assertNotNull(applicationSettings.getStyleSheet());
        assertNotNull(applicationSettings.getLogo());
        assertNull(applicationSettings.getImprint());
        assertNull(applicationSettings.getPrivacyPolicy());
    }
}
