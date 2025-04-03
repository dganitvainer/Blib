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
 * A service that sends automatic reminders to library users about book returns.
 * This service runs once daily and checks which users need to be reminded about returning books.
 * It uses a singleton pattern to ensure only one notification service runs at a time.
 */

public class NotificationService {
    private static NotificationService instance;
    private final ScheduledExecutorService scheduler;
    private final dbHandler dbHandler;

    
    /**
     * Creates a new notification service.
     * Sets up the scheduler and starts the daily reminder checks.
     * 
     * @param dbHandler The database connection to use
     */

    private NotificationService(dbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduleReturnReminders();
        System.out.println("NotificationService initialized successfully");
    }

    /**
     * Gets or creates the notification service instance.
     * This ensures only one notification service runs at a time.
     * 
     * @param dbHandler The database connection to use
     * @return The notification service instance
     */

    public static synchronized NotificationService getInstance(dbHandler dbHandler) {
        if (instance == null) {
            instance = new NotificationService(dbHandler);
        }
        return instance;
    }

    /**
     * Sets up the daily schedule for sending return reminders.
     * Reminders are sent once per day at 4:25 AM.
     * If 4:25 AM has already passed for today, the first reminder will be sent tomorrow.
     */

    private void scheduleReturnReminders() {
        // Calculate initial delay until next run at 08:10
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.toLocalDate().atTime(LocalTime.of(4, 25));
        
        // If the time has already passed today, schedule for tomorrow
        if (now.toLocalTime().isAfter(LocalTime.of(4, 25))) {
            nextRun = nextRun.plusDays(1);
        }
        
        long initialDelay = ChronoUnit.MINUTES.between(now, nextRun);
        
        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    System.out.println("Starting daily return reminder check at: " + LocalDateTime.now());
                    sendReturnReminders();
                    System.out.println("Daily return reminder check completed: " + LocalDateTime.now());
                } catch (Exception e) {
                    System.err.println("Error in daily return reminder check: " + e.getMessage());
                    e.printStackTrace();
                }
            },
            initialDelay,
            TimeUnit.DAYS.toMinutes(1),  // Run daily
            TimeUnit.MINUTES
        );
        
        System.out.println("Scheduled next return reminder check for: " + nextRun);
    }
    
    /**
     * Sends reminder notifications to users who need to return books.
     * This method checks the database and creates reminders for users
     * whose book return dates are approaching.
     */
    private void sendReturnReminders() {
        try {
            dbHandler.createReturnReminders();
        } catch (SQLException e) {
            System.err.println("Error creating return reminders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stops the notification service safely.
     * Waits up to 60 seconds for any running tasks to complete.
     * If tasks don't complete in time, forces them to stop.
     */

    public void shutdown() {
        System.out.println("Shutting down NotificationService...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            System.out.println("NotificationService shutdown completed");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            System.err.println("NotificationService shutdown interrupted: " + e.getMessage());
        }
    }
}