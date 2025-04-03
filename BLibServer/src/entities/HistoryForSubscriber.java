package entities;

import java.time.LocalDate;
import java.io.Serializable;

/**
 * Represents a subscriber's loan history record in the library system. This
 * class tracks the borrowing history of books by subscribers, including loan
 * dates, expected return dates, and actual return dates. Implements
 * Serializable for data persistence.
 * 
 */
public class HistoryForSubscriber implements Serializable {
	/** Serial version UID for serialization */
	private static final long serialVersionUID = 1L; // Required for serialization
	/** Book's unique identifier */
	private int bookId;
	/** Loan's unique identifier */
	private int loanId;

	/** Title of the book */
	private String title;

	/** Subscriber's ID */
	private int subscriberId;

	/** Date when the book was loaned */
	private LocalDate LoanDate;

	/** Expected return date */
	private LocalDate returnDate;

	/** Actual date when the book was returned */
	private LocalDate actualReturn;

	/**
	 * Constructs a new loan history record with the specified details.
	 *
	 * @param bookId     Book's identifier(barcode)
	 * @param title      Title of the book
	 * @param loanId     the id of loan
	 * @param subID      Subscriber's identifier
	 * @param loanDate   Date when the book was loaned
	 * @param returnDate Expected return date
	 * @param Actual     Actual date when the book was returned
	 */
	public HistoryForSubscriber(int bookId, int loanId, String title, int subID, LocalDate loanDate,
			LocalDate returnDate, LocalDate Actual) {
		this.bookId = bookId;
		this.loanId = loanId;
		this.title = title;
		this.subscriberId = subID;
		this.returnDate = returnDate;
		this.LoanDate = loanDate;
		this.actualReturn = Actual;
	}

	// Getters and Setters

	/**
	 * Gets the book's identifier.
	 * 
	 * @return The book ID
	 */
	public int getBookId() {
		return bookId;
	}

	/**
	 * Sets the book's identifier.
	 * 
	 * @param bookId The new book ID
	 */
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	/**
	 * Gets the loan's identifier.
	 * 
	 * @return The loan ID
	 */
	public int getLoanId() {
		return loanId;
	}

	/**
	 * Sets the loan's identifier.
	 * 
	 * @param loanId The unique identifier for the loan transaction
	 */
	public void setLoanId(int loanId) {
		this.loanId = loanId;
	}

	/**
	 * Gets the book's title.
	 * 
	 * @return The title of the book
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the book's title.
	 * 
	 * @param title The new title
	 */
	public void setTitle(String title) {
		this.title = title;
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

	/*----------------------------------------------*/
	/* LOAN DATE */
	/**
	 * Gets the loan date.
	 * 
	 * @return The date when the book was loaned
	 */
	public LocalDate getLoanDate() {
		return LoanDate;
	}

	/**
	 * Sets the loan date.
	 * 
	 * @param loanDate The new loan date
	 */
	public void setLoanDate(LocalDate loanDate) {
		this.LoanDate = loanDate;
	}

	/* RETURN DATE */
	/**
	 * Gets the expected return date.
	 * 
	 * @return The expected return date
	 */
	public LocalDate getReturnDate() {
		return returnDate;
	}

	/**
	 * Sets the expected return date.
	 * 
	 * @param retuneDate The new expected return date
	 */
	public void setReturnDate(LocalDate retuneDate) {
		this.returnDate = retuneDate;
	}

	/* ACTUAL DATE */
	/**
	 * Gets the actual return date.
	 * 
	 * @return The actual date when the book was returned
	 */
	public LocalDate getActualReturn() {
		return actualReturn;
	}

	/**
	 * Sets the actual return date.
	 * 
	 * @param Actual The new actual return date
	 */
	public void setActualReturn(LocalDate Actual) {
		this.actualReturn = Actual;
	}

}
