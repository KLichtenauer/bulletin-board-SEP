package de.schwarzes_brett.business_logic.periodic;

import de.schwarzes_brett.data_access.periodic.DataAccessMaintainer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Realizes the periodic processes by means of a ScheduledThreadPoolExecutor.
 * This class is responsible for firing periodic actions that clean up unused data.
 * For example, it calls the {@link DataAccessMaintainer} periodically.
 *
 * @author Tim-Florian Feulner
 */
@Named
@ApplicationScoped
public class MaintenanceExecutor {

    private static final int THREAD_COUNT = 3;
    private static final int SHUTDOWN_TIMEOUT = 60;

    @Inject
    private transient Logger logger;

    private ScheduledThreadPoolExecutor threadPool;

    /**
     * Default constructor.
     */
    public MaintenanceExecutor() {}

    /**
     * Initializes the executor by starting the thread pool.
     */
    public void init() {
        threadPool = new ScheduledThreadPoolExecutor(THREAD_COUNT);

        threadPool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        threadPool.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);

        threadPool.scheduleAtFixedRate(ImageCleaner::cleanUnusedImages, ImageCleaner.EXECUTION_RATE, ImageCleaner.EXECUTION_RATE, TimeUnit.SECONDS);
        threadPool.scheduleAtFixedRate(VerificationCleaner::cleanUnverifiedUsers, VerificationCleaner.EXECUTION_RATE,
                                       VerificationCleaner.EXECUTION_RATE, TimeUnit.SECONDS);
        threadPool.scheduleAtFixedRate(DataAccessMaintainer::maintain, DataAccessMaintainer.EXECUTION_RATE, DataAccessMaintainer.EXECUTION_RATE,
                                       TimeUnit.SECONDS);

        logger.finest("MaintenanceExecutor initialized.");
    }

    /**
     * Destroys the executor by telling the thread pool to stop. Prevents further starting of actions and tries to terminate running tasks.
     */
    public void destroy() {
        threadPool.shutdownNow();

        boolean finishedExecution = false;
        logger.finest("Started waiting for termination of the MaintenanceExecutor.");
        try {
            finishedExecution = threadPool.awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.warning("Shutdown of MaintenanceExecutor was interrupted.");
        }

        if (!finishedExecution) {
            logger.warning("Could not shutdown the MaintenanceExecutor correctly.");
        } else {
            logger.finest("MaintenanceExecutor destroyed.");
        }
    }

}
