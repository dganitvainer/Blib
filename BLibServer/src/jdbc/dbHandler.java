package jdbc;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import enums.NotificationType;
import enums.ReservationStatus;
import entities.ActivityLog;
import entities.Book;
import entities.BookLoanDetailsDTO;
import entities.HistoryForSubscriber;
import entities.Loan;
import entities.Notification;
import entities.Reservation;
import entities.Subscriber;
import entities.SubscriberDTO;
import entities.User;
import enums.ActivityType;
import enums.SubscriberStatus;
import enums.UserType;

/**
 * The {@code BLibClient} class manages the communication between the client and
 * the server in the library system. It extends {@code AbstractClient} to handle
 * network communication, sending and receiving messages between the client
 * application and the server.
 * 
 * <p>
 * This class is responsible for processing responses from the server and
 * routing them to the appropriate controllers for handling user interface
 * actions.
 * </p>
 */
public class dbHandler {
	private Connection conn;

	/**
	 * Constructs a new database handler with the specified connection. Initializes
	 * the handler with an existing database connection to be used for subsequent
	 * database operations.
	 *
	 * @param connection An active Connection object to the database
	 * @see Connection
	 */
	public dbHandler(Connection connection) {
		this.conn = connection;
	}

	/**
	 * Retrieves the current database connection.
	 *
	 * @return The active Connection object being used by this handler
	 * @see java.sql.Connection
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * Retrieves a book's details by its ID, including loan information if
	 * applicable.
	 *
	 * @param bookId The ID of the book to fetch.
	 * @return A BookLoanDetailsDTO object containing book and loan details, or null
	 *         if not found.
	 * @throws SQLException If a database access error occurs.
	 */
	public BookLoanDetailsDTO getBookById(int bookId) throws SQLException {
		String query = """
				    SELECT b.book_id, b.title, b.author, b.subject, b.description,
				           b.total_copies, b.copies_available, b.shelf_location,
				           bb.member_id AS subscriberId, bb.return_date
				    FROM Books b
				    LEFT JOIN BookBorrowing bb ON b.book_id = bb.book_id
				    WHERE b.book_id = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String title = rs.getString("title");
					String author = rs.getString("author");
					String subject = rs.getString("subject");
					String description = rs.getString("description");
					int totalCopies = rs.getInt("total_copies");
					int copiesAvailable = rs.getInt("copies_available");
					String shelfLocation = rs.getString("shelf_location");

					// Loan details
					int subscriberId = rs.getInt("subscriberId");
					String returnDate = rs.getString("return_date");

					// Create a BookLoanDetailsDTO object
					return new BookLoanDetailsDTO(bookId, title, author, subject, description, totalCopies,
							copiesAvailable, shelfLocation, subscriberId, returnDate);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching book by ID: " + e.getMessage());
			throw e;
		}

		return null; // If no book is found
	}

	/**
	 * Retrieves a list of books by their title, including loan information if
	 * applicable.
	 *
	 * @param title The title of the book to search for.
	 * @return A list of BookLoanDetailsDTO objects matching the given title.
	 * @throws SQLException If a database access error occurs.
	 */
	public ArrayList<BookLoanDetailsDTO> getBooksByName(String title) throws SQLException {
		String query = """
				SELECT b.BookID, b.Title, b.Author, b.Subject, b.Description,
				       b.TotalCopies, b.CopiesAvailable, b.ShelfLocation,
				       l.SubscriberID, l.ReturnDate
				FROM Books b
				LEFT JOIN Loans l ON b.BookID = l.BookID
				WHERE b.Title = ?
				""";

		ArrayList<BookLoanDetailsDTO> books = new ArrayList<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, title);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int bookId = rs.getInt("BookID");
					String author = rs.getString("Author");
					String subject = rs.getString("Subject");
					String description = rs.getString("Description");
					int totalCopies = rs.getInt("TotalCopies");
					int copiesAvailable = rs.getInt("CopiesAvailable");
					String shelfLocation = rs.getString("ShelfLocation");

					// Loan details
					int subscriberId = rs.getInt("SubscriberID");
					String returnDate = rs.getString("ReturnDate");

					books.add(new BookLoanDetailsDTO(bookId, title, author, subject, description, totalCopies,
							copiesAvailable, shelfLocation, subscriberId, returnDate));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching books by title: " + e.getMessage());
			throw e;
		}

		return books;
	}

	/**
	 * Retrieves a list of books by their subject, including loan information if
	 * applicable.
	 *
	 * @param subject The subject of the books to search for.
	 * @return A list of BookLoanDetailsDTO objects matching the given subject.
	 * @throws SQLException If a database access error occurs.
	 */
	public ArrayList<BookLoanDetailsDTO> getBooksBySubject(String subject) throws SQLException {
		String query = """
				SELECT b.BookID, b.Title, b.Author, b.Subject, b.Description,
				       b.TotalCopies, b.CopiesAvailable, b.ShelfLocation,
				       l.SubscriberID, l.ReturnDate
				FROM Books b
				LEFT JOIN Loans l ON b.BookID = l.BookID
				WHERE b.Subject = ?
				""";

		ArrayList<BookLoanDetailsDTO> books = new ArrayList<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, subject);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int bookId = rs.getInt("BookID");
					String title = rs.getString("Title");
					String author = rs.getString("Author");
					String description = rs.getString("Description");
					int totalCopies = rs.getInt("TotalCopies");
					int copiesAvailable = rs.getInt("CopiesAvailable");
					String shelfLocation = rs.getString("ShelfLocation");

					// Loan details
					int subscriberId = rs.getInt("SubscriberID");
					String returnDate = rs.getString("ReturnDate");

					books.add(new BookLoanDetailsDTO(bookId, title, author, subject, description, totalCopies,
							copiesAvailable, shelfLocation, subscriberId, returnDate));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching books by subject: " + e.getMessage());
			throw e;
		}

		return books;
	}

	/**
	 * Retrieves a list of books by their description, including loan information if
	 * applicable.
	 *
	 * @param description A keyword or phrase to search for in book descriptions.
	 * @return A list of BookLoanDetailsDTO objects containing matching
	 *         descriptions.
	 * @throws SQLException If a database access error occurs.
	 */
	public ArrayList<BookLoanDetailsDTO> getBooksByDescription(String description) throws SQLException {
		String query = """
				SELECT b.BookID, b.Title, b.Author, b.Subject, b.Description,
				       b.TotalCopies, b.CopiesAvailable, b.ShelfLocation,
				       l.SubscriberID, l.ReturnDate
				FROM Books b
				LEFT JOIN Loans l ON b.BookID = l.BookID
				WHERE b.Description LIKE ?
				""";

		ArrayList<BookLoanDetailsDTO> books = new ArrayList<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, "%" + description + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int bookId = rs.getInt("BookID");
					String title = rs.getString("Title");
					String author = rs.getString("Author");
					String subject = rs.getString("Subject");
					String desc = rs.getString("Description");
					int totalCopies = rs.getInt("TotalCopies");
					int copiesAvailable = rs.getInt("CopiesAvailable");
					String shelfLocation = rs.getString("ShelfLocation");

					// Loan details
					int subscriberId = rs.getInt("SubscriberID");
					String returnDate = rs.getString("ReturnDate");

					books.add(new BookLoanDetailsDTO(bookId, title, author, subject, desc, totalCopies, copiesAvailable,
							shelfLocation, subscriberId, returnDate));

					if (!rs.isBeforeFirst()) {
						System.out.println("No rows found for description: " + description);
					}
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching books by description: " + e.getMessage());
			throw e;
		}

		return books;
	}

	/**
	 * Checks if a username exists in the database.
	 *
	 * @param userId The ID of the user to check.
	 * @return true if the username exists, false otherwise.
	 * @throws SQLException If a database access error occurs.
	 */
	public boolean isUsernameExists(int userId) throws SQLException {
		String query = "SELECT COUNT(*) FROM Users WHERE UserID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, userId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the provided password matches the user ID.
	 *
	 * @param userId       The ID of the user.
	 * @param passwordHash The hashed password to verify.
	 * @return true if the password matches, false otherwise.
	 * @throws SQLException If a database access error occurs.
	 */
	public boolean isPasswordCorrect(int userId, String passwordHash) throws SQLException {
		String query = "SELECT COUNT(*) FROM Users WHERE UserID = ? AND PasswordHash = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, userId);
			stmt.setString(2, passwordHash);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		}
		return false;
	}

	/**
	 * Retrieves user details based on the given user ID.
	 * 
	 * @param userId the ID of the user to retrieve
	 * @return a User object containing the user's details, or null if not found
	 * @throws SQLException if a database access error occurs
	 */

	public User getUserDetails(int userId) throws SQLException {
		String query = "SELECT * FROM Users WHERE UserID = ?";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, userId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					// Create and return a User object with all fields
					return new User(rs.getInt("UserID"), // UserID
							rs.getString("FullName"), // FullName
							rs.getString("PasswordHash"), // PasswordHash
							rs.getString("Email"), // Email
							rs.getString("Phone"), // Phone
							UserType.valueOf(rs.getString("UserType")), // UserType as ENUM
							rs.getTimestamp("CreatedAt") != null ? rs.getTimestamp("CreatedAt").toString() : null, // CreatedAt
							rs.getString("Address") // Address
					);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching user details: " + e.getMessage());
			throw e;
		}

		return null; // Return null if no user is found
	}

	/**
	 * Checks if a user with the given ID or phone number exists in the database.
	 * 
	 * @param userId the ID of the user to check
	 * @param phone  the phone number to check
	 * @return true if a matching user is found, false otherwise
	 * @throws SQLException if a database access error occurs
	 */
	public boolean checkUserExists(int userId, String phone) throws SQLException {
		String query = "SELECT COUNT(*) FROM Users WHERE UserID = ? OR Phone = ?";// The query counts how many records
																					// match any of these conditions

		try (PreparedStatement stmt = conn.prepareStatement(query)) { // Create a prepared statement to prevent SQL
																		// injection

			stmt.setInt(1, userId);
			stmt.setString(2, phone);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) { // Move to first row
					return rs.getInt(1) > 0; // Get count value and check if > 0
				}
			}
		}
		return false;
	}

	/**
	 * Creates a new user and subscriber in the database.
	 * 
	 * @param user the User object containing the user's details
	 * @return true if the user was successfully created, false otherwise
	 * @throws SQLException if a database access error occurs
	 */
	public boolean createNewUser(User user) throws SQLException {
		System.out.println("Starting createNewUser for ID: " + user.getUserId());

		String insertUserQuery = """
				    INSERT INTO Users (UserID, FullName, PasswordHash, Email, Phone, UserType, CreatedAt, Address)
				    VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)
				""";
		String insertSubscriberQuery = """
				    INSERT INTO Subscribers (SubscriberID, Status)
				    VALUES (?, 'ACTIVE')
				""";

		conn.setAutoCommit(false);
		try {
			// Insert into Users table
			try (PreparedStatement userStmt = conn.prepareStatement(insertUserQuery)) {
				userStmt.setInt(1, user.getUserId());
				userStmt.setString(2, user.getFullName());
				userStmt.setString(3, user.getPassword());
				userStmt.setString(4, user.getEmail());
				userStmt.setString(5, user.getPhone());
				userStmt.setString(6, user.getUserType().toString());
				userStmt.setString(7, user.getAddress());

				int userResult = userStmt.executeUpdate();
				System.out.println("Users insert result: " + userResult);
			}

			// Insert into Subscribers table
			try (PreparedStatement subStmt = conn.prepareStatement(insertSubscriberQuery)) {
				subStmt.setInt(1, user.getUserId());
				int subResult = subStmt.executeUpdate();
				System.out.println("Subscribers insert result: " + subResult);
			}

			conn.commit();
			System.out.println("Successfully created new user and subscriber");
			return true;

		} catch (SQLException e) {
			System.err.println("Error in createNewUser: " + e.getMessage());
			e.printStackTrace();
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * Updates the details of an existing user.
	 * 
	 * @param subscriber the User object containing updated user details
	 * @return true if the update was successful, false otherwise
	 * @throws SQLException if a database access error occurs
	 */

	public boolean updateMember(User subscriber) throws SQLException {
		System.out.println(subscriber.getUserId() + "  " + subscriber.getFullName() + "  " + subscriber.getPassword()
				+ "  " + subscriber.getEmail() + "  " + subscriber.getPhone() + "  " + subscriber.getAddress());
		String query = """
				    UPDATE users
				    SET FullName = ?, PasswordHash = ?, Email = ?, Phone = ?, Address = ?
				    WHERE UserID = ?
				""";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, subscriber.getFullName());
			stmt.setString(2, subscriber.getPassword());
			stmt.setString(3, subscriber.getEmail());
			stmt.setString(4, subscriber.getPhone());
			stmt.setString(5, subscriber.getAddress());
			stmt.setInt(6, subscriber.getUserId());

			int rowsUpdated = stmt.executeUpdate();
			return rowsUpdated > 0;

		} catch (SQLException e) {
			System.err.println("Error updating user details: " + e.getMessage());
		}
		return false;
	}

	/**
	 * Retrieves the borrowing history for a given subscriber.
	 * 
	 * @param subscriber the SubscriberDTO object containing subscriber details
	 * @return a list of HistoryForSubscriber objects representing the borrow
	 *         history
	 * @throws SQLException if a database access error occurs
	 */

	public ArrayList<HistoryForSubscriber> getBorrowHistoryForSubscriber(SubscriberDTO subscriber) throws SQLException {
		ArrayList<HistoryForSubscriber> borrowHistory = new ArrayList<>();

		String query = """
				    SELECT l.LoanID, l.BookID, b.Title, l.LoanDate, l.ReturnDate, l.ActualReturnDate
				    FROM Loans l
				    JOIN Books b ON l.BookID = b.BookID
				    WHERE l.SubscriberID = ?
				    ORDER BY l.LoanDate DESC
				""";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subscriber.getUserId());

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int loanId = rs.getInt("LoanID");
					int bookId = rs.getInt("BookID");
					String title = rs.getString("Title");

					// Convert to LocalDate and add 1 day
					LocalDate loanDate = rs.getTimestamp("LoanDate").toLocalDateTime().toLocalDate().plusDays(1);
					LocalDate returnDate = rs.getTimestamp("ReturnDate").toLocalDateTime().toLocalDate().plusDays(1);
					LocalDate actualReturnDate = rs.getTimestamp("ActualReturnDate") != null
							? rs.getTimestamp("ActualReturnDate").toLocalDateTime().toLocalDate().plusDays(1)
							: null;

					System.out.println("Corrected Loan Date: " + loanDate);
					System.out.println("Corrected Return Date: " + returnDate);

					HistoryForSubscriber history = new HistoryForSubscriber(bookId, loanId, title,
							subscriber.getUserId(), loanDate, returnDate, actualReturnDate);
					borrowHistory.add(history);

					System.out.println("Fetched Loan: LoanID=" + loanId + ", BookID=" + bookId + ", Title=" + title);
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching borrow history: " + e.getMessage());
			throw e;
		}

		return borrowHistory;
	}

	/**
	 * Processes the borrowing of a book by a subscriber.
	 * 
	 * @param bookId       the ID of the book to be borrowed
	 * @param subscriberId the ID of the subscriber borrowing the book
	 * @param LibrarianId  the ID of the librarian handling the transaction
	 * @return a message indicating the result of the borrowing process
	 * @throws SQLException if a database access error occurs
	 */

	public String processBookBorrow(int bookId, int subscriberId, int LibrarianId) throws SQLException {
		System.out.println("Starting processBookBorrow for book " + bookId + " and subscriber " + subscriberId);
		conn.setAutoCommit(false);

		try {
			// First, check subscriber status
			String checkSubscriberQuery = """
					SELECT Status FROM Subscribers
					WHERE SubscriberID = ?
					""";
			PreparedStatement subStmt = conn.prepareStatement(checkSubscriberQuery);
			subStmt.setInt(1, subscriberId);
			ResultSet subRs = subStmt.executeQuery();

			if (!subRs.next()) {
				conn.rollback();
				return "Subscriber doesn't exist";
			}

			if (subRs.getString("Status").equals("FROZEN")) {
				conn.rollback();
				return "Subscriber is frozen and cannot borrow books";
			}

			// Get and validate book
			String checkBookQuery = """
					SELECT * FROM Books WHERE BookID = ?
					""";
			PreparedStatement checkStmt = conn.prepareStatement(checkBookQuery);
			checkStmt.setInt(1, bookId);
			ResultSet rs = checkStmt.executeQuery();

			if (!rs.next()) {
				conn.rollback();
				return "Book doesn't exist";
			}

			if (rs.getInt("CopiesAvailable") <= 0) {
				conn.rollback();
				return "No copies of this book are available";
			}

			// Create Book entity
			Book book = new Book(rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
					rs.getString("Subject"), rs.getString("Description"), rs.getInt("TotalCopies"),
					rs.getInt("CopiesAvailable"), rs.getString("ShelfLocation"));

			// Create Loan entity
			Loan loan = new Loan(0, subscriberId, book.getBookId(), new java.sql.Date(System.currentTimeMillis()),
					new java.sql.Date(System.currentTimeMillis() + 14L * 24L * 60L * 60L * 1000L), null);

			// Create loan record
			String createLoanQuery = """
					INSERT INTO Loans (SubscriberID, BookID, LoanDate, ReturnDate)
					VALUES (?, ?, ?, ?)
					""";
			PreparedStatement loanStmt = conn.prepareStatement(createLoanQuery);
			loanStmt.setInt(1, loan.getSubscriberId());
			loanStmt.setInt(2, loan.getBookId());
			loanStmt.setDate(3, loan.getLoanDate());
			loanStmt.setDate(4, loan.getReturnDate());
			loanStmt.executeUpdate();

			// Update book copies
			book.setCopiesAvailable(book.getCopiesAvailable() - 1);
			String updateBookQuery = """
					UPDATE Books
					SET CopiesAvailable = ?
					WHERE BookID = ?
					""";
			PreparedStatement bookStmt = conn.prepareStatement(updateBookQuery);
			bookStmt.setInt(1, book.getCopiesAvailable());
			bookStmt.setInt(2, book.getBookId());
			bookStmt.executeUpdate();

			// Create activity log
			String borrowMessage = "Successfully borrowed book: " + book.getTitle();
			ActivityLog activity = new ActivityLog(0, subscriberId, LibrarianId, book.getBookId(), ActivityType.LOAN,
					new java.sql.Timestamp(System.currentTimeMillis()), borrowMessage);

			String logQuery = """
					INSERT INTO ActivityLog
					(SubscriberID, BookID, ActivityType, ActivityDate, Message, LibrarianID)
					VALUES (?, ?, ?, ?, ?, ?)
					""";
			PreparedStatement logStmt = conn.prepareStatement(logQuery);
			logStmt.setInt(1, activity.getSubscriberId());
			logStmt.setInt(2, activity.getBookId());
			logStmt.setString(3, activity.getActivityType().toString());
			logStmt.setTimestamp(4, activity.getActivityDate());
			logStmt.setString(5, activity.getMessage());
			logStmt.setInt(6, activity.getLibrarianId());
			logStmt.executeUpdate();

			conn.commit();
			return borrowMessage;

		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * Processes the return of a book, updating loan records, book availability, and
	 * handling cases of lost books or late returns.
	 *
	 * @param bookId       The ID of the book being returned.
	 * @param subscriberId The ID of the subscriber returning the book.
	 * @param LibrarianId  The ID of the librarian handling the return.
	 * @param isLost       Indicates whether the book is lost.
	 * @return A message indicating the result of the return process.
	 * @throws SQLException If a database error occurs.
	 */
	public String processBookReturn(int bookId, int subscriberId, int LibrarianId, boolean isLost) throws SQLException {
		System.out.println("Starting processBookReturn for book " + bookId + " and subscriber " + subscriberId);
		conn.setAutoCommit(false);
		try {
			// Get active loan and book details
			String checkLoanQuery = """
					SELECT l.*, b.*
					FROM Loans l
					JOIN Books b ON l.BookID = b.BookID
					WHERE l.BookID = ? AND l.SubscriberID = ?
					AND l.ActualReturnDate IS NULL
					""";

			PreparedStatement checkStmt = conn.prepareStatement(checkLoanQuery);
			checkStmt.setInt(1, bookId);
			checkStmt.setInt(2, subscriberId);
			ResultSet rs = checkStmt.executeQuery();

			if (!rs.next()) {
				conn.rollback();
				return "No active loan found for this book and subscriber";
			}

			// Create Book entity
			Book book = new Book(rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
					rs.getString("Subject"), rs.getString("Description"), rs.getInt("TotalCopies"),
					rs.getInt("CopiesAvailable"), rs.getString("ShelfLocation"));

			// Create Loan entity and calculate days
			Loan loan = new Loan(rs.getInt("LoanID"), rs.getInt("SubscriberID"), rs.getInt("BookID"),
					rs.getDate("LoanDate"), rs.getDate("ReturnDate"), new java.sql.Date(System.currentTimeMillis()));

			long diffInDays = (loan.getActualReturnDate().getTime() - loan.getReturnDate().getTime())
					/ (1000 * 60 * 60 * 24);

			// Different handling for lost books vs regular returns
			if (isLost) {
				// For lost books: decrease total copies, don't change available copies
				book.setTotalCopies(book.getTotalCopies() - 1);
				String updateBookQuery = """
						UPDATE Books
						SET TotalCopies = ?
						WHERE BookID = ?
						""";
				PreparedStatement bookStmt = conn.prepareStatement(updateBookQuery);
				bookStmt.setInt(1, book.getTotalCopies());
				bookStmt.setInt(2, book.getBookId());
				bookStmt.executeUpdate();
			} else {
				// For regular returns: increase available copies
				book.setCopiesAvailable(book.getCopiesAvailable() + 1);
				String updateBookQuery = """
						UPDATE Books
						SET CopiesAvailable = ?
						WHERE BookID = ?
						""";
				PreparedStatement bookStmt = conn.prepareStatement(updateBookQuery);
				bookStmt.setInt(1, book.getCopiesAvailable());
				bookStmt.setInt(2, book.getBookId());
				bookStmt.executeUpdate();
			}

			// Update loan record using Loan entity
			String updateLoanQuery = """
					UPDATE Loans
					SET ActualReturnDate = ?
					WHERE LoanID = ?
					""";
			PreparedStatement loanStmt = conn.prepareStatement(updateLoanQuery);
			loanStmt.setDate(1, loan.getActualReturnDate());
			loanStmt.setInt(2, loan.getLoanId());
			loanStmt.executeUpdate();

			// Create appropriate message based on return type
			String returnMessage;
			if (isLost) {
				returnMessage = "Book reported as lost: " + book.getTitle();
			} else {
				returnMessage = "Successfully returned book: " + book.getTitle();
				if (diffInDays > 0) {
					returnMessage += " (" + diffInDays + (diffInDays == 1 ? " day" : " days") + " late)";
				}

				// Only freeze member for late returns (>7 days)
				if (diffInDays > 7) {
					String freezeQuery = """
							UPDATE Subscribers
							SET Status = 'FROZEN'
							WHERE SubscriberID = ?
							""";
					PreparedStatement freezeStmt = conn.prepareStatement(freezeQuery);
					freezeStmt.setInt(1, subscriberId);
					freezeStmt.executeUpdate();
					returnMessage += " - Note: Member has been frozen due to late return";
				}
			}

			// Create and insert ActivityLog entity for return/lost
			ActivityLog activity = new ActivityLog(0, subscriberId, LibrarianId, book.getBookId(),
					isLost ? ActivityType.OTHER : ActivityType.RETURN,
					new java.sql.Timestamp(System.currentTimeMillis()), returnMessage);

			String logQuery = """
					INSERT INTO ActivityLog
					(SubscriberID, BookID, ActivityType, ActivityDate, Message, LibrarianID)
					VALUES (?, ?, ?, ?, ?, ?)
					""";
			PreparedStatement logStmt = conn.prepareStatement(logQuery);
			logStmt.setInt(1, activity.getSubscriberId());
			logStmt.setInt(2, activity.getBookId());
			logStmt.setString(3, activity.getActivityType().toString());
			logStmt.setTimestamp(4, activity.getActivityDate());
			logStmt.setString(5, activity.getMessage());
			logStmt.setInt(6, activity.getLibrarianId());
			logStmt.executeUpdate();

			// Check for pending reservations on regular returns
			if (!isLost) {
				String checkReservationQuery = """
						SELECT * FROM Reservations
						WHERE BookID = ? AND Status = 'PENDING'
						ORDER BY ReservationDate ASC
						LIMIT 1
						""";
				PreparedStatement reservationStmt = conn.prepareStatement(checkReservationQuery);
				reservationStmt.setInt(1, bookId);
				ResultSet reservationRs = reservationStmt.executeQuery();

				if (reservationRs.next()) {
					// Create Reservation entity
					Reservation reservation = new Reservation(reservationRs.getInt("ReservationID"),
							reservationRs.getInt("SubscriberID"), book.getBookId(),
							reservationRs.getString("ReservationDate"), LocalDate.now().plusDays(2).toString(),
							ReservationStatus.FULFILLED);

					// Update reservation using entity
					String updateReservationQuery = """
							UPDATE Reservations
							SET Status = ?,
							    ExpirationDate = ?
							WHERE ReservationID = ?
							""";
					PreparedStatement updateReservationStmt = conn.prepareStatement(updateReservationQuery);
					updateReservationStmt.setString(1, reservation.getStatus().toString());
					updateReservationStmt.setString(2, reservation.getExpirationDate());
					updateReservationStmt.setInt(3, reservation.getReservationId());
					updateReservationStmt.executeUpdate();

					// Create and insert Notification entity
					String notificationMessage = "Book '" + book.getTitle()
							+ "' is now available. Please collect within 2 days.";
					Notification notification = new Notification(0, reservation.getSubscriberId(), notificationMessage,
							LocalDateTime.now().toString(), NotificationType.RESERVATION_READY);

					String createNotificationQuery = """
							INSERT INTO Notifications
							(SubscriberID, Message, NotificationDate, Type)
							VALUES (?, ?, ?, ?)
							""";
					PreparedStatement notificationStmt = conn.prepareStatement(createNotificationQuery);
					notificationStmt.setInt(1, notification.getSubscriberId());
					notificationStmt.setString(2, notification.getMessage());
					notificationStmt.setString(3, notification.getNotificationDate());
					notificationStmt.setString(4, notification.getType().toString());
					notificationStmt.executeUpdate();

					// Create and insert ActivityLog entity for notification
					ActivityLog notificationActivity = new ActivityLog(0, reservation.getSubscriberId(), LibrarianId,
							book.getBookId(), ActivityType.NOTIFICATION,
							new java.sql.Timestamp(System.currentTimeMillis()),
							"Notification sent: " + notificationMessage);

					String notificationLogQuery = """
							INSERT INTO ActivityLog
							(SubscriberID, BookID, ActivityType, ActivityDate, Message, LibrarianID)
							VALUES (?, ?, ?, ?, ?, ?)
							""";
					PreparedStatement notificationLogStmt = conn.prepareStatement(notificationLogQuery);
					notificationLogStmt.setInt(1, notificationActivity.getSubscriberId());
					notificationLogStmt.setInt(2, notificationActivity.getBookId());
					notificationLogStmt.setString(3, notificationActivity.getActivityType().toString());
					notificationLogStmt.setTimestamp(4, notificationActivity.getActivityDate());
					notificationLogStmt.setString(5, notificationActivity.getMessage());
					notificationLogStmt.setInt(6, notificationActivity.getLibrarianId());
					notificationLogStmt.executeUpdate();

					returnMessage += "\nNotification sent to waiting subscriber.";
				}
			}

			conn.commit();
			return returnMessage;

		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * Retrieves the details of a subscriber, including their status and basic user
	 * information.
	 *
	 * @param subscriberId The ID of the subscriber.
	 * @return A Subscriber object containing the subscriber's details, or null if
	 *         not found.
	 * @throws SQLException If a database error occurs.
	 */

	public Subscriber getSubscriberDetails(int subscriberId) throws SQLException {
		String query = """
				    SELECT s.SubscriberID, s.Status, u.FullName, u.Email, u.Phone, u.Address
				    FROM Subscribers s
				    INNER JOIN Users u ON s.SubscriberID = u.UserID
				    WHERE s.SubscriberID = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subscriberId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					// Retrieve subscriber details
					int id = rs.getInt("SubscriberID");
					String status = rs.getString("Status");
					String fullName = rs.getString("FullName");

					// Create and return a Subscriber object
					Subscriber subscriber = new Subscriber(id);
					subscriber.setStatus(SubscriberStatus.valueOf(status));

					// If additional user details are needed, create a wrapper object or return as a
					// map
					System.out.println("Subscriber details fetched successfully: " + fullName);
					return subscriber;
				} else {
					System.out.println("No subscriber found with ID: " + subscriberId);
					return null;
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching subscriber details: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Retrieves all activity logs from the database, including details about the
	 * librarian and book involved.
	 *
	 * @return A list of activity logs.
	 * @throws SQLException If a database error occurs.
	 */
	public ArrayList<ActivityLog> getActivityLogs() throws SQLException {
		System.out.println("Executing getActivityLogs database query");
		ArrayList<ActivityLog> logs = new ArrayList<>();
		String query = """
				    SELECT al.*, u.FullName as LibrarianName, b.Title as BookTitle
				    FROM ActivityLog al
				    LEFT JOIN Users u ON al.LibrarianID = u.UserID
				    LEFT JOIN Books b ON al.BookID = b.BookID
				    ORDER BY al.ActivityDate DESC
				""";

		try (Statement stmt = conn.createStatement()) {
			System.out.println("Executing SQL query: " + query);
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				try {
					int activityId = rs.getInt("ActivityID");
					int subscriberId = rs.getInt("SubscriberID");
					int librarianId = rs.getInt("LibrarianID");
					int bookId = rs.getInt("BookID");
					String activityTypeStr = rs.getString("ActivityType");
					Timestamp activityDate = rs.getTimestamp("ActivityDate");
					String message = rs.getString("Message");

					System.out.println("Found activity log: ID=" + activityId + ", Type=" + activityTypeStr);

					ActivityLog log = new ActivityLog(activityId, subscriberId, librarianId, bookId,
							ActivityType.valueOf(activityTypeStr), activityDate, message);
					logs.add(log);
				} catch (Exception e) {
					System.err.println("Error processing row: " + e.getMessage());
					e.printStackTrace();
				}
			}
			System.out.println("Retrieved " + logs.size() + " activity logs from database");
			return logs;
		} catch (SQLException e) {
			System.err.println("SQL Error in getActivityLogs: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Retrieves filtered activity logs based on the user type.
	 *
	 * @param filterUser The user whose activity logs should be retrieved.
	 * @return A list of filtered activity logs.
	 * @throws SQLException If a database error occurs.
	 */

	public ArrayList<ActivityLog> getFilteredActivityLogs(User filterUser) throws SQLException {
		String query = """
				    SELECT * FROM ActivityLog
				    WHERE %s = ?
				    ORDER BY ActivityDate DESC
				""";

		query = String.format(query, filterUser.getUserType() == UserType.Librarian ? "LibrarianID" : "SubscriberID");

		ArrayList<ActivityLog> logs = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, filterUser.getUserId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				ActivityLog log = new ActivityLog(rs.getInt("ActivityID"), rs.getInt("SubscriberID"),
						rs.getInt("LibrarianID"), rs.getInt("BookID"),
						ActivityType.valueOf(rs.getString("ActivityType")), rs.getTimestamp("ActivityDate"),
						rs.getString("Message"));
				logs.add(log);
			}
		}
		return logs;
	}

	/**
	 * Retrieves all registered subscribers from the database.
	 *
	 * @return A list of all subscribers.
	 * @throws SQLException If a database error occurs.
	 */

	public ArrayList<SubscriberDTO> getAllMembers() throws SQLException {
		ArrayList<SubscriberDTO> members = new ArrayList<>();
		String query = """
				    SELECT u.*, s.Status as SubscriberStatus
				    FROM Users u
				    JOIN Subscribers s ON u.UserID = s.SubscriberID
				    WHERE u.UserType = 'Subscriber'
				""";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			while (rs.next()) {
				SubscriberDTO member = new SubscriberDTO(rs.getInt("UserID"), rs.getString("FullName"),
						rs.getString("PasswordHash"), rs.getString("Email"), rs.getString("Phone"),
						SubscriberStatus.valueOf(rs.getString("SubscriberStatus")), rs.getString("Address"));
				members.add(member);
			}
			System.out.println("Retrieved " + members.size() + " members from database");
			return members;
		}
	}

	/**
	 * Extends the loan period of a book for a subscriber, ensuring no reservations
	 * or restrictions apply.
	 *
	 * @param memberId    The ID of the subscriber requesting the extension.
	 * @param bookId      The ID of the book being extended.
	 * @param librarianId The ID of the librarian approving the extension.
	 * @return A message indicating whether the extension was successful or not.
	 * @throws SQLException If a database error occurs.
	 */

	public String extendBookLoan(int memberId, int bookId, int librarianId) throws SQLException {
		System.out.println("Starting extend loan process for book " + bookId + " and subscriber " + memberId);
		conn.setAutoCommit(false);
		try {
			// First check if subscriber is frozen
			String checkStatusQuery = "SELECT Status FROM Subscribers WHERE SubscriberID = ?";
			try (PreparedStatement statusStmt = conn.prepareStatement(checkStatusQuery)) {
				statusStmt.setInt(1, memberId);
				ResultSet statusRs = statusStmt.executeQuery();
				if (statusRs.next() && statusRs.getString("Status").equals("FROZEN")) {
					conn.rollback();
					return "Cannot extend loan - Subscriber is frozen";
				}
			}

			// Check for pending reservations
			String checkReservationQuery = """
					SELECT * FROM Reservations
					WHERE BookID = ? AND Status = 'Pending'
					""";
			try (PreparedStatement resStmt = conn.prepareStatement(checkReservationQuery)) {
				resStmt.setInt(1, bookId);
				ResultSet resRs = resStmt.executeQuery();
				if (resRs.next()) {
					conn.rollback();
					return "Cannot extend loan - Book has pending reservations";
				}
			}

			// Get loan and book details
			String getLoanAndBookQuery = """
					SELECT l.*, b.*
					FROM Loans l
					JOIN Books b ON l.BookID = b.BookID
					WHERE b.BookID = ? AND l.SubscriberID = ? AND l.ActualReturnDate IS NULL
					""";

			Book book = null;
			Loan loan = null;

			try (PreparedStatement stmt = conn.prepareStatement(getLoanAndBookQuery)) {
				stmt.setInt(1, bookId);
				stmt.setInt(2, memberId);
				ResultSet rs = stmt.executeQuery();

				if (!rs.next()) {
					conn.rollback();
					return "No active loan found for this book and subscriber";
				}

				// Create Book entity
				book = new Book(rs.getInt("BookID"), rs.getString("Title"), rs.getString("Author"),
						rs.getString("Subject"), rs.getString("Description"), rs.getInt("TotalCopies"),
						rs.getInt("CopiesAvailable"), rs.getString("ShelfLocation"));

				// Create Loan entity
				loan = new Loan(rs.getInt("LoanID"), rs.getInt("SubscriberID"), rs.getInt("BookID"),
						rs.getDate("LoanDate"), rs.getDate("ReturnDate"), rs.getDate("ActualReturnDate"));

				// Calculate days until return
				long daysUntilReturn = (loan.getReturnDate().getTime() - System.currentTimeMillis())
						/ (1000 * 60 * 60 * 24);

				if (daysUntilReturn > 7) {
					conn.rollback();
					return "Cannot extend loan - More than 7 days remaining until return date";
				}
			}

			// Extend the loan
			String updateLoanQuery = """
					UPDATE Loans
					SET ReturnDate = DATE_ADD(ReturnDate, INTERVAL 7 DAY)
					WHERE SubscriberID = ? AND BookID = ? AND ActualReturnDate IS NULL
					""";
			try (PreparedStatement updateStmt = conn.prepareStatement(updateLoanQuery)) {
				updateStmt.setInt(1, memberId);
				updateStmt.setInt(2, bookId);
				int updated = updateStmt.executeUpdate();

				if (updated > 0) {
					// Create activity log
					ActivityLog activity = new ActivityLog(0, memberId, librarianId, bookId, ActivityType.EXTENSION,
							new java.sql.Timestamp(System.currentTimeMillis()),
							"Extended loan for book: " + book.getTitle() + " by 7 days");

					String logQuery = """
							INSERT INTO ActivityLog
							(SubscriberID, BookID, ActivityType, ActivityDate, Message, LibrarianID)
							VALUES (?, ?, ?, ?, ?, ?)
							""";
					try (PreparedStatement logStmt = conn.prepareStatement(logQuery)) {
						logStmt.setInt(1, activity.getSubscriberId());
						logStmt.setInt(2, activity.getBookId());
						logStmt.setString(3, activity.getActivityType().toString());
						logStmt.setTimestamp(4, activity.getActivityDate());
						logStmt.setString(5, activity.getMessage());
						logStmt.setInt(6, activity.getLibrarianId());
						logStmt.executeUpdate();
					}

					conn.commit();
					return "Successfully extended loan for book: " + book.getTitle();
				} else {
					conn.rollback();
					return "No active loan found for this book and subscriber";
				}
			}

		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * Retrieves all books from the database, including loan details if applicable.
	 *
	 * @return A list of books with their loan details.
	 * @throws SQLException If a database error occurs.
	 */

	public ArrayList<BookLoanDetailsDTO> getAllBooks() throws SQLException {
		String query = """
				SELECT b.BookID, b.Title, b.Author, b.Subject, b.Description,
				       b.TotalCopies, b.CopiesAvailable, b.ShelfLocation,
				       l.SubscriberID, l.ReturnDate
				FROM Books b
				LEFT JOIN Loans l ON b.BookID = l.BookID
				""";

		ArrayList<BookLoanDetailsDTO> books = new ArrayList<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int bookId = rs.getInt("BookID");
					String title = rs.getString("Title");
					String author = rs.getString("Author");
					String subject = rs.getString("Subject");
					String description = rs.getString("Description");
					int totalCopies = rs.getInt("TotalCopies");
					int copiesAvailable = rs.getInt("CopiesAvailable");
					String shelfLocation = rs.getString("ShelfLocation");
					int subscriberId = rs.getObject("SubscriberID") != null ? rs.getInt("SubscriberID") : 0;
					String returnDate = rs.getString("ReturnDate");

					books.add(new BookLoanDetailsDTO(bookId, title, author, subject, description, totalCopies,
							copiesAvailable, shelfLocation, subscriberId, returnDate));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching all books: " + e.getMessage());
			throw e;
		}

		return books;
	}

	/**
	 * Retrieves books written by a specific author.
	 *
	 * @param author The name of the author to filter books by.
	 * @return A list of books written by the specified author.
	 * @throws SQLException If a database error occurs.
	 */

	public ArrayList<BookLoanDetailsDTO> getBooksByAuthor(String author) throws SQLException {
		String query = """
				SELECT b.BookID, b.Title, b.Author, b.Subject, b.Description,
				       b.TotalCopies, b.CopiesAvailable, b.ShelfLocation,
				       l.SubscriberID, l.ReturnDate
				FROM Books b
				LEFT JOIN Loans l ON b.BookID = l.BookID
				WHERE b.Author LIKE ?
				""";

		ArrayList<BookLoanDetailsDTO> books = new ArrayList<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setString(1, "%" + author + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int bookId = rs.getInt("BookID");
					String title = rs.getString("Title");
					String subject = rs.getString("Subject");
					String description = rs.getString("Description");
					int totalCopies = rs.getInt("TotalCopies");
					int copiesAvailable = rs.getInt("CopiesAvailable");
					String shelfLocation = rs.getString("ShelfLocation");
					int subscriberId = rs.getInt("SubscriberID");
					String returnDate = rs.getString("ReturnDate");

					books.add(new BookLoanDetailsDTO(bookId, title, author, subject, description, totalCopies,
							copiesAvailable, shelfLocation, subscriberId, returnDate));
				}
			}
		} catch (SQLException e) {
			System.err.println("Error fetching books by author: " + e.getMessage());
			throw e;
		}

		return books;

	}

	/**
	 * Retrieves the next available user ID by finding the maximum existing ID and
	 * incrementing it.
	 *
	 * @return The next available user ID.
	 * @throws SQLException If a database error occurs.
	 */
	public int getNextAvailableUserId() throws SQLException {
		String query = "SELECT MAX(UserID) as maxId FROM Users";

		try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
			if (rs.next()) {
				int currentMaxId = rs.getInt("maxId");
				return currentMaxId + 1;
			}
			return 1000; // Start from 1000 if no users exist
		}
	}

	/*****************************************************************************************************************************/
	// functions for borrow time report //
	/*****************************************************************************************************************************/
	/**
	 * Generates a report on loan durations for books returned within the specified
	 * time frame.
	 *
	 * @param days The number of past days to include in the report.
	 * @return A list of counts representing different loan duration categories.
	 * @throws SQLException If a database error occurs.
	 */

	public ArrayList<Number> getLoanDurationReport(int days) throws SQLException {
		String query = """
				    SELECT
				        CASE
				            WHEN DATEDIFF(ActualReturnDate, LoanDate) <= 7 THEN '0-7 days'
				            WHEN DATEDIFF(ActualReturnDate, LoanDate) <= 14 THEN '8-14 days'
				            WHEN DATEDIFF(ActualReturnDate, LoanDate) <= 21 THEN '15-21 days'
				            ELSE '22+ days'
				        END AS LoanDuration,
				        COUNT(*) as Count
				    FROM Loans
				    WHERE LoanDate >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)
				    AND ActualReturnDate IS NOT NULL
				    GROUP BY LoanDuration
				    ORDER BY
				        CASE LoanDuration
				            WHEN '0-7 days' THEN 1
				            WHEN '8-14 days' THEN 2
				            WHEN '15-21 days' THEN 3
				            ELSE 4
				        END
				""";

		ArrayList<Number> durationCounts = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, days);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					durationCounts.add(rs.getInt("Count"));
				}
			}
		}
		System.out.println("Loan Duration Data: " + durationCounts);
		return durationCounts;
	}

	/**
	 * Generates a report on late returns for books within the specified time frame.
	 *
	 * @param days The number of past days to include in the report.
	 * @return A list of counts representing different late return categories.
	 * @throws SQLException If a database error occurs.
	 */

	public ArrayList<Number> getLateReturnReport(int days) throws SQLException {
		String query = """
				    SELECT
				        CASE
				            WHEN DATEDIFF(ActualReturnDate, ReturnDate) <= 0 THEN 'On Time'
				            WHEN DATEDIFF(ActualReturnDate, ReturnDate) <= 7 THEN 'Grace Period'
				            ELSE 'Overdue'
				        END AS ReturnStatus,
				        COUNT(*) as Count
				    FROM Loans
				    WHERE LoanDate >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)
				    AND ActualReturnDate IS NOT NULL
				    GROUP BY ReturnStatus
				""";

		ArrayList<Number> returnStatusCounts = new ArrayList<>();
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, days);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					returnStatusCounts.add(rs.getInt("Count"));
				}
			}
		}
		System.out.println("Late Return Data: " + returnStatusCounts);
		return returnStatusCounts;
	}

	/**
	 * Retrieves a simple book object by its ID.
	 *
	 * @param bookId The ID of the book to retrieve.
	 * @return The book object if found, otherwise null.
	 * @throws SQLException If a database error occurs.
	 */

	public Book getSimpleBookById(int bookId) throws SQLException {
		String query = """
				SELECT * FROM Books
				WHERE BookID = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String title = rs.getString("Title");
				System.out.println("\nServer: Processing book request for ID: " + bookId);
				System.out.println("Server: Book title: " + title);

				Book book = new Book(rs.getInt("BookID"), title, rs.getString("Author"), rs.getString("Subject"),
						rs.getString("Description"), rs.getInt("TotalCopies"), rs.getInt("CopiesAvailable"),
						rs.getString("ShelfLocation"));

				// Load image data with detailed debugging
				try {
					String fileName = title.replace(" ", "_") + ".jpg";
					String resourcePath = "/images/" + fileName;
					System.out.println("Server: Looking for image file: " + fileName);
					System.out.println("Server: Using resource path: " + resourcePath);

					// Try to list all available image resources
					URL resourceFolderUrl = getClass().getResource("/images");
					if (resourceFolderUrl != null) {
						System.out.println("Server: Found images folder at: " + resourceFolderUrl);
						// List all resources in the images folder
						try (InputStream is = getClass().getResourceAsStream("/images")) {
							if (is != null) {
								System.out.println("Server: Available images:");
								// List contents if possible
							}
						}
					} else {
						System.out.println("Server: Cannot find images folder in resources");
					}

					InputStream imageStream = getClass().getResourceAsStream(resourcePath);
					if (imageStream != null) {
						byte[] imageData = imageStream.readAllBytes();
						book.setImageData(imageData);
						System.out.println(
								"Server: Successfully loaded image data, size: " + imageData.length + " bytes");
						imageStream.close();
					} else {
						System.out.println("Server: Could not find image resource at: " + resourcePath);
					}
				} catch (IOException e) {
					System.out.println("Server: Error loading image: " + e.getMessage());
					e.printStackTrace();
				}

				return book;
			}
		} catch (SQLException e) {
			System.err.println("Error fetching book by ID: " + e.getMessage());
			throw e;
		}

		return null;
	}
	/*****************************************************************************************************************************/
	// end methods for borrow time report //
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	// start methods for activity status report //
	/*****************************************************************************************************************************/
	/**
	 * Retrieves the distribution of active and frozen members over specified time
	 * periods.
	 *
	 * @param days The number of past days to include in the analysis.
	 * @return A map where the keys represent time periods (e.g., "0-7", "8-14"
	 *         days), and the values are lists containing counts of active and
	 *         frozen members.
	 * @throws SQLException If a database error occurs.
	 */

	public Map<String, ArrayList<Number>> getMemberStatusDistribution(int days) throws SQLException {
		String query = """
				    WITH WeeklyStatuses AS (
				        SELECT
				            s.SubscriberID,
				            s.Status,
				            CASE
				                WHEN DATEDIFF(CURRENT_DATE, ssh.ChangeDate) <= 7 THEN '0-7'
				                WHEN DATEDIFF(CURRENT_DATE, ssh.ChangeDate) <= 14 THEN '8-14'
				                WHEN DATEDIFF(CURRENT_DATE, ssh.ChangeDate) <= 21 THEN '15-21'
				                WHEN DATEDIFF(CURRENT_DATE, ssh.ChangeDate) <= 30 THEN '22-30'
				            END as WeekRange
				        FROM Subscribers s
				        JOIN SubscriberStatusHistory ssh ON s.SubscriberID = ssh.SubscriberID
				        WHERE ssh.ChangeDate >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)
				        AND ssh.Status = 'FROZEN'
				    )
				    SELECT
				        WeekRange,
				        COUNT(DISTINCT SubscriberID) as FrozenCount,
				        (SELECT COUNT(*) FROM Subscribers) - COUNT(DISTINCT SubscriberID) as ActiveCount
				    FROM WeeklyStatuses
				    WHERE WeekRange IS NOT NULL
				    GROUP BY WeekRange
				    ORDER BY WeekRange;
				""";

		Map<String, ArrayList<Number>> periodData = new LinkedHashMap<>();
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, days);

			try (ResultSet rs = stmt.executeQuery()) {
				// Initialize data for all periods
				periodData.put("0-7", new ArrayList<>(Arrays.asList(0, 0)));
				periodData.put("8-14", new ArrayList<>(Arrays.asList(0, 0)));
				periodData.put("15-21", new ArrayList<>(Arrays.asList(0, 0)));
				periodData.put("22-30", new ArrayList<>(Arrays.asList(0, 0)));

				// Fill in actual data
				while (rs.next()) {
					String weekRange = rs.getString("WeekRange");
					int activeCount = rs.getInt("ActiveCount");
					int frozenCount = rs.getInt("FrozenCount");

					ArrayList<Number> counts = periodData.get(weekRange);
					counts.set(0, activeCount);
					counts.set(1, frozenCount);
				}
			}
		}

		return periodData;
	}

	/*****************************************************************************************************************************/
	// end methods for activity status report //
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	// start methods for Order Book report //
	/*****************************************************************************************************************************/

	/**
	 * Checks if there are no available copies of a given book.
	 *
	 * @param bookId The ID of the book to check.
	 * @return True if no copies are available, false otherwise.
	 * @throws SQLException If a database error occurs.
	 */

	public boolean isNoCopiesAvailable(int bookId) throws SQLException {
		String query = """
				    SELECT CopiesAvailable
				    FROM Books
				    WHERE BookID = ?
				""";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int copiesAvailable = rs.getInt("CopiesAvailable");
					return copiesAvailable <= 0;
				}
			}
		}
		return false;
	}

	/**
	 * Retrieves the total number of copies for a given book.
	 *
	 * @param bookId The ID of the book.
	 * @return The total number of copies available.
	 * @throws SQLException If a database error occurs.
	 */

	public int getBookTotalCopies(int bookId) throws SQLException {
		String query = "SELECT TotalCopies FROM Books WHERE BookId = ?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("TotalCopies");
			}
		}
		return 0; // Default if book is not found
	}

	/**
	 * Checks if a user has already borrowed a specific book and has not returned
	 * it.
	 *
	 * @param bookId The ID of the book.
	 * @param userId The ID of the user.
	 * @return True if the user has already borrowed the book, false otherwise.
	 * @throws SQLException If a database error occurs.
	 */

	public boolean isBookAlreadyBorrowedByUser(int bookId, int userId) throws SQLException {
		String query = """
				    SELECT COUNT(*)
				    FROM Loans
				    WHERE BookID = ? AND SubscriberID = ? AND ActualReturnDate IS NULL
				""";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);
			stmt.setInt(2, userId);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					return count > 0;
				}
			}
		}
		return false;
	}

	/**
	 * Reserves a book for a user if possible.
	 *
	 * @param userId The ID of the user making the reservation.
	 * @param bookId The ID of the book to reserve.
	 * @return True if the reservation was successful, false otherwise.
	 * @throws SQLException If a database error occurs.
	 */

	public boolean reserveBook(int userId, int bookId) throws SQLException {
		System.out.println("Attempting to reserve book with ID " + bookId + " for user " + userId);
		conn.setAutoCommit(false);

		try {
			// Step 3: Create a new reservation
			String createReservationQuery = """
					    INSERT INTO Reservations (SubscriberID, BookID, ReservationDate, ExpirationDate, Status)
					    VALUES (?, ?, CURRENT_DATE, NULL, 'PENDING')
					""";
			try (PreparedStatement createStmt = conn.prepareStatement(createReservationQuery)) {
				createStmt.setInt(1, userId);
				createStmt.setInt(2, bookId);

				int rowsInserted = createStmt.executeUpdate();
				if (rowsInserted > 0) {
					System.out.println("Reservation created successfully for user " + userId + " and book " + bookId);
				} else {
					conn.rollback();
					System.out.println("Failed to create reservation.");
					return false;
				}
			}

			// Step 4: Add an entry to ActivityLog
			String activityMessage = "User " + userId + " reserved the book with ID: " + bookId;
			String activityLogQuery = """
					    INSERT INTO ActivityLog (SubscriberID, BookID, ActivityType, ActivityDate, Message)
					    VALUES (?, ?, 'RESERVATION', CURRENT_TIMESTAMP, ?)
					""";
			try (PreparedStatement logStmt = conn.prepareStatement(activityLogQuery)) {
				logStmt.setInt(1, userId);
				logStmt.setInt(2, bookId);
				logStmt.setString(3, activityMessage);

				int rowsLogged = logStmt.executeUpdate();
				if (rowsLogged > 0) {
					System.out.println("Activity log updated successfully for reservation.");
				} else {
					conn.rollback();
					System.out.println("Failed to update activity log.");
					return false;
				}
			}

			// Commit transaction
			conn.commit();
			return true;

		} catch (SQLException e) {
			System.err.println("Error while reserving book: " + e.getMessage());
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/**
	 * Retrieves the count of pending reservations for a specific book.
	 *
	 * @param bookId The ID of the book.
	 * @return The number of pending reservations.
	 * @throws SQLException If a database error occurs.
	 */

	public int getPendingReservationsCount(int bookId) throws SQLException {
		String query = "SELECT COUNT(*) FROM Reservations WHERE BookId = ? AND Status = 'PENDING'";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, bookId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		}
		return 0; // Default if no results
	}

	/**
	 * Checks if a user has already reserved a specific book.
	 *
	 * @param subscriberId The ID of the subscriber.
	 * @param bookId       The ID of the book.
	 * @return True if the user has a pending reservation for the book, false
	 *         otherwise.
	 * @throws SQLException If a database error occurs.
	 */

	public boolean isBookAlreadyReservedByUser(int subscriberId, int bookId) throws SQLException {
		String query = """
				SELECT COUNT(*) AS count
				FROM Reservations
				WHERE SubscriberID = ? AND BookID = ? AND Status = 'PENDING'
				""";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subscriberId); // שימוש ב-SubscriberID
			stmt.setInt(2, bookId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt("count");
					System.out.println("DEBUG: Reservations count for subscriberId = " + subscriberId + ", bookId = "
							+ bookId + " is " + count);
					return count > 0; // Return true if there is at least one reservation
				}
			}
		} catch (SQLException e) {
			System.err.println("ERROR: Failed to check reservation for subscriberId = " + subscriberId + ", bookId = "
					+ bookId + ": " + e.getMessage());
			throw e;
		}

		return false; // No reservation found
	}

	/*****************************************************************************************************************************/
	// end methods for Order Book //
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	// methods for notifications //
	/*****************************************************************************************************************************/

	/**
	 * Retrieves notifications for a specific subscriber.
	 *
	 * @param subscriberId the ID of the subscriber whose notifications are
	 *                     retrieved
	 * @return a list of notifications for the given subscriber
	 * @throws SQLException if a database access error occurs
	 */
	public ArrayList<Notification> getNotificationsForMember(int subscriberId) throws SQLException {
		String query = """
				SELECT NotificationID, SubscriberID, Message, NotificationDate, Type
				FROM Notifications
				WHERE SubscriberID = ?;
				""";

		ArrayList<Notification> notifications = new ArrayList<>();

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, subscriberId);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					int notificationId = rs.getInt("NotificationID");
					String message = rs.getString("Message");
					String notificationDate = rs.getString("NotificationDate");
					NotificationType type = NotificationType.valueOf(rs.getString("Type"));

					notifications.add(new Notification(notificationId, subscriberId, message, notificationDate, type));
				}
			}
		}

		return notifications;
	}

	/**
	 * Deletes notifications based on a list of notification IDs.
	 *
	 * @param notificationIds a list of notification IDs to be deleted
	 * @return true if at least one notification was deleted, false otherwise
	 */

	public boolean deleteNotifications(ArrayList<Integer> notificationIds) {
		if (notificationIds.isEmpty())
			return false;

		String sql = "DELETE FROM Notifications WHERE NotificationID IN ("
				+ notificationIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			for (int i = 0; i < notificationIds.size(); i++) {
				stmt.setInt(i + 1, notificationIds.get(i));
			}

			int rowsDeleted = stmt.executeUpdate();
			return rowsDeleted > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*****************************************************************************************************************************/
	// end methods for notifications //
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	// method for notification return book thread //
	/*****************************************************************************************************************************/

	/**
	 * Creates return reminders for books that are due the next day.
	 *
	 * @throws SQLException if a database access error occurs
	 */

	public void createReturnReminders() throws SQLException {
		String findAndNotifyQuery = """
				    SELECT l.SubscriberID, l.BookID, l.ReturnDate, b.Title
				    FROM Loans l
				    JOIN Books b ON l.BookID = b.BookID
				    WHERE DATE(l.ReturnDate) = DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY)
				    AND l.ActualReturnDate IS NULL
				""";

		String createNotificationQuery = """
				    INSERT INTO Notifications (SubscriberID, Message, NotificationDate, Type)
				    VALUES (?, ?, CURRENT_TIMESTAMP, 'REMINDER')
				""";

		String createLogQuery = """
				    INSERT INTO ActivityLog (SubscriberID, BookID, ActivityType, ActivityDate, Message)
				    VALUES (?, ?, 'NOTIFICATION', CURRENT_TIMESTAMP, ?)
				""";

		conn.setAutoCommit(false);
		try {
			try (PreparedStatement findStmt = conn.prepareStatement(findAndNotifyQuery);
					PreparedStatement notifyStmt = conn.prepareStatement(createNotificationQuery);
					PreparedStatement logStmt = conn.prepareStatement(createLogQuery)) {

				ResultSet rs = findStmt.executeQuery();
				int remindersCreated = 0;

				while (rs.next()) {
					int subscriberId = rs.getInt("SubscriberID");
					int bookId = rs.getInt("BookID");
					String bookTitle = rs.getString("Title");
					String message = "Reminder: The book '" + bookTitle + "' is due tomorrow";

					// Create notification
					notifyStmt.setInt(1, subscriberId);
					notifyStmt.setString(2, message);
					notifyStmt.executeUpdate();

					// Create activity log
					logStmt.setInt(1, subscriberId);
					logStmt.setInt(2, bookId);
					logStmt.setString(3, message);
					logStmt.executeUpdate();

					remindersCreated++;
				}

				conn.commit();
				System.out.println("Created " + remindersCreated + " return reminders");
			}
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/*****************************************************************************************************************************/
	// end method for notification return book thread //
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	// method for status activation thread //
	/*****************************************************************************************************************************/
	/**
	 * Processes automatic status updates for frozen subscribers. Identifies
	 * subscribers who have been frozen for 30 days or more and updates their status
	 * to active.
	 *
	 * @throws SQLException if a database access error occurs
	 */
	public void processSubscriberStatusUpdates() throws SQLException {
		String findFrozenSubscribersQuery = """
				    SELECT s.SubscriberID
				    FROM Subscribers s
				    JOIN SubscriberStatusHistory ssh ON s.SubscriberID = ssh.SubscriberID
				    WHERE s.Status = 'FROZEN'
				    AND ssh.Status = 'FROZEN'
				    AND DATEDIFF(CURRENT_DATE, ssh.ChangeDate) >= 30
				    AND ssh.ChangeDate = (
				        SELECT MAX(ChangeDate)
				        FROM SubscriberStatusHistory
				        WHERE SubscriberID = s.SubscriberID
				    )
				""";

		String updateSubscriberQuery = """
				    UPDATE Subscribers
				    SET Status = 'ACTIVE'
				    WHERE SubscriberID = ?
				""";

		String insertHistoryQuery = """
				    INSERT INTO SubscriberStatusHistory
				    (SubscriberID, Status, ChangeDate, Reason)
				    VALUES (?, 'ACTIVE', CURRENT_DATE, 'Automatic status update after 30-day freeze period')
				""";

		String insertNotificationQuery = """
				    INSERT INTO Notifications
				    (SubscriberID, Message, NotificationDate, Type)
				    VALUES (?, 'Your account has been automatically reactivated after the 30-day freeze period.',
				    CURRENT_TIMESTAMP, 'OTHER')
				""";

		String insertActivityLogQuery = """
				    INSERT INTO ActivityLog
				    (SubscriberID, ActivityType, ActivityDate, Message)
				    VALUES (?, 'OTHER', CURRENT_TIMESTAMP, 'Subscriber status automatically changed to ACTIVE')
				""";

		conn.setAutoCommit(false);
		try {
			// First, find all eligible subscribers
			ArrayList<Integer> eligibleSubscribers = new ArrayList<>();
			try (Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(findFrozenSubscribersQuery)) {
				while (rs.next()) {
					eligibleSubscribers.add(rs.getInt("SubscriberID"));
				}
			}

			System.out.println("Found " + eligibleSubscribers.size() + " subscribers eligible for status update");

			// Prepare all the required PreparedStatements
			try (PreparedStatement updateSubscriberStmt = conn.prepareStatement(updateSubscriberQuery);
					PreparedStatement insertHistoryStmt = conn.prepareStatement(insertHistoryQuery);
					PreparedStatement insertNotificationStmt = conn.prepareStatement(insertNotificationQuery);
					PreparedStatement insertActivityLogStmt = conn.prepareStatement(insertActivityLogQuery)) {

				// Process each eligible subscriber
				for (Integer subscriberId : eligibleSubscribers) {
					try {
						// Update subscriber status
						updateSubscriberStmt.setInt(1, subscriberId);
						updateSubscriberStmt.executeUpdate();

						// Create status history record
						insertHistoryStmt.setInt(1, subscriberId);
						insertHistoryStmt.executeUpdate();

						// Create notification
						insertNotificationStmt.setInt(1, subscriberId);
						insertNotificationStmt.executeUpdate();

						// Log the activity
						insertActivityLogStmt.setInt(1, subscriberId);
						insertActivityLogStmt.executeUpdate();

						System.out.println("Successfully updated status for subscriber: " + subscriberId);
					} catch (SQLException e) {
						System.err.println("Error processing subscriber " + subscriberId + ": " + e.getMessage());
						throw e; // Re-throw to trigger rollback
					}
				}
			}

			conn.commit();
			System.out.println("Successfully completed status updates for all eligible subscribers");
		} catch (SQLException e) {
			System.err.println("Error in processSubscriberStatusUpdates: " + e.getMessage());
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/*****************************************************************************************************************************/
	// method for status activation thread //
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
	// method for auto report message thread //
	/*****************************************************************************************************************************/

	/**
	 * Creates an automatic report notification in the system.
	 *
	 * @throws SQLException if a database access error occurs
	 */

	public void createAutomaticReportNotification() throws SQLException {
		String createNotificationQuery = """
				    INSERT INTO Notifications (SubscriberID, Message, NotificationDate, Type)
				    VALUES ( 0,?, CURRENT_TIMESTAMP, 'OTHER')
				""";

		String createLogQuery = """
				    INSERT INTO ActivityLog (SubscriberID,ActivityType, ActivityDate, Message)
				    VALUES (0,'OTHER', CURRENT_TIMESTAMP, ?)
				""";

		String message = "The system generated Automatic reports, generated on "
				+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		conn.setAutoCommit(false);
		try (PreparedStatement notifyStmt = conn.prepareStatement(createNotificationQuery);
				PreparedStatement logStmt = conn.prepareStatement(createLogQuery)) {

			// Create notification
			notifyStmt.setString(1, message);
			notifyStmt.executeUpdate();

			// Create activity log
			logStmt.setString(1, message);
			logStmt.executeUpdate();

			conn.commit();
			System.out.println("Automatic reports notification created");
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.setAutoCommit(true);
		}
	}

	/*****************************************************************************************************************************/
//                                       method for auto report message thread                                               //
	/*****************************************************************************************************************************/

	/*****************************************************************************************************************************/
//                                       method for auto reservation thread                                                  //
	/*****************************************************************************************************************************/

	/**
	 * Processes expired reservations and handles all associated database
	 * operations. This includes updating reservation statuses, book availability,
	 * creating notifications, and logging activities.
	 *
	 * @throws SQLException if any database operation fails
	 */
	public void processExpiredReservations() throws SQLException {
		Connection conn = null;
		PreparedStatement pstmtExpired = null;
		PreparedStatement pstmtUpdateReservation = null;
		PreparedStatement pstmtUpdateBook = null;
		PreparedStatement pstmtCreateNotification = null;
		PreparedStatement pstmtCreateLog = null;
		PreparedStatement pstmtNextReservation = null;

		try {
			conn = getConnection();
			conn.setAutoCommit(false); // Start transaction

			// 1. Find expired fulfilled reservations
			String findExpiredSQL = "SELECT r.ReservationID, r.SubscriberID, r.BookID, b.Title "
					+ "FROM Reservations r " + "JOIN Books b ON r.BookID = b.BookID " + "WHERE r.Status = 'FULFILLED' "
					+ "AND r.ExpirationDate <= CURDATE()";

			pstmtExpired = conn.prepareStatement(findExpiredSQL);
			ResultSet expiredRs = pstmtExpired.executeQuery();

			while (expiredRs.next()) {
				int reservationId = expiredRs.getInt("ReservationID");
				int subscriberId = expiredRs.getInt("SubscriberID");
				int bookId = expiredRs.getInt("BookID");
				String bookTitle = expiredRs.getString("Title");

				// 2. Update reservation status to CANCELLED
				String updateReservationSQL = "UPDATE Reservations " + "SET Status = 'CANCELLED' "
						+ "WHERE ReservationID = ?";

				pstmtUpdateReservation = conn.prepareStatement(updateReservationSQL);
				pstmtUpdateReservation.setInt(1, reservationId);
				pstmtUpdateReservation.executeUpdate();

				// 3. Increment book's available copies
				String updateBookSQL = "UPDATE Books " + "SET CopiesAvailable = CopiesAvailable + 1 "
						+ "WHERE BookID = ?";

				pstmtUpdateBook = conn.prepareStatement(updateBookSQL);
				pstmtUpdateBook.setInt(1, bookId);
				pstmtUpdateBook.executeUpdate();

				// 4. Create cancellation notification
				String createNotificationSQL = "INSERT INTO Notifications (SubscriberID, Message, Type) "
						+ "VALUES (?, ?, 'REMINDER')";

				String cancelMessage = String.format("Your reservation for '%s' has expired and been cancelled.",
						bookTitle);

				pstmtCreateNotification = conn.prepareStatement(createNotificationSQL);
				pstmtCreateNotification.setInt(1, subscriberId);
				pstmtCreateNotification.setString(2, cancelMessage);
				pstmtCreateNotification.executeUpdate();

				// 5. Log the cancellation
				String createLogSQL = "INSERT INTO ActivityLog (SubscriberID, ActivityType, BookID, Message) "
						+ "VALUES (?, 'RESERVATION', ?, ?)";

				String logMessage = String.format("Reservation cancelled due to expiration for book: %s", bookTitle);

				pstmtCreateLog = conn.prepareStatement(createLogSQL);
				pstmtCreateLog.setInt(1, subscriberId);
				pstmtCreateLog.setInt(2, bookId);
				pstmtCreateLog.setString(3, logMessage);
				pstmtCreateLog.executeUpdate();

				// 6. Check for next pending reservation
				String findNextReservationSQL = "SELECT r.ReservationID, r.SubscriberID " + "FROM Reservations r "
						+ "WHERE r.BookID = ? " + "AND r.Status = 'PENDING' " + "ORDER BY r.ReservationDate ASC "
						+ "LIMIT 1";

				pstmtNextReservation = conn.prepareStatement(findNextReservationSQL);
				pstmtNextReservation.setInt(1, bookId);
				ResultSet nextRs = pstmtNextReservation.executeQuery();

				if (nextRs.next()) {
					int nextSubscriberId = nextRs.getInt("SubscriberID");
					int nextReservationId = nextRs.getInt("ReservationID");

					// Update next reservation to FULFILLED
					String updateNextReservationSQL = "UPDATE Reservations " + "SET Status = 'FULFILLED', "
							+ "ExpirationDate = DATE_ADD(CURDATE(), INTERVAL 3 DAY) " + "WHERE ReservationID = ?";

					pstmtUpdateReservation = conn.prepareStatement(updateNextReservationSQL);
					pstmtUpdateReservation.setInt(1, nextReservationId);
					pstmtUpdateReservation.executeUpdate();

					// Create notification for next subscriber
					String nextMessage = String.format(
							"The book '%s' is now available for pickup. Please collect within 3 days.", bookTitle);

					pstmtCreateNotification = conn.prepareStatement(createNotificationSQL);
					pstmtCreateNotification.setInt(1, nextSubscriberId);
					pstmtCreateNotification.setString(2, nextMessage);
					pstmtCreateNotification.executeUpdate();

					// Log the notification
					String nextLogMessage = String.format("Book '%s' now available for next reservation holder",
							bookTitle);

					pstmtCreateLog = conn.prepareStatement(createLogSQL);
					pstmtCreateLog.setInt(1, nextSubscriberId);
					pstmtCreateLog.setInt(2, bookId);
					pstmtCreateLog.setString(3, nextLogMessage);
					pstmtCreateLog.executeUpdate();
				}
			}

			conn.commit(); // Commit transaction
			System.out.println("Successfully processed expired reservations");

		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					System.err.println("Error rolling back transaction: " + ex.getMessage());
				}
			}
			throw e;
		} finally {
			// Close all prepared statements
			if (pstmtExpired != null)
				pstmtExpired.close();
			if (pstmtUpdateReservation != null)
				pstmtUpdateReservation.close();
			if (pstmtUpdateBook != null)
				pstmtUpdateBook.close();
			if (pstmtCreateNotification != null)
				pstmtCreateNotification.close();
			if (pstmtCreateLog != null)
				pstmtCreateLog.close();
			if (pstmtNextReservation != null)
				pstmtNextReservation.close();

			if (conn != null) {
				conn.setAutoCommit(true); // Reset auto-commit
				conn.close();
			}
		}
	}

	/**
	 * Retrieves all activity logs for a specific subscriber from the database.
	 * Fetches detailed activity information including librarian interactions, book
	 * activities, and associated messages, ordered by activity date.
	 *
	 * @param userId The subscriber's unique identifier
	 * @return {@code ArrayList<ActivityLog>} A list of activity logs associated
	 *         with the subscriber
	 * @throws SQLException If a database access error occurs or the SQL query fails
	 * 
	 * @see ActivityLog
	 * @see ActivityType
	 */
	public ArrayList<ActivityLog> getSubscriberActivityLogs(int userId) throws SQLException {
		System.out.println("Executing getSubscriberActivityLogs for user ID: " + userId);

		ArrayList<ActivityLog> logs = new ArrayList<>();

		String query = """
				SELECT al.ActivityID, al.SubscriberID, al.LibrarianID, al.BookID, al.ActivityType,
				       al.ActivityDate, al.Message
				FROM ActivityLog al
				WHERE al.SubscriberID = ?
				ORDER BY al.ActivityDate DESC
				""";

		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, userId); // Bind the user ID to the query
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				int activityId = rs.getInt("ActivityID");
				int subscriberId = rs.getInt("SubscriberID");
				int librarianId = rs.getInt("LibrarianID");
				int bookId = rs.getInt("BookID");
				String activityTypeStr = rs.getString("ActivityType");
				Timestamp activityDate = rs.getTimestamp("ActivityDate");
				String message = rs.getString("Message");

				ActivityLog log = new ActivityLog(activityId, subscriberId, librarianId, bookId,
						ActivityType.valueOf(activityTypeStr), activityDate, message);

				logs.add(log);
			}

			System.out.println("Retrieved " + logs.size() + " activity logs for user ID: " + userId);
			return logs;

		} catch (SQLException e) {
			System.err.println("SQL Error in getSubscriberActivityLogs: " + e.getMessage());
			throw e;
		}
	}

	/*****************************************************************************************************************************/
	// method for auto reservation thread //
	/*****************************************************************************************************************************/

}
