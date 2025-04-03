package entities;

import java.io.Serializable;
import java.sql.Timestamp;

import enums.ActivityType;

/**
 * Represents a log entry for activities performed in the library system. This
 * class tracks various activities related to books, subscribers, and
 * librarians. Each activity log entry contains information about who performed
 * the action, what action was performed, and when it occurred.
 */
public class ActivityLog implements Serializable {
	/** Serial version UID for serialization */
	private static final long serialVersionUID = 1L;

	/** Unique identifier for the activity log entry */
	private int activityId;

	/** ID of the subscriber involved in the activity */
	private int subscriberId;

	/** ID of the librarian who performed or supervised the activity */
	private int librarianId;

	/** ID of the book involved in the activity */
	private int bookId;

	/** Type of activity performed (e.g., CHECKOUT, RETURN, etc.) */
	private ActivityType activityType;

	/** Timestamp when the activity occurred */
	private Timestamp activityDate;

	/** Additional details or notes about the activity */
	private String message;

	/**
	 * Constructs a new ActivityLog with the specified parameters.
	 * 
	 * @param activityId   An identifier for the activity log entry
	 * @param subscriberId ID of the subscriber involved in the activity
	 * @param librarianId  ID of the librarian who performed or supervised the
	 *                     activity
	 * @param bookId       ID(barcode) of the book involved in the activity
	 * @param activityType Type of activity performed (e.g., LOAN, RETURN, etc.)
	 * @param activityDate Timestamp when the activity occurred
	 * @param message      Additional details or notes about the activity
	 */
	public ActivityLog(int activityId, int subscriberId, int librarianId, int bookId, ActivityType activityType,
			Timestamp activityDate, String message) {
		this.activityId = activityId;
		this.subscriberId = subscriberId;
		this.bookId = bookId;
		this.activityType = activityType;
		this.activityDate = activityDate;
		this.message = message;
		this.librarianId = librarianId;
	}

	/**
	 * Gets the activity ID.
	 * 
	 * @return the identifier for the activity log entry
	 */
	public int getActivityId() {
		return activityId;
	}

	/**
	 * Sets the activity ID.
	 * 
	 * @param activityId The new activity ID to set
	 */
	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	/**
	 * Gets the subscriber ID.
	 * 
	 * @return The ID of the subscriber involved in this activity
	 */
	public int getSubscriberId() {
		return subscriberId;
	}

	/**
	 * Sets the subscriber ID.
	 * 
	 * @param subscriberId The new subscriber ID to set
	 */
	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	/**
	 * Gets the book ID.
	 * 
	 * @return The ID of the book involved in this activity
	 */
	public int getBookId() {
		return bookId;
	}

	/**
	 * Sets the book ID.
	 * 
	 * @param bookId The new book ID to set
	 */
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	/**
	 * Gets the activity type.
	 * 
	 * @return The type of activity performed
	 */
	public ActivityType getActivityType() {
		return activityType;
	}

	/**
	 * Sets the activity type.
	 * 
	 * @param activityType The new activity type to set
	 */
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}

	/**
	 * Gets the activity date.
	 * 
	 * @return The timestamp when this activity occurred
	 */
	public Timestamp getActivityDate() {
		return activityDate;
	}

	/**
	 * Sets the activity date.
	 * 
	 * @param activityDate The new activity date to set
	 */
	public void setActivityDate(Timestamp activityDate) {
		this.activityDate = activityDate;
	}

	/**
	 * Gets the activity message.
	 * 
	 * @return The additional details or notes about this activity
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the activity message.
	 * 
	 * @param message The new message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Gets the librarian ID.
	 * 
	 * @return The ID of the librarian who handled this activity
	 */
	public int getLibrarianId() {
		return librarianId;
	}

	/**
	 * Sets the librarian ID.
	 * 
	 * @param librarianId The new librarian ID to set
	 */
	public void setLibrarianId(int librarianId) {
		this.librarianId = librarianId;
	}

}