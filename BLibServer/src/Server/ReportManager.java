package Server;

import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.sql.SQLException;

import entities.*;
import jdbc.dbHandler;

/**
 * Manages report generation and storage for the library management system.
 * Ensures one unique report per report type per day, with automatic reuse of existing reports.
 * 
 * Key Features:
 * - Singleton pattern for report management
 * - Automated monthly report generation
 * - Prevents duplicate report generation on the same day
 * - Loads existing reports if regeneration is attempted
 */
public class ReportManager {
    /**
     * Directory for storing generated reports
     */
    private static final String REPORTS_DIR = "./reports";

    /**
     * Formatter for generating date-based filenames
     */
    private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Scheduler for automated report generation tasks
     */
    private final ScheduledExecutorService scheduler;

    /**
     * Database handler for retrieving report data
     */
    private final dbHandler dbHandler;

    /**
     * Singleton instance of ReportManager
     */
    private static ReportManager instance;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the scheduler, report directory, and schedules monthly report generation.
     * 
     * @param dbHandler Database handler for retrieving report data
     */
    private ReportManager(dbHandler dbHandler) {
        this.dbHandler = dbHandler;
        this.scheduler = Executors.newScheduledThreadPool(1);
        initializeReportsDirectory();
        scheduleMonthlyReportGeneration();
        System.out.println("ReportManager initialized successfully");
    }

    /**
     * Gets the singleton instance of ReportManager.
     * Creates the instance if it doesn't exist.
     * 
     * @param dbHandler Database handler instance
     * @return Singleton instance of ReportManager
     */
    public static synchronized ReportManager getInstance(dbHandler dbHandler) {
        if (instance == null) {
            instance = new ReportManager(dbHandler);
        }
        return instance;
    }

    /**
     * Creates the reports directory if it doesn't exist.
     */
    private void initializeReportsDirectory() {
        try {
            Path reportsPath = Paths.get(REPORTS_DIR);
            Files.createDirectories(reportsPath);
            System.out.println("Reports directory initialized at: " + reportsPath.toAbsolutePath());
            
            // Test write permissions
            Path testFile = reportsPath.resolve("test.txt");
            try {
                Files.writeString(testFile, "Test write access");
                Files.delete(testFile);
                System.out.println("Write access confirmed in reports directory");
            } catch (IOException e) {
                System.err.println("WARNING: No write access to reports directory: " + e.getMessage());
                throw new RuntimeException("No write access to reports directory", e);
            }
        } catch (IOException e) {
            System.err.println("Critical error creating reports directory: " + e.getMessage());
            throw new RuntimeException("Could not create reports directory", e);
        }
    }
    /**
     * Generates a unique, timestamped filename for a report.
     * 
     * @param baseFilename Base filename for the report
     * @return Unique filename with date and random identifier
     */
    private String generateTimestampedFilename(String baseFilename) {
 
    	if (baseFilename.endsWith(".ser")) {
    		baseFilename = baseFilename.substring(0,baseFilename.length()-4);
    	}
    	LocalDate today = LocalDate.now();
    	String dateStamp = today.format(FILE_NAME_FORMATTER);
    	
    	String filename = String.format("%s_%s.ser", baseFilename, dateStamp);
    	System.out.println("Generated filename: "+ filename);
    	return filename;
    	
    }

    /**
     * Saves a report to a file with a unique timestamped filename.
     * 
     * @param report The report object to save
     * @param baseFilename Base filename for the report
     * @return The full path of the saved file, or null if saving failed
     */
    public synchronized Path saveReport(Object report, String baseFilename) {
        String filename = generateTimestampedFilename(baseFilename);
        Path reportPath = Paths.get(REPORTS_DIR, filename);
        
        System.out.println("Attempting to save report to: " + reportPath.toAbsolutePath());
        
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(reportPath.toFile()))) {
            out.writeObject(report);
            System.out.println("Successfully saved report: " + filename);
            return reportPath;
        } catch (IOException e) {
            System.err.println("Failed to save report " + filename + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Loads the most recently created report for the current day.
     * 
     * @param baseFilename Base filename to search for
     * @return The loaded report object, or null if no report is found
     */
    public synchronized Object loadReport(String baseFilename) {
        LocalDate today = LocalDate.now();
        String todayStamp = today.format(FILE_NAME_FORMATTER);
        
        try {
            // Find all report files for today that match the base filename
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                    Paths.get(REPORTS_DIR), 
                    baseFilename + "_" + todayStamp + "*.ser")) {
                
                // Get the most recently created file
                Path mostRecentFile = null;
                long mostRecentTime = 0;
                
                for (Path entry : stream) {
                    long lastModifiedTime = Files.getLastModifiedTime(entry).toMillis();
                    if (lastModifiedTime > mostRecentTime) {
                        mostRecentTime = lastModifiedTime;
                        mostRecentFile = entry;
                    }
                }
                
                if (mostRecentFile != null) {
                    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(mostRecentFile.toFile()))) {
                        System.out.println("Loaded report: " + mostRecentFile.getFileName());
                        return in.readObject();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load report for " + baseFilename + ": " + e.getMessage());
        }
        
        return null;
    }

    /**
     * Checks if a report for the current day exists.
     * 
     * @param baseFilename Base filename to search for
     * @return true if a report exists for today, false otherwise
     */
    public boolean isReportCurrent(String baseFilename) {
        LocalDate today = LocalDate.now();
        String todayStamp = today.format(FILE_NAME_FORMATTER);
        
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                    Paths.get(REPORTS_DIR), 
                    baseFilename + "_" + todayStamp + "*.ser")) {
                return stream.iterator().hasNext();
            }
        } catch (IOException e) {
            System.err.println("Error checking for current report: " + e.getMessage());
            return false;
        }
    }

    /**
     * Schedules automatic monthly report generation at midnight on the first day of each month.
     */
    private void scheduleMonthlyReportGeneration() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.plusMonths(1)
                                 .withDayOfMonth(1)
                                 .withHour(0)
                                 .withMinute(0)
                                 .withSecond(0);
        
        long initialDelay = ChronoUnit.MINUTES.between(now, nextRun);
        
        scheduler.scheduleAtFixedRate(
            () -> {
                try {
                    System.out.println("Starting monthly report generation: " + LocalDateTime.now());
                    generateAllReports();
                    System.out.println("Monthly report generation completed: " + LocalDateTime.now());
                } catch (Exception e) {
                    System.err.println("Error in monthly report generation: " + e.getMessage());
                }
            },
            initialDelay,
            TimeUnit.DAYS.toMinutes(30),
            TimeUnit.MINUTES
        );
        System.out.println("Scheduled next report generation for: " + nextRun);
    }

    /**
     * Generates all required reports.
     * Includes loan duration, late return, and activity status reports.
     */
    public void generateAllReports() {
        try {
            System.out.println("Generating borrow time reports...");
            generateAndSaveLoanDurationReport();
            generateAndSaveLateReturnReport();

            System.out.println("Generating activity status reports...");
            int[] periods = {7, 14, 21, 30};
            for (int period : periods) {
                generateAndSaveActivityStatusReport(period);
            }
            System.out.println("All reports generated successfully");
            // Call the method to create notification and activity log
            dbHandler.createAutomaticReportNotification();

        } catch (Exception e) {
            System.err.println("Failed to generate reports: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates and saves loan duration report for the last 30 days.
     * Loads existing report if already generated today.
     * 
     * @throws SQLException if database query fails
     */
    private void generateAndSaveLoanDurationReport() throws SQLException {
        String baseFilename = "loan_duration_report";
        
        // Check if report already exists
        Object existingReport = loadReport(baseFilename);
        if (existingReport != null) {
            System.out.println("Loan duration report already exists for today");
            return;
        }
        
        System.out.println("Generating loan duration report...");
        ArrayList<Number> data = dbHandler.getLoanDurationReport(30);
        ChartReport report = new ChartReport(
            data,
            "Monthly Loan Duration Report",
            "LOAN_DURATION",
            0  // System-generated report
        );
        
        saveReport(report, baseFilename);
        
    }

    /**
     * Generates and saves late return report for the last 30 days.
     * Loads existing report if already generated today.
     * 
     * @throws SQLException if database query fails
     */
    private void generateAndSaveLateReturnReport() throws SQLException {
        String baseFilename = "late_return_report";
        
        // Check if report already exists
        Object existingReport = loadReport(baseFilename);
        if (existingReport != null) {
            System.out.println("Late return report already exists for today");
            return;
        }
        
        System.out.println("Generating late return report...");
        ArrayList<Number> data = dbHandler.getLateReturnReport(30);
        ChartReport report = new ChartReport(
            data,
            "Monthly Late Return Report",
            "LATE_RETURN",
            0  // System-generated report
        );
        
        saveReport(report, baseFilename);
    }

    /**
     * Generates and saves activity status report for a specific period.
     * Loads existing report if already generated today.
     * 
     * @param period The time period in days (7, 14, 21, or 30)
     * @throws SQLException if database query fails
     */
    private void generateAndSaveActivityStatusReport(int period) throws SQLException {
        String baseFilename = "activity_status_report_" + period;
        
        // Check if report already exists
        Object existingReport = loadReport(baseFilename);
        if (existingReport != null) {
            System.out.println("Activity status report already exists for today");
            return;
        }
        
        System.out.println("Generating activity status report for " + period + " days...");
        Map<String, ArrayList<Number>> statusData = dbHandler.getMemberStatusDistribution(period);
        PieChartReport report = new PieChartReport(
            statusData,
            "Activity Status Report (" + period + " days)",
            "MEMBER_STATUS_" + period,
            0  // System-generated report
        );
        
        saveReport(report, baseFilename);
    }

    /**
     * Shuts down the report manager's scheduler.
     * Ensures all scheduled tasks are completed or terminated.
     */
    public void shutdown() {
        System.out.println("Shutting down ReportManager...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            System.out.println("ReportManager shutdown completed");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            System.err.println("ReportManager shutdown interrupted: " + e.getMessage());
        }
    }
}