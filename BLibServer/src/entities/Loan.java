package entities;

import java.sql.Date;

/**
 * Represents a book loan record in the library system. This class tracks the
 * details of book loans, including who borrowed the book, when it was borrowed,
 * and when it should be and was actually returned.
 * 
 */
public class Loan {
	/** An identifier for the loan */
	private int loanId;
	
	/** ID of the subscriber who borrowed the book */
	private int subscriberId;
	
	/** ID of the borrowed book */
	private int bookId;
	
	/** Date when the book was loaned */
	private Date loanDate;
	
	/** Expected return date */
	private Date returnDate;
	
	/** Actual date when the book was returned */
	private Date actualReturnDate;

	/**
	 * Constructs a new Loan with the specified details.
	 *
	 * @param loanId           An identifier for the loan
	 * @param subscriberId     ID of the subscriber who borrowed the book
	 * @param bookId           ID of the borrowed book
	 * @param loanDate         Date when the book was loaned
	 * @param returnDate       Expected return date
	 * @param actualReturnDate Actual date when the book was returned
	 */
	public Loan(int loanId, int subscriberId, int bookId, Date loanDate, Date returnDate, Date actualReturnDate) {
		this.loanId = loanId;
		this.subscriberId = subscriberId;
		this.bookId = bookId;
		this.loanDate = loanDate;
		this.returnDate = returnDate;
		this.actualReturnDate = actualReturnDate;
	}

	// Getters and Setters

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
	 * @param loanId The new loan ID
	 */
	public void setLoanId(int loanId) {
		this.loanId = loanId;
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
	 * Gets the loan date.
	 * 
	 * @return The date when the book was loaned
	 */
	public Date getLoanDate() {
		return loanDate;
	}

	/**
	 * Sets the loan date.
	 * 
	 * @param loanDate The new loan date
	 */
	public void setLoanDate(Date loanDate) {
		this.loanDate = loanDate;
	}

	/**
	 * Gets the expected return date.
	 * 
	 * @return The expected return date
	 */
	public Date getReturnDate() {
		return returnDate;
	}

	/**
	 * Sets the expected return date.
	 * 
	 * @param returnDate The new expected return date
	 */
	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	/**
	 * Gets the actual return date.
	 * 
	 * @return The actual date when the book was returned
	 */
	public Date getActualReturnDate() {
		return actualReturnDate;
	}

	/**
	 * Sets the actual return date.
	 * 
	 * @param actualReturnDate The new actual return date
	 */
	public void setActualReturnDate(Date actualReturnDate) {
		this.actualReturnDate = actualReturnDate;
	}
}