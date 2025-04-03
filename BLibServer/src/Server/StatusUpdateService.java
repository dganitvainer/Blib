package Server;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import jdbc.dbHandler;

/**
 * Service class responsible for automatically updating subscriber statuses.
 * checks daily to identify and update subscribers who has frozen status for a month
 * so there penalty period is over.
 * Implements the Singleton pattern to ensure only one instance manages status updates.
 */
public class StatusUpdateService {
    private static StatusUpdateService instance;
    private final ScheduledExecutorService scheduler;
    private final dbHandler dbHandler;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the scheduler and sets up daily status update checks.
     *
     * @param dbHandler Database handler instance for performing status updates
     */
    private StatusUpdateService(dbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduleStatusUpdates();
        System.out.println("StatusUpdateService initialized successfully");
    }

    /**
     * Gets the singleton instance of StatusUpdateService.
     * Creates the instance if it doesn't exist.
     *
     * @param dbHandler Database handler instance
     * @return The singleton instance of StatusUpdateService
     */
    public static synchronized StatusUpdateService getInstance(dbHandler dbHandler) {
        if (instance == null) {
            instance = new StatusUpdateService(dbHandler);
        }
        return instance;
    }

    /**
     * Schedules the daily status update checks.
     * Runs at 00:01 every day to check and update subscriber statuses.
     * If the scheduled time has passed for the current day, schedules for the next day.
     */
    private void scheduleStatusUpdates() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.toLocalDate().atTime(LocalTime.of(4, 25));
        
        if (now.toLocalTime().isAfter(LocalTime.of(4, 25))) {
            nextRun = nextRun.plusDays(1);
        }
        
        long initialDelay = ChronoUnit.MINUTES.between(now, nextRun);
        
        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    System.out.println("Starting daily status update check at: " + LocalDateTime.now());
                    processStatusUpdates();
                    System.out.println("Daily status update check completed: " + LocalDateTime.now());
                } catch (Exception e) {
                    System.err.println("Error in daily status update check: " + e.getMessage());
                    e.printStackTrace();
                }
            },
            initialDelay,
            TimeUnit.DAYS.toMinutes(1),
            TimeUnit.MINUTES
        );
        
        System.out.println("Scheduled next status update check for: " + nextRun);
    }

    /**
     * Processes the status updates for all eligible subscribers.
     * Calls the database handler to perform the actual updates.
     */
    private void processStatusUpdates() {
        try {
            dbHandler.processSubscriberStatusUpdates();
        } catch (SQLException e) {
            System.err.println("Error processing status updates: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shuts down the status update service.
     * Ensures all scheduled tasks are completed or terminated.
     * Waits up to 60 seconds for tasks to complete before forcing shutdown.
     */
    public void shutdown() {
        System.out.println("Shutting down StatusUpdateService...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            System.out.println("StatusUpdateService shutdown completed");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            System.err.println("StatusUpdateService shutdown interrupted: " + e.getMessage());
        }
    }
}