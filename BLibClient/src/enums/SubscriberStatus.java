package enums;

/**
 * Represents the status of a subscriber in the library system. This enum
 * defines all possible statuses that can be set for a subscriber within the
 * library management system.
 * 
 * @author
 */
public enum SubscriberStatus {
	/** Represents the default status of a subscriber */
	ACTIVE,

	/**
	 * Represents a status of a subscriber that returned a book one week or more
	 * overdue the return date
	 */
	FROZEN
}
