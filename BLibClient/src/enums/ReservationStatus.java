package enums;

/**
 * Represents the status of a reservation in the library system. This enum
 * defines all possible statuses that can be set for a reservation within the
 * library management system.
 * 
 * @author
 */
public enum ReservationStatus {
	/** Represents a status for when a subscriber is still waiting for his book */
	PENDING,

	/**
	 * Represents a status for when a book is arrived and a subscriber can loan the
	 * book
	 */
	FULFILLED,

	/**
	 * Represents a status for when a subscriber didn't come to loan the book - 2
	 * days after the book's arrival
	 */
	CANCELLED
}