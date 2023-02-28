package de.schwarzes_brett.test_util.integration_test;

import de.schwarzes_brett.data_access.cache.ApplicationSettingsDaoCache;
import de.schwarzes_brett.data_access.cache.DAOCache;
import de.schwarzes_brett.data_access.cache.UserDaoCache;
import jakarta.servlet.ServletContext;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Manages the lifecycle of the application for integration testing.
 *
 * @author Tim-Florian Feulner
 */
final class ApplicationLifecycle {

    private final ServletContext servletContext = mock(ServletContext.class);

    private static void clearCache(DAOCache<?> cache) {
        try {
            Field field = DAOCache.class.getDeclaredField("keyValueStore");
            field.setAccessible(true);
            ((HashMap<?, ?>) field.get(cache)).clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new Error("Invalid cache field operations specified.");
        }
    }

    public void init(ITBase itBase) {
        when(servletContext.getResourceAsStream(anyString())).thenAnswer(
                invocation -> {
                    String path = invocation.getArgument(0);
                    path = path.replace("WEB-INF/classes", ".");
                    return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                }
        );

        // Clear caches in between tests.
        clearCache(ApplicationSettingsDaoCache.getInstance());
        clearCache(UserDaoCache.getInstance());

        itBase.getSystemStartStop().init(servletContext);
    }

    public void destroy(ITBase itBase) {
        itBase.getSystemStartStop().destroy(null);
    }

}
