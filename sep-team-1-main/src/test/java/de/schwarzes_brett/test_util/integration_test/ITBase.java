package de.schwarzes_brett.test_util.integration_test;

import de.schwarzes_brett.backing.lifecycle.SystemStartStop;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import java.util.logging.Logger;

/**
 * A base class for every integration test. Manages some internal lifecycle requirements.
 *
 * @author Tim-Florian Feulner
 */
public abstract class ITBase {

    @Produces
    @Default
    private final Logger logger = Logger.getAnonymousLogger();
    @Inject
    private SystemStartStop systemStartStop;

    /**
     * Gets the injected {@code SystemStartStop} instance.
     *
     * @return The injected {@code SystemStartStop} instance.
     */
    public SystemStartStop getSystemStartStop() {
        return systemStartStop;
    }

}
