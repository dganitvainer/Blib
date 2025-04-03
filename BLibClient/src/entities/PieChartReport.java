package entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

/**
 * Represents a pie chart report in the library system. This class handles data
 * for generating pie charts with period-based data analysis. It includes
 * functionality for tracking different time periods and maintaining data
 * validity. Implements Serializable for data persistence.
 * 
 * @author
 */
public class PieChartReport implements Serializable {
	/** Serial version UID for serialization */
	private static final long serialVersionUID = 1L;

	/** Map containing chart data organized by time periods */
	private Map<String, ArrayList<Number>> periodChartData;

	/** Title of the chart */
	private String chartTitle;

	/** Type of report being generated */
	private String reportType;

	/** Timestamp when the report was generated */
	private LocalDateTime generationTime;

	/** ID of the librarian who generated the report */
	private int librarianId;

	/**
	 * Constructs a new PieChartReport with the specified parameters.
	 *
	 * @param periodChartData Map of period-based chart data
	 * @param chartTitle      Title of the chart
	 * @param reportType      Type of report being generated
	 * @param librarianId     ID of the librarian generating the report
	 */
	public PieChartReport(Map<String, ArrayList<Number>> periodChartData, String chartTitle, String reportType,
			int librarianId) {
		this.periodChartData = periodChartData;
		this.chartTitle = chartTitle;
		this.reportType = reportType;
		this.generationTime = LocalDateTime.now();
		this.librarianId = librarianId;
	}

	/**
	 * Gets the chart data for a specific period.
	 * 
	 * @param period The time period to retrieve data for
	 * @return ArrayList of data points for the specified period
	 */
	public ArrayList<Number> getPeriodData(String period) {
		return periodChartData.get(period);
	}

	/**
	 * Gets all period-based chart data.
	 * 
	 * @return Map containing all period-based data
	 */
	public Map<String, ArrayList<Number>> getAllPeriodData() {
		return periodChartData;
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

	// Helper methods for getting status counts for a specific period
	/**
	 * Gets the count of active subscribers for a specific period.
	 * 
	 * @param period The time period to get the count for
	 * @return The number of active subscribers in the specified period
	 */
	public int getActiveCount(String period) {
		ArrayList<Number> data = periodChartData.get(period);
		return data != null ? data.get(0).intValue() : 0;
	}

	/**
	 * Gets the count of frozen subscribers for a specific period.
	 * 
	 * @param period The time period to get the count for
	 * @return The number of frozen subscribers in the specified period
	 */
	public int getFrozenCount(String period) {
		ArrayList<Number> data = periodChartData.get(period);
		return data != null ? data.get(1).intValue() : 0;
	}

	/**
	 * Gets the chart data for the most recent period. If no data is available,
	 * returns an empty ArrayList.
	 * 
	 * @return ArrayList of data points for the most recent period
	 */
	public ArrayList<Number> getChartData() {
		String latestPeriod = periodChartData.keySet().stream().max((p1, p2) -> {
			int days1 = Integer.parseInt(p1.split(" ")[0]);
			int days2 = Integer.parseInt(p2.split(" ")[0]);
			return Integer.compare(days1, days2);
		}).orElse(null);

		return latestPeriod != null ? periodChartData.get(latestPeriod) : new ArrayList<>();
	}
}