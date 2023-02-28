package de.schwarzes_brett.test_util;

/**
 * Provides information about the environment under which the system tests are running.
 *
 * @author Tim-Florian Feulner
 */
public final class TestEnvironment {

    private static final String PROCESS_ID = System.getenv("PROCESS_ID");
    private static final String SERVER_URL = System.getenv("SERVER_URL");

    private TestEnvironment() {}

    /**
     * Checks if currently running raw system tests.
     *
     * @return {@code true} iff running blackbox tests for testing functionality.
     */
    public static boolean isSystemTest() {
        return PROCESS_ID == null || PROCESS_ID.isEmpty();
    }

    /**
     * Checks if currently running load tests.
     *
     * @return {@code true} iff running blackbox tests for testing performance.
     */
    public static boolean isLoadTest() {
        return !isSystemTest();
    }

    /**
     * Provides the id of the current process if running a load test,
     * or {@code null} if running raw system tests.
     *
     * @return The load test process id.
     */
    public static String getProcessId() {
        return isLoadTest() ? PROCESS_ID : null;
    }

    /**
     * Provides the base url to connect to the application server.
     *
     * @return The base url string.
     */
    public static String getBaseURL() {
        if (isSystemTest()) {
            return "https://localhost:" + TomcatServerLifecycle.HTTPS_PORT + "/schwarzes_brett/";
        } else {
            return SERVER_URL + "/";
        }
    }

}
