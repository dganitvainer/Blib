package entities;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) for book loan details. This class combines book
 * information with loan-specific details for efficient data transfer.
 * Implements Serializable for network transmission and persistence.
 * 
 */
public class BookLoanDetailsDTO implements Serializable {
	/** Serial version UID for serialization */
	private static final long serialVersionUID = 1L;

	/** Book's identifier */
	private int bookId;
	
	/** Title of the book */
	private String title;
	
	/** Author of the book */
	private String author;
	
	/** Subject or category of the book */
	private String subject;
	
	/** Detailed description of the book */
	private String description;
	
	/** Total number of copies owned by the library */
	private int totalCopies;
	
	/** Number of copies currently available for loan */
	private int copiesAvailable;
	
	/** Physical location of the book in the library */
	private String shelfLocation;
	
	/** ID of the subscriber who borrowed the book */
	private int subscriberId;
	
	/** Expected return date of the book */
	private String returnDate;

	/**
	 * Constructs a new BookLoanDetailsDTO with the specified details.
	 *
	 * @param bookId          Book's identifier(barcode)
	 * @param title           Title of the book
	 * @param author          Author of the book
	 * @param subject         Subject of the book
	 * @param description     Detailed description of the book
	 * @param totalCopies     Total number of copies owned by the library
	 * @param copiesAvailable Number of copies currently available
	 * @param shelfLocation   Physical location of the book
	 * @param subscriberId    ID of the subscriber who borrowed the book
	 * @param returnDate      Expected return date of the book
	 */
	public BookLoanDetailsDTO(int bookId, String title, String author, String subject, String description,
			int totalCopies, int copiesAvailable, String shelfLocation, int subscriberId, String returnDate) {
		this.bookId = bookId;
		this.title = title;
		this.author = author;
		this.subject = subject;
		this.description = description;
		this.totalCopies = totalCopies;
		this.copiesAvailable = copiesAvailable;
		this.shelfLocation = shelfLocation;
		this.subscriberId = subscriberId;
		this.returnDate = returnDate;
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
	 * Gets the book's author.
	 * 
	 * @return The author of the book
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Sets the book's author.
	 * 
	 * @param author The new author
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Gets the book's subject.
	 * 
	 * @return The subject of the book
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Sets the book's subject.
	 * 
	 * @param subject The new subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * Gets the book's description.
	 * 
	 * @return The description of the book
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the book's description.
	 * 
	 * @param description The new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the total number of copies owned by the library.
	 * 
	 * @return The total number of copies
	 */
	public int getTotalCopies() {
		return totalCopies;
	}

	/**
	 * Sets the total number of copies owned by the library.
	 * 
	 * @param totalCopies The new total number of copies
	 */
	public void setTotalCopies(int totalCopies) {
		this.totalCopies = totalCopies;
	}

	/**
	 * Gets the number of copies currently available for loan.
	 * 
	 * @return The number of available copies
	 */
	public int getCopiesAvailable() {
		return copiesAvailable;
	}

	/**
	 * Sets the number of copies currently available for loan.
	 * 
	 * @param copiesAvailable The new number of available copies
	 */
	public void setCopiesAvailable(int copiesAvailable) {
		this.copiesAvailable = copiesAvailable;
	}

	/**
	 * Gets the book's shelf location in the library.
	 * 
	 * @return The shelf location
	 */
	public String getShelfLocation() {
		return shelfLocation;
	}

	/**
	 * Sets the book's shelf location in the library.
	 * 
	 * @param shelfLocation The new shelf location
	 */
	public void setShelfLocation(String shelfLocation) {
		this.shelfLocation = shelfLocation;
	}

	/**
	 * Gets the ID of the subscriber who borrowed the book.
	 * 
	 * @return The subscriber ID
	 */
	public int getSubscriberId() {
		return subscriberId;
	}

	/**
	 * Sets the ID of the subscriber who borrowed the book.
	 * 
	 * @param subscriberId The new subscriber ID
	 */
	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
	}

	/**
	 * Gets the expected return date of the book.
	 * 
	 * @return The return date
	 */
	public String getReturnDate() {
		return returnDate;
	}

	/**
	 * Sets the expected return date of the book.
	 * 
	 * @param returnDate The new return date
	 */
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}
}
