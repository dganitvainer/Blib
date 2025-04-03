package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Represents a chart report in the library system. This class handles data for
 * generating charts and reports, particularly for loan duration and late return
 * analytics. Implements Serializable for data persistence.
 * 
 */
public class ChartReport implements Serializable {
	/** Serial version UID for serialization */
	private static final long serialVersionUID = 1L;
	
	/** Data points for the chart */
	private ArrayList<Number> chartData;
	
	/** Title of the chart */
	private String chartTitle;
	
	/** Type of report (LOAN_DURATION or LATE_RETURN) */
	private String reportType;
	
	/** Timestamp when the report was generated */
	private LocalDateTime generationTime;
	
	/** ID of the librarian who generated the report */
	private int librarianId;

	/**
	 * Constructs a new ChartReport with the specified parameters.
	 *
	 * @param chartData   List of data points for the chart
	 * @param chartTitle  Title of the chart
	 * @param reportType  Type of report (LOAN_DURATION or LATE_RETURN)
	 * @param librarianId ID of the librarian generating the report
	 */
	public ChartReport(ArrayList<Number> chartData, String chartTitle, String reportType, int librarianId) {
		this.chartData = chartData;
		this.chartTitle = chartTitle;
		this.reportType = reportType;
		this.generationTime = LocalDateTime.now();
		this.librarianId = librarianId;
	}

	/**
	 * Gets the chart data points.
	 * 
	 * @return ArrayList of data points for the chart
	 */
	public ArrayList<Number> getChartData() {
		return chartData;
	}

	/**
	 * Gets the chart title.
	 * 
	 * @return The title of the chart
	 */
	public String getChartTitle() {
		return chartTitle;
	}

	/**
	 * Gets the report type.
	 * 
	 * @return The type of report
	 */
	public String getReportType() {
		return reportType;
	}

	/**
	 * Gets the generation timestamp.
	 * 
	 * @return The time when the report was generated
	 */
	public LocalDateTime getGenerationTime() {
		return generationTime;
	}

	/**
	 * Gets the librarian ID.
	 * 
	 * @return The ID of the librarian who generated the report
	 */
	public int getLibrarianId() {
		return librarianId;
	}

	/**
	 * Checks if the report is still valid. A report is considered valid for 24
	 * hours after its generation.
	 * 
	 * @return true if the report is still valid, false otherwise
	 */
	public boolean isStillValid() {
		return generationTime.plusDays(1).isAfter(LocalDateTime.now());
	}
}
