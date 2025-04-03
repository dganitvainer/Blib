package entities;

import enums.ReservationStatus;

/**
 * Represents a book reservation in the library system. This class manages book
 * reservations made by subscribers, including reservation dates, expiration
 * dates, and the current status of the reservation.
 * 
 * @author
 */
public class Reservation {
	/** An identifier for the reservation */
	private int reservationId;

	/** ID of the subscriber who made the reservation */
	private int subscriberId;

	/** ID of the reserved book */
	private int bookId;

	/** Date when the reservation was made */
	private String reservationDate;

	/** Date when the reservation expires */
	private String expirationDate;

	/** Current status of the reservation */
	private ReservationStatus status;

	/**
	 * Constructs a new Reservation with the specified details.
	 *
	 * @param reservationId   An identifier for the reservation
	 * @param subscriberId    ID of the subscriber making the reservation
	 * @param bookId          ID of the reserved book
	 * @param reservationDate Date when the reservation was made
	 * @param expirationDate  Date when the reservation expires
	 * @param status          Current status of the reservation
	 */
	public Reservation(int reservationId, int subscriberId, int bookId, String reservationDate, String expirationDate,
			ReservationStatus status) {
		this.reservationId = reservationId;
		this.subscriberId = subscriberId;
		this.bookId = bookId;
		this.reservationDate = reservationDate;
		this.expirationDate = expirationDate;
		this.status = status;
	}

	// Getters and Setters
	/**
	 * Gets the reservation's unique identifier.
	 * 
	 * @return The reservation ID
	 */
	public int getReservationId() {
		return reservationId;
	}

	/**
	 * Sets the reservation's unique identifier.
	 * 
	 * @param reservationId The new reservation ID
	 */
	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}

	/**
	 * Gets the subscriber's unique identifier.
	 * 
	 * @return The subscriber ID
	 */
	public int getSubscriberId() {
		return subscriberId;
	}

	/**
	 * Sets the subscriber's unique identifier.
	 * 
	 * @param subscriberId The new subscriber ID
	 */
	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	/**
	 * Gets the book's unique identifier.
	 * 
	 * @return The book ID
	 */
	public int getBookId() {
		return bookId;
	}

	/**
	 * Sets the book's unique identifier.
	 * 
	 * @param bookId The new book ID
	 */
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	/**
	 * Gets the reservation date.
	 * 
	 * @return The date when the reservation was made
	 */
	public String getReservationDate() {
		return reservationDate;
	}

	/**
	 * Sets the reservation date.
	 * 
	 * @param reservationDate The new reservation date
	 */
	public void setReservationDate(String reservationDate) {
		this.reservationDate = reservationDate;
	}

	/**
	 * Gets the expiration date.
	 * 
	 * @return The date when the reservation expires
	 */
	public String getExpirationDate() {
		return expirationDate;
	}

	/**
	 * Sets the expiration date.
	 * 
	 * @param expirationDate The new expiration date
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * Gets the reservation status.
	 * 
	 * @return The current status of the reservation
	 */
	public ReservationStatus getStatus() {
		return status;
	}

	/**
	 * Sets the reservation status.
	 * 
	 * @param status The new reservation status
	 */
	public void setStatus(ReservationStatus status) {
		this.status = status;
	}
}