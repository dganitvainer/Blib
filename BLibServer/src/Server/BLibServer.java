package Server;

import java.io.IOException;
import java.sql.SQLException;
import jdbc.dbHandler;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import java.util.ArrayList;
import java.util.Map;
import controller.ServerPortController;
import entities.HistoryForSubscriber;
import entities.User;
import enums.Commands;
import enums.UserType;
import entities.ActivityLog;
import entities.Book;
import entities.BookLoanDetailsDTO;
import entities.ChartReport;
import entities.Message;
import entities.Notification;
import entities.PieChartReport;
import entities.Subscriber;
import entities.SubscriberDTO;

/**
 * The main server class that handles all client-server communication for the
 * library system. This server manages client connections, processes client
 * requests, and coordinates various services like reports, notifications, and
 * status updates.
 */

public class BLibServer extends AbstractServer {
	/** List of all connected clients */
	public static ArrayList<ConnectionToClient> clients; // list of all connected clients
	/** Database connection handler */
	public static dbHandler dbHandler;
	/** Controller for the server's user interface */
	public static ServerPortController serverPortController; // Controller for the server's user interface
	private ReportManager reportManager; // using thread for generating and managing reports in the start of a month
	private NotificationService notificationService; // using thread for sending returns nessages daily
	private StatusUpdateService statusUpdateService; // using thread for changing
	private ReservationExpiryService reservationExpiryService; // using thread to manage book reservation expiry

	/**
	 * Constructs a new BLib server instance on the specified port.
	 *
	 * @param port The port number on which the server will listen
	 */

	public BLibServer(int port) {
		super(port);
		clients = new ArrayList<>();
	}

	/**
	 * Sets up the database connection and starts all required services. This method
	 * initializes: - Report generation service - Notification handling service -
	 * User status update service - Reservation expiry service
	 * 
	 * @param dbHandler The database connection handler to use
	 */
	public void setDbHandlerController(dbHandler dbHandler) {
		BLibServer.dbHandler = dbHandler;
		if (reportManager == null) {
			this.reportManager = ReportManager.getInstance(dbHandler);
			System.out.println("ReportManager initialized");
		}
		if (notificationService == null) {
			this.notificationService = NotificationService.getInstance(dbHandler);
			System.out.println("NotificationService initialized");
		}
		if (statusUpdateService == null) {
			this.statusUpdateService = StatusUpdateService.getInstance(dbHandler);
			System.out.println("StatusUpdateService initialized");
		}
		if (reservationExpiryService == null) { // New initialization
			this.reservationExpiryService = ReservationExpiryService.getInstance(dbHandler);
			System.out.println("ReservationExpiryService initialized");
		}
	}

	/**
	 * Sets the controller for the server's user interface.
	 * 
	 * @param serverPortController The UI controller to use
	 */

	public void setServerPortController(ServerPortController serverPortController) {
		BLibServer.serverPortController = serverPortController;
	}

	/**
	 * Gets the service that handles report generation.
	 * 
	 * @return The report manager service
	 */
	public ReportManager getReportManager() {
		return reportManager;
	}

	/**
	 * Gets the service that handles system notifications.
	 * 
	 * @return The notification service
	 */

	public NotificationService getNotificationService() {
		return notificationService;
	}

	/**
	 * Gets the service that handles user status updates.
	 * 
	 * @return The status update service
	 */

	public StatusUpdateService getStatusUpdateService() {
		return statusUpdateService;
	}

	/**
	 * Gets the service that manages book reservation expiry.
	 * 
	 * @return The reservation expiry service
	 */

	public ReservationExpiryService getReservationExpiryService() {
		return reservationExpiryService;
	}

	/**
	 * Processes messages received from clients. This method checks if the message
	 * is valid and handles any errors that occur during message processing.
	 * 
	 * @param msg    The message received from the client
	 * @param client The client that sent the message
	 */

	@Override
	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		if (!(msg instanceof Message)) {
			try {
				client.sendToClient(new Message("Error: Invalid message format.", Commands.MainManu));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}

		Message m = (Message) msg;
		Commands command = m.getCmd(); // Using enum Commands

		try {
			switch (command) {
			case ConnectClient:
				synchronized (clients) {
					clients.add(client);
				}
				System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
				if (serverPortController != null) {
					serverPortController.addClientToTable(client);
				}
				break;

			case CheckUsername:
				try {
					// Log that the CheckUsername case has started
					System.out.println("Starting CheckUsername case...");

					// Retrieve the user object from the message
					User user = (User) m.getObj();
					System.out.println("Received User ID: " + user.getUserId());

					// Check if the username exists in the database
					boolean usernameExists = dbHandler.isUsernameExists(user.getUserId());
					System.out.println("Username exists: " + usernameExists);

					if (!usernameExists) {
						// Username does not exist
						System.out.println("Username not found.");
						client.sendToClient(new Message("username not found", Commands.CheckUsername));
					} else {
						// Check if the password is correct
						boolean passwordCorrect = dbHandler.isPasswordCorrect(user.getUserId(), user.getPassword());
						System.out.println("Password correct: " + passwordCorrect);

						if (!passwordCorrect) {
							// Password is incorrect
							System.out.println("Incorrect password.");
							client.sendToClient(new Message("incorrect password", Commands.CheckUsername));
						} else {
							// Retrieve complete user details from the database
							System.out.println("Fetching complete user details...");
							User completeUser = dbHandler.getUserDetails(user.getUserId());

							if (completeUser != null) {
								System.out.println("User details retrieved: " + completeUser.getFullName()
										+ ", UserType: " + completeUser.getUserType());

								if (completeUser.getUserType().equals(UserType.Subscriber)) {
									// Fetch additional subscriber details
									System.out.println("Fetching subscriber details...");
									Subscriber subscriber = dbHandler.getSubscriberDetails(user.getUserId());
									SubscriberDTO subscriberDTO = new SubscriberDTO(completeUser.getUserId(),
											completeUser.getFullName(), completeUser.getPassword(),
											completeUser.getEmail(), completeUser.getPhone(), subscriber.getStatus(),
											completeUser.getAddress());
									System.out.println("SubscriberDTO created: " + subscriberDTO.getFullName()
											+ ", Status: " + subscriberDTO.getStatus());
									client.sendToClient(new Message(subscriberDTO, Commands.CheckUsername));
								} else {
									// Send librarian or other user type details directly
									System.out.println("Sending librarian details to client.");
									client.sendToClient(new Message(completeUser, Commands.CheckUsername));
								}
							} else {
								// User details not found
								System.out.println("Error: User details not found.");
								client.sendToClient(
										new Message("Error: User details not found.", Commands.CheckUsername));
							}
						}
					}
				} catch (SQLException e) {
					// Log database errors
					System.err.println("Database error: " + e.getMessage());
					e.printStackTrace();
					try {
						client.sendToClient(new Message("Database Error: " + e.getMessage(), Commands.CheckUsername));
					} catch (IOException ioException) {
						System.err.println("Error sending database error to client: " + ioException.getMessage());
						ioException.printStackTrace();
					}
				} catch (IOException e) {
					// Log IO errors
					System.err.println("Error sending message to client: " + e.getMessage());
					e.printStackTrace();
				}
				break;

			case GetBookByName:
				String title = (String) m.getObj();
				ArrayList<BookLoanDetailsDTO> booksByName = dbHandler.getBooksByName(title);
				client.sendToClient(new Message(booksByName, Commands.GetBookByName));
				break;

			case GetBookByTheme:
				String subject = (String) m.getObj();
				ArrayList<BookLoanDetailsDTO> booksByTheme = dbHandler.getBooksBySubject(subject);
				client.sendToClient(new Message(booksByTheme, Commands.GetBookByTheme));
				break;

			case GetBookByDescription:
				String desciption = (String) m.getObj();
				ArrayList<BookLoanDetailsDTO> booksByDescription = dbHandler.getBooksByDescription(desciption);
				client.sendToClient(new Message(booksByDescription, Commands.GetBookByDescription));
				break;

			case CreateMember:
				try {
					User newUser = (User) m.getObj();
					boolean exists = dbHandler.checkUserExists(newUser.getUserId(), newUser.getPhone());
					if (exists) {
						// Send error message instead of just false
						client.sendToClient(
								new Message("A User with this ID or phone already exists", Commands.CreateMember));
						return;
					}
					boolean success = dbHandler.createNewUser(newUser);
					if (success) {
						client.sendToClient(new Message(true, Commands.CreateMember));
					} else {
						client.sendToClient(new Message("Failed to create member", Commands.CreateMember));
					}
				} catch (SQLException e) {
					client.sendToClient(new Message("Database error: " + e.getMessage(), Commands.CreateMember));
				}
				break;

			case UpdateMember:
				try {
					System.out.println(" UPDATEUPDATE");

					SubscriberDTO dto = (SubscriberDTO) m.getObj();
					User subscriber = dbHandler.getUserDetails(dto.getUserId());
					subscriber.setFullName(dto.getFullName());
					subscriber.setEmail(dto.getEmail());
					subscriber.setPhone(dto.getPhone());
					subscriber.setAddress(dto.getAddress());
					subscriber.setPasswordHash(dto.getPassword());

					System.out.println("  Member ID: ");

					boolean updated = dbHandler.updateMember(subscriber);

					if (updated) {
						client.sendToClient(new Message("Success", Commands.UpdateMember));
					} else {
						client.sendToClient(new Message("Update failed", Commands.UpdateMember));
					}
				} catch (SQLException e) {
					client.sendToClient(new Message("Database error: " + e.getMessage(), Commands.UpdateMember));
				}
				break;

			case GetBorrowHistory:
				SubscriberDTO dto = (SubscriberDTO) m.getObj();
				try {
					ArrayList<HistoryForSubscriber> history = dbHandler.getBorrowHistoryForSubscriber(dto);
					System.out.println(history.toString());
					client.sendToClient(new Message(history, Commands.GetBorrowHistory));

				} catch (SQLException e) {
					client.sendToClient(new Message(null, Commands.GetBorrowHistory));
				}

				break;

			case GetBookNameById:
				try {
					int bookID = (int) m.getObj();
					BookLoanDetailsDTO bookDTO = dbHandler.getBookById(bookID);
					String name = bookDTO.getTitle();

					if (!name.isEmpty()) {
						client.sendToClient(new Message(name, Commands.GetBookNameById));
					}
				} catch (SQLException e) {
					client.sendToClient(new Message("Database error: " + e.getMessage(), Commands.GetBookNameById));
				}

				break;

			case ReturnBook:
				try {
					Object[] returnData = (Object[]) m.getObj();
					int bookId = Integer.parseInt((String) returnData[0]);
					int subscriberId = Integer.parseInt((String) returnData[1]);
					int librarianId = (Integer) returnData[2];
					boolean isLost = (Boolean) returnData[3]; // New flag for lost status

					String result = dbHandler.processBookReturn(bookId, subscriberId, librarianId, isLost);
					client.sendToClient(new Message(result, Commands.ReturnBook));

				} catch (Exception e) {
					System.out.println("Error in ReturnBook processing: " + e.getMessage());
					client.sendToClient(new Message("Error processing return: " + e.getMessage(), Commands.ReturnBook));
				}
				break;

			case BorrowBook:
				try {
					Object[] borrowData = (Object[]) m.getObj();
					int bookId = Integer.parseInt((String) borrowData[0]);
					int subscriberId = Integer.parseInt((String) borrowData[2]);
					int librarianId = (Integer) borrowData[3];

					String result = dbHandler.processBookBorrow(bookId, subscriberId, librarianId);
					// If the message starts with "Successfully", it was successful
					client.sendToClient(new Message(result, Commands.BorrowBook));

				} catch (Exception e) {
					System.out.println("Error in BorrowBook processing: " + e.getMessage());
					client.sendToClient(new Message("Error processing borrow: " + e.getMessage(), Commands.BorrowBook));
				}
				break;

			case GetActivityLogs:
				System.out.println("Server received GetActivityLogs request");
				try {
					if (dbHandler == null) {
						System.err.println("dbHandler is null!");
						client.sendToClient(new Message("Database handler not initialized", Commands.GetActivityLogs));
						break;
					}

					ArrayList<ActivityLog> logs = dbHandler.getActivityLogs();
					System.out.println("Server retrieved " + logs.size() + " logs");

					Message response = new Message(logs, Commands.GetActivityLogs);
					client.sendToClient(response);
					System.out.println("Server sent logs to client");

				} catch (SQLException e) {
					System.err.println("SQL error in GetActivityLogs: " + e.getMessage());
					e.printStackTrace();
					try {
						client.sendToClient(new Message("Database error: " + e.getMessage(), Commands.GetActivityLogs));
					} catch (IOException ioe) {
						System.err.println("Failed to send error to client: " + ioe.getMessage());
					}
				} catch (Exception e) {
					System.err.println("Unexpected error in GetActivityLogs: " + e.getMessage());
					e.printStackTrace();
					try {
						client.sendToClient(new Message("Server error: " + e.getMessage(), Commands.GetActivityLogs));
					} catch (IOException ioe) {
						System.err.println("Failed to send error to client: " + ioe.getMessage());
					}
				}
				break;

			case GetFilteredActivityLogs:
				System.out.println("Server received GetFilteredActivityLogs request");
				try {
					User filterUser = (User) m.getObj();
					ArrayList<ActivityLog> logs = dbHandler.getFilteredActivityLogs(filterUser);
					System.out.println("Retrieved " + logs.size() + " logs for user " + filterUser.getUserId());
					client.sendToClient(new Message(logs, Commands.GetFilteredActivityLogs));
				} catch (SQLException e) {
					System.err.println("Error in GetFilteredActivityLogs: " + e.getMessage());
					client.sendToClient(new Message("Error fetching activity logs: " + e.getMessage(),
							Commands.GetFilteredActivityLogs));
				}
				break;
			case GetAllMembers:
				System.out.println("Server received GetAllMembers request");
				try {
					ArrayList<SubscriberDTO> members = dbHandler.getAllMembers();
					System.out.println("Retrieved " + members.size() + " members from database");
					client.sendToClient(new Message(members, Commands.GetAllMembers));
				} catch (SQLException e) {
					System.err.println("Error fetching members: " + e.getMessage());
					e.printStackTrace();
					client.sendToClient(new Message(new ArrayList<>(), Commands.GetAllMembers));
				}
				break;
			case ExtendBookLoan:
				try {
					Object[] extensionData = (Object[]) m.getObj();
					int memberId = (Integer) extensionData[0];
					int bookId = (Integer) extensionData[1];
					int librarianId = (Integer) extensionData[2];

					System.out.println("Received extension request - Member ID: " + memberId + ", Book ID: " + bookId
							+ ", Librarian ID: " + librarianId);

					// Get user details
					User user = dbHandler.getUserDetails(memberId);
					if (user == null) {
						client.sendToClient(new Message("Error: User not found.", Commands.ExtendBookLoan));
						return;
					}

					String result;
					if (user.getUserType() == UserType.Librarian) {
						// Librarian extending loan (librarianId is NOT null)
						result = dbHandler.extendBookLoan(memberId, bookId, librarianId);
					} else {
						// Subscriber extending loan (librarianId should be 0)
						result = dbHandler.extendBookLoan(memberId, bookId, 0);
					}

					// Send response to the client
					client.sendToClient(new Message(result, Commands.ExtendBookLoan));
					System.out.println("Loan extension result: " + result);

				} catch (Exception e) {
					System.err.println("Error in extension processing: " + e.getMessage());
					e.printStackTrace();
					try {
						client.sendToClient(
								new Message("Error extending loan: " + e.getMessage(), Commands.ExtendBookLoan));
					} catch (IOException ioException) {
						System.err.println("Failed to send error response to client: " + ioException.getMessage());
					}
				}
				break;
			case GetAllBooks:
				try {
					ArrayList<BookLoanDetailsDTO> allBooks = dbHandler.getAllBooks();
					client.sendToClient(new Message(allBooks, Commands.GetAllBooks));
				} catch (SQLException e) {
					System.err.println("Error fetching all books: " + e.getMessage());
					try {
						client.sendToClient(new Message("Error fetching books.", Commands.GetAllBooks));
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
				break;
			case GetBookByAuthor:
				try {
					String author = (String) m.getObj();
					ArrayList<BookLoanDetailsDTO> booksByAuthor;
					booksByAuthor = dbHandler.getBooksByAuthor(author);
					client.sendToClient(new Message(booksByAuthor, Commands.GetBookByAuthor));
				} catch (SQLException e) {
					client.sendToClient(new Message("Error fetching books.", Commands.GetAllBooks));

					e.printStackTrace();
				}
				break;

			case GetLoanDurationChart:
				try {
					String filename = "loan_duration_report.ser";
					ArrayList<Number> chartData = dbHandler.getLoanDurationReport(30);

					// Create and save report
					ChartReport report = new ChartReport(chartData, "Loan Duration Report", "LOAN_DURATION", 0);
					reportManager.saveReport(report, filename);

					client.sendToClient(new Message(chartData, Commands.GetLoanDurationChart));
				} catch (SQLException | IOException e) {
					System.err.println("Error in GetLoanDurationChart: " + e.getMessage());
					client.sendToClient(new Message("Error generating loan duration report: " + e.getMessage(),
							Commands.GetLoanDurationChart));
				}
				break;
			case GetLateReturnChart:
				try {
					String filename = "late_return_report.ser";
					ArrayList<Number> chartData = dbHandler.getLateReturnReport(30);

					// Create and save report
					ChartReport report = new ChartReport(chartData, "Late Return Report", "LATE_RETURN", 0);
					reportManager.saveReport(report, filename);

					client.sendToClient(new Message(chartData, Commands.GetLateReturnChart));
				} catch (SQLException | IOException e) {
					System.err.println("Error in GetLateReturnChart: " + e.getMessage());
					client.sendToClient(new Message("Error generating late return report: " + e.getMessage(),
							Commands.GetLateReturnChart));
				}
				break;

			case GetMemberStatus:
				try {
					int period = (Integer) m.getObj();
					String filename = "activity_status_" + period + "_report.ser";
					Map<String, ArrayList<Number>> statusData = dbHandler.getMemberStatusDistribution(period);

					// Create and save report
					PieChartReport report = new PieChartReport(statusData, "Member Status Report", "MEMBER_STATUS", 0);
					reportManager.saveReport(report, filename);

					client.sendToClient(new Message(statusData, Commands.GetMemberStatus));
				} catch (SQLException | IOException e) {
					System.err.println("Error in GetMemberStatus: " + e.getMessage());
					client.sendToClient(new Message("Error generating member status report: " + e.getMessage(),
							Commands.GetMemberStatus));
				}
				break;
			case getSimpleBookById:
				try {
					int bookId = (int) m.getObj();
					Book book = dbHandler.getSimpleBookById(bookId);
					client.sendToClient(new Message(book, Commands.getSimpleBookById));
				} catch (SQLException e) {
					System.err.println("Error fetching book: " + e.getMessage());
					client.sendToClient(new Message(null, Commands.getSimpleBookById));
				} catch (IOException e) {
					System.err.println("Error sending response: " + e.getMessage());
				}
				break;
			case OrderBook:
				try {
					System.out.println("DEBUG: Starting OrderBook process...");

					Object[] orderData = (Object[]) m.getObj();
					int userId = (Integer) orderData[0];
					int bookId = (Integer) orderData[1];
					System.out.println("DEBUG: Received userId = " + userId + ", bookId = " + bookId);

					boolean alreadyReserved = dbHandler.isBookAlreadyReservedByUser(userId, bookId);
					System.out.println("DEBUG: alreadyReserved = " + alreadyReserved);

					if (alreadyReserved) {
						client.sendToClient(new Message("alreadyreserved", Commands.OrderBook)); // lowercase
						System.out.println("DEBUG: User has already reserved this book.");
						break;
					}

					boolean noCopiesAvailable = dbHandler.isNoCopiesAvailable(bookId);
					System.out.println("DEBUG: noCopiesAvailable = " + noCopiesAvailable);

					if (noCopiesAvailable) {
						int pendingReservations = dbHandler.getPendingReservationsCount(bookId);
						int totalCopies = dbHandler.getBookTotalCopies(bookId);
						System.out.println("DEBUG: pendingReservations = " + pendingReservations + ", totalCopies = "
								+ totalCopies);

						if (pendingReservations < totalCopies) {
							boolean alreadyBorrowed = dbHandler.isBookAlreadyBorrowedByUser(userId, bookId);
							System.out.println("DEBUG: alreadyBorrowed = " + alreadyBorrowed);

							if (alreadyBorrowed) {
								client.sendToClient(new Message("alreadyborrowed", Commands.OrderBook)); // lowercase
								System.out.println("DEBUG: Book already borrowed by user.");
							} else {
								boolean reservationSuccess = dbHandler.reserveBook(userId, bookId);
								System.out.println("DEBUG: reservationSuccess = " + reservationSuccess);

								if (reservationSuccess) {
									client.sendToClient(new Message("success", Commands.OrderBook)); // lowercase
									System.out.println("DEBUG: Book successfully reserved.");
								} else {
									client.sendToClient(new Message("failed", Commands.OrderBook)); // lowercase
									System.out.println("DEBUG: Failed to reserve the book.");
								}
							}
						} else {
							client.sendToClient(new Message("nocopiesavailable", Commands.OrderBook)); // lowercase
							System.out.println("DEBUG: No available copies due to too many pending reservations.");
						}
					} else {
						client.sendToClient(new Message("canborrow", Commands.OrderBook)); // lowercase
						System.out.println("DEBUG: There are options to borrow.");
					}
				} catch (SQLException e) {
					System.err.println("ERROR: Database error during OrderBook: " + e.getMessage());
					e.printStackTrace();
					try {
						client.sendToClient(new Message("databaseerror", Commands.OrderBook)); // lowercase
					} catch (IOException ioException) {
						System.err.println(
								"ERROR: Failed to send database error message to client: " + ioException.getMessage());
					}
				} catch (Exception e) {
					System.err.println("ERROR: Exception during OrderBook: " + e.getMessage());
					e.printStackTrace();
					try {
						client.sendToClient(new Message("error", Commands.OrderBook)); // lowercase
					} catch (IOException ioException) {
						System.err
								.println("ERROR: Failed to send error message to client: " + ioException.getMessage());
					}
				}
				break;
			case GetNotifications:
				try {
					int subscriberId = (int) m.getObj(); // Assume client sends SubscriberID
					ArrayList<Notification> notifications = dbHandler.getNotificationsForMember(subscriberId);
					client.sendToClient(new Message(notifications, Commands.GetNotifications));
				} catch (SQLException e) {
					System.err.println("Error fetching notifications: " + e.getMessage());
					try {
						client.sendToClient(new Message("Error fetching notifications.", Commands.GetNotifications));
					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
				break;
			case DeleteNotifications:
				try {
					@SuppressWarnings("unchecked")
					ArrayList<Integer> notificationIds = (ArrayList<Integer>) m.getObj();
					boolean deleted = dbHandler.deleteNotifications(notificationIds);
					client.sendToClient(new Message(deleted, Commands.DeleteNotifications));
				} catch (Exception e) {
					e.printStackTrace();
					client.sendToClient(new Message(false, Commands.DeleteNotifications));
				}
				break;
			case GetActivityLogsByMember:
				try {
					System.out.println("DEBUG: Starting GetActivityLogsByMember process...");

					int userId = (Integer) m.getObj();
					System.out.println("DEBUG: Received userId = " + userId);

					// Fetch activity logs for the specific user
					ArrayList<ActivityLog> logs = dbHandler.getSubscriberActivityLogs(userId);
					System.out.println("DEBUG: Retrieved " + logs.size() + " activity logs for userId = " + userId);

					// Send logs back to the client
					client.sendToClient(new Message(logs, Commands.GetActivityLogsByMember));

				} catch (SQLException e) {
					System.err.println("ERROR: Database error during GetActivityLogsByMember: " + e.getMessage());
					e.printStackTrace();
					try {
						client.sendToClient(new Message("databaseerror", Commands.GetActivityLogsByMember));
					} catch (IOException ioException) {
						System.err.println(
								"ERROR: Failed to send database error message to client: " + ioException.getMessage());
					}
				} catch (Exception e) {
					System.err.println("ERROR: Exception during GetActivityLogsByMember: " + e.getMessage());
					e.printStackTrace();
					try {
						client.sendToClient(new Message("error", Commands.GetActivityLogsByMember));
					} catch (IOException ioException) {
						System.err
								.println("ERROR: Failed to send error message to client: " + ioException.getMessage());
					}
				}
				break;

			default:
				client.sendToClient(new Message("Error: Unknown command received.", command));
				break;
			}
		} catch (SQLException e) {
			try {
				client.sendToClient(new Message("Database Error: " + e.getMessage(), command));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method overrides the one in the superclass. Called when the server
	 * starts listening for connections.
	 */
	@Override
	protected void serverStarted() {
		System.out.println("Server listening for connections on port " + getPort());
	}

	/**
	 * This method overrides the one in the superclass. Called when the server stops
	 * listening for connections.
	 */
	@Override
	protected void serverStopped() {
		System.out.println("Server has stopped listening for connections.");
	}

}
