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
 * Service class responsible for managing book reservation expirations.
 * Runs daily at 23:00 to check for expired fulfilled reservations and manages
 * the cascading effects of cancellations, including updating book availability
 * and notifying next-in-line subscribers.
 */
public class ReservationExpiryService {
    private static ReservationExpiryService instance;
    private final ScheduledExecutorService scheduler;
    private final dbHandler dbHandler;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the scheduler and sets up daily reservation checks.
     *
     * @param dbHandler Database handler for performing reservation operations
     */
    private ReservationExpiryService(dbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduleReservationChecks();
        System.out.println("ReservationExpiryService initialized successfully");
    }

    /**
     * Gets the singleton instance of ReservationExpiryService.
     * Creates the instance if it doesn't exist.
     *
     * @param dbHandler Database handler instance
     * @return The singleton instance of ReservationExpiryService
     */
    public static synchronized ReservationExpiryService getInstance(dbHandler dbHandler) {
        if (instance == null) {
            instance = new ReservationExpiryService(dbHandler);
        }
        return instance;
    }

    /**
     * Schedules the daily reservation expiry checks.
     * Runs at 23:00 every day to process expired reservations.
     */
    private void scheduleReservationChecks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.toLocalDate().atTime(LocalTime.of(5, 58));
        
        if (now.toLocalTime().isAfter(LocalTime.of(5, 58))) {
            nextRun = nextRun.plusDays(1);
        }
        
        long initialDelay = ChronoUnit.MINUTES.between(now, nextRun);
        
        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    System.out.println("Starting daily reservation expiry check at: " + LocalDateTime.now());
                    processExpiredReservations();
                    System.out.println("Daily reservation expiry check completed: " + LocalDateTime.now());
                } catch (Exception e) {
                    System.err.println("Error in daily reservation expiry check: " + e.getMessage());
                    e.printStackTrace();
                }
            },
            initialDelay,
            TimeUnit.DAYS.toMinutes(1), // Run daily
            TimeUnit.MINUTES
        );
        
        System.out.println("Scheduled next reservation expiry check for: " + nextRun);
    }

    /**
     * Processes expired reservations and manages the cascading effects.
     * For each expired fulfilled reservation:
     * 1. Cancels the reservation
     * 2. Updates book availability
     * 3. Notifies the subscriber
     * 4. Checks for and processes next-in-line reservations
     */
    private void processExpiredReservations() {
        try {
            dbHandler.processExpiredReservations();
        } catch (SQLException e) {
            System.err.println("Error processing expired reservations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shuts down the reservation expiry service.
     * Ensures all scheduled tasks are completed or terminated.
     */
    public void shutdown() {
        System.out.println("Shutting down ReservationExpiryService...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            System.out.println("ReservationExpiryService shutdown completed");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            System.err.println("ReservationExpiryService shutdown interrupted: " + e.getMessage());
        }
    }
}