package entities;

import java.sql.Date;

import enums.SubscriberStatus;

/**
 * Represents a historical record of subscriber status changes in the library
 * system. This class tracks when and why a subscriber's status changed,
 * maintaining a history of status transitions for audit and management
 * purposes.
 * 
 */
public class SubscriberStatusHistory {
	/** An identifier for the history record */
	private int historyId;

	/** ID of the subscriber whose status changed */
	private int subscriberId;

	/** Status that was set at the time of change */
	private SubscriberStatus status;

	/** Date when the status change occurred */
	private Date changeDate;

	/** Reason for the status change */
	private String reason;

	/**
	 * Constructs a new SubscriberStatusHistory record with the specified details.
	 *
	 * @param historyId    An identifier for this history record
	 * @param subscriberId ID of the subscriber whose status changed
	 * @param status       Status that was set
	 * @param changeDate   Date when the change occurred
	 * @param reason       Reason for the status change
	 */
	public SubscriberStatusHistory(int historyId, int subscriberId, SubscriberStatus status, Date changeDate,
			String reason) {
		this.historyId = historyId;
		this.subscriberId = subscriberId;
		this.status = status;
		this.changeDate = changeDate;
		this.reason = reason;
	}

	// Getters and Setters
	/**
	 * Gets the history record's unique identifier.
	 * 
	 * @return The history record ID
	 */
	public int getHistoryId() {
		return historyId;
	}

	/**
	 * Sets the history record's identifier.
	 * 
	 * @param historyId The new history record ID
	 */
	public void setHistoryId(int historyId) {
		this.historyId = historyId;
	}

	/**
	 * Gets the subscriber's ID.
	 * 
	 * @return The subscriber ID
	 */
	public int getSubscriberId() {
		return subscriberId;
	}

	/**
	 * Sets the subscriber's ID.
	 * 
	 * @param subscriberId The new subscriber ID
	 */
	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	/**
	 * Gets the status that was set.
	 * 
	 * @return The status at the time of change
	 */
	public SubscriberStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status for this history record.
	 * 
	 * @param status The new status
	 */
	public void setStatus(SubscriberStatus status) {
		this.status = status;
	}

	/**
	 * Gets the date when the status change occurred.
	 * 
	 * @return The change date
	 */
	public Date getChangeDate() {
		return changeDate;
	}

	/**
	 * Sets the date when the status change occurred.
	 * 
	 * @param changeDate The new change date
	 */
	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	/**
	 * Gets the reason for the status change.
	 * 
	 * @return The reason for the change
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Sets the reason for the status change.
	 * 
	 * @param reason The new reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
}
