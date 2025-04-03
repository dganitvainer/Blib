package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import entities.ActivityLog;
import entities.Book;
import entities.BookLoanDetailsDTO;
import entities.HistoryForSubscriber;
import entities.Message;
import entities.Notification;
import entities.SubscriberDTO;
import enums.Commands;
import javafx.application.Platform;
import librarian.LibrarianScreenController;
import library.BookSearchController;
import library.MemberBookSearchController;
import logIn.MainMenuController;
import member.MemberMenuController;
import member.NotificationMemberController;
import member.BookSearchMemberController;
import member.MemberActivityLogsController;
import member.MemberCardController;
import member.ViewBorrowedBooksController;
import ocsf.client.AbstractClient;

/**
 * The BLibClient class manages the communication between the client and the
 * server. It handles the sending and receiving of messages using String-based
 * commands.
 */
public class BLibClient extends AbstractClient {
	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ClientController clientUI;

	/** Static controllers used for routing server responses to appropriate UI components */

	/** 
	 * Controller for managing and displaying borrowed books view.
	 * Handles functionality related to viewing and managing borrowed book records.
	 */
	static public ViewBorrowedBooksController ViewBorrowedBooksController;

	/**
	 * Controller for the main menu interface.
	 * Manages navigation and core functionality of the main application menu.
	 */
	static public MainMenuController mainMenuController;

	/**
	 * Controller for member card operations.
	 * Handles functionality related to member card management and display.
	 */
	static public MemberCardController MemberCardController;

	/**
	 * Controller for book search functionality.
	 * Manages general book search operations and results display.
	 */
	static public BookSearchController bookSearchController;

	/**
	 * Controller for librarian interface operations.
	 * Manages functionality specific to librarian users including book management
	 * and member administration.
	 */
	static public LibrarianScreenController librarianScreenController;

	/**
	 * Controller for member menu interface.
	 * Manages navigation and functionality specific to member users.
	 */
	static public MemberMenuController memberMenuController;

	/**
	 * Controller for member-specific book search operations.
	 * Handles book search functionality with member-specific features.
	 */
	public static MemberBookSearchController MemberbookSearchController;

	/**
	 * Controller for member book search interface.
	 * Manages the book search view and operations for member users.
	 */
	public static BookSearchMemberController bookSearchMemberController;

	/**
	 * Controller for member notifications.
	 * Handles display and management of notifications for member users.
	 */
	public static NotificationMemberController notificationMemberController;

	/**
	 * Controller for member activity logs.
	 * Manages the tracking and display of member activities and history.
	 */
	public static MemberActivityLogsController memberActivityLogsController;

	/**
	 * Constructs an instance of the BLibClient.
	 *
	 * @param host   The server to connect to.
	 * @param port   The port number to connect on.
	 * @param clientUI The client controller instance.
	 * @throws IOException If an error occurs during connection.
	 */
	public BLibClient(String host, int port, ClientController clientUI) throws IOException {
		super(host, port);
		this.clientUI = clientUI;
		System.out.println("Connecting to server...");
		openConnection();
	}

	/**
	 * Handles the message from the server and routes it to the appropriate
	 * controller.
	 *
	 * @param message The message received from the server.
	 */
	public void handleMessageFromServer(Object message) {
		if (!(message instanceof Message)) {
			System.err.println("Invalid message type received.");
			System.err.println("Class type: " + message.getClass().getName());
			System.err.println("Message content: " + message.toString());
			return;
		}

		Message m = (Message) message;
		Commands command = m.getCmd();

		switch (command) {
		case CheckUsername:
			// Pass to MainMenuController
			Platform.runLater(() -> {
				if (mainMenuController != null) {
					mainMenuController.openMenu(m);
				} else {
					System.err.println("MemberController is not set.");
				}
			});
			break;

		case GetBookByName:
			Platform.runLater(() -> {
				ArrayList<BookLoanDetailsDTO> books = (ArrayList<BookLoanDetailsDTO>) m.getObj();
				if (bookSearchController != null) {
					bookSearchController.handleServerResponse(books);
				} else if (bookSearchMemberController != null) {
					bookSearchMemberController.handleServerResponse(books);
				} else {
					System.err.println("Neither BookSearchController nor BookSearchMemberController is set.");
				}
			});
			break;

		case GetBookByTheme:
			Platform.runLater(() -> {
				ArrayList<BookLoanDetailsDTO> books = (ArrayList<BookLoanDetailsDTO>) m.getObj();
				if (bookSearchController != null) {
					bookSearchController.handleServerResponse(books);
				} else if (bookSearchMemberController != null) {
					bookSearchMemberController.handleServerResponse(books);
				} else {
					System.err.println("Neither BookSearchController nor BookSearchMemberController is set.");
				}
			});
			break;

		case GetBookByDescription:
			Platform.runLater(() -> {
				ArrayList<BookLoanDetailsDTO> books = (ArrayList<BookLoanDetailsDTO>) m.getObj();
				if (bookSearchController != null) {
					bookSearchController.handleServerResponse(books);
				} else if (bookSearchMemberController != null) {
					bookSearchMemberController.handleServerResponse(books);
				} else {
					System.err.println("Neither BookSearchController nor BookSearchMemberController is set.");
				}
			});
			break;
		case ClientDisconnect:
			System.out.println((String) m.getObj());
			break;
		case ReturnBook:
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					librarianScreenController.handleReturnBookResponse(m);
				}
			});
			break; ///////////////// @@@@@@@@@@@@@@@@@@@///////////////
		case BorrowBook:
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					librarianScreenController.handleBorrowBookResponse(m);
				} else {
					System.err.println("LibrarianScreenController is not set");
				}
			});
			break;

		case CreateMember:
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					librarianScreenController.handleCreateMemberResponse(m);
				} else {
					System.err.println("LibrarianScreenController is not set");
				}
			});
			break;

		case GetBorrowHistory:
			System.out.println("returned to client side cykaa");
			Platform.runLater(() -> {
				System.out.println("got instance xyn");
				if (BLibClient.ViewBorrowedBooksController != null) {
					if (m.getObj() instanceof ArrayList<?>) {
						System.out.println("controller is not null");
						ArrayList<HistoryForSubscriber> history = (ArrayList<HistoryForSubscriber>) m.getObj();
						System.out.println("Client received " + history.size() + " loans");
						BLibClient.ViewBorrowedBooksController.setBorrowHistoryList(history);
					} else {
						System.err.println("Received invalid data type");
					}
				} else {
					System.err.println("ViewBorrowedBooksController is not initialized.");
				}
			});
			break;

		case UpdateMember:
			Platform.runLater(() -> {
				if (m.getObj() instanceof String) {
					String result = (String) m.getObj();
					System.out.println("UpdateMember Response: " + result);

					if (MemberCardController != null) {
						MemberCardController.handleUpdateResponse(m);
					} else {
						System.err.println("MemberCardController is not set.");
					}
				} else {
					System.err.println("Unexpected response type for UpdateMember.");
				}
			});
			break;

		// In BLibClient.java handleMessageFromServer method
		case GetActivityLogs:
			System.out.println("Client received GetActivityLogs response");
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					if (m.getObj() instanceof ArrayList<?>) {
						ArrayList<ActivityLog> logs = (ArrayList<ActivityLog>) m.getObj();
						System.out.println("Client received " + logs.size() + " logs");
						librarianScreenController.handleActivityLogsResponse(logs);
					} else {
						System.err.println("Received invalid data type for activity logs");
					}
				} else {
					System.err.println("LibrarianScreenController is not set");
				}
			});
			break;

		case GetFilteredActivityLogs:
			System.out.println("Client received GetFilteredActivityLogs response");
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					ArrayList<ActivityLog> logs = (ArrayList<ActivityLog>) m.getObj();
					librarianScreenController.handleActivityLogsResponse(logs);
				} else {
					System.err.println("LibrarianScreenController is not set");
				}
			});
			break;
		case GetAllMembers:
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					ArrayList<SubscriberDTO> members = (ArrayList<SubscriberDTO>) m.getObj();
					librarianScreenController.handleMemberDataResponse(members);
				} else {
					System.err.println("LibrarianScreenController is not set");
				}
			});
			break;
		case ExtendBookLoan:
			Platform.runLater(() -> {
				if (!(m.getObj() instanceof String)) {
					System.err.println("Unexpected response type for ExtendBookLoan");
					return;
				}

				String result = (String) m.getObj();
				System.out.println("Loan Extension Response: " + result);

				// If librarian
				if (librarianScreenController != null) {
					librarianScreenController.handleExtensionResponse(new Message(result, Commands.ExtendBookLoan));
				}
				// If subscriber (member)
				else if (ViewBorrowedBooksController != null) {
					ViewBorrowedBooksController.handleExtensionResponse(new Message(result, Commands.ExtendBookLoan));
				}
				// No controller found
				else {
					System.err.println("No controller available to handle loan extension response.");
				}
			});
			break;
		case GetAllBooks:
			Platform.runLater(() -> {
				ArrayList<BookLoanDetailsDTO> books = (ArrayList<BookLoanDetailsDTO>) m.getObj();
				if (bookSearchController != null) {
					bookSearchController.handleServerResponse(books);
				} else if (bookSearchMemberController != null) {
					bookSearchMemberController.handleServerResponse(books);
				} else {
					System.err.println("Neither BookSearchController nor BookSearchMemberController is set.");
				}
			});
			break;
		case GetBookByAuthor:
			Platform.runLater(() -> {
				if (bookSearchController != null) {
					bookSearchController.handleServerResponse((ArrayList<BookLoanDetailsDTO>) m.getObj());
				}
			});
			break;

		case GetLoanDurationChart:
			System.out.println("Client received GetLoanDurationChart response");
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					ArrayList<Number> chartData = (ArrayList<Number>) m.getObj();
					librarianScreenController.handleLoanDurationChartResponse(chartData);
				}
			});
			break;

		case GetLateReturnChart:
			System.out.println("Client received GetLateReturnChart response");
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					ArrayList<Number> chartData = (ArrayList<Number>) m.getObj();
					librarianScreenController.handleLateReturnChartResponse(chartData);
				}
			});
			break;

		case GetMemberStatus:
			System.out.println("Client received GetMemberStatus response");
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					if (m.getObj() instanceof Map) {
						@SuppressWarnings("unchecked")
						Map<String, ArrayList<Number>> statusData = (Map<String, ArrayList<Number>>) m.getObj();
						librarianScreenController.handleMemberStatusResponse(statusData);
					} else {
						System.err.println("Invalid data type received for member status");
					}
				} else {
					System.err.println("LibrarianScreenController is not set");
				}
			});
			break;
		case getSimpleBookById:
			Platform.runLater(() -> {
				if (librarianScreenController != null) {
					Book book = (Book) m.getObj();
					librarianScreenController.handleBookResponse(book);
				} else {
					System.err.println("LibrarianScreenController is not set");
				}
			});
		case OrderBook:
			Platform.runLater(() -> {
				if (bookSearchMemberController != null) {
					String result = (String) m.getObj();
					bookSearchMemberController.handleOrderBookResponse(result);
				} else {
					System.err.println("bookSearchMemberController is not set.");
				}
			});
			break;

		case GetNotifications:
			if (notificationMemberController != null) {
				ArrayList<Notification> notifications = (ArrayList<Notification>) m.getObj();
				notificationMemberController.handleNotificationsResponse(notifications);
			} else {
				System.err.println("NotificationMemberController is not set.");
			}
			break;
		case DeleteNotifications:
			boolean deleteSuccess = (boolean) m.getObj();
			if (notificationMemberController != null) {
				notificationMemberController.handleDeleteNotificationsResponse(deleteSuccess);
			}
			break;
		case GetActivityLogsByMember:
			if (memberActivityLogsController != null) {
				ArrayList<ActivityLog> logs = (ArrayList<ActivityLog>) m.getObj();
				System.out.println("DEBUG: Client received " + logs.size() + " activity logs.");
				memberActivityLogsController.handleActivityLogsResponse(logs);
			} else {
				System.err.println("MemberActivityLogsController is not set.");
			}
			break;

		// Add more cases for different commands as needed

		default:
			System.err.println("Unhandled command: " + command);
			break;
		}
	}

	/**
	 * Sends a message to the server.
	 *
	 * @param message The message to send (Object format).
	 */
	public void sendMessageToServer(Object message) {
		try {
			sendToServer(message);
		} catch (IOException e) {
			System.err.println("Could not send message to server: " + e.getMessage());
		}
	}
	/**
	* Sets the static MemberCardController instance for the application.
	* This method updates the controller reference used for member card operations.
	*
	* @param controller The new MemberCardController instance to be used.
	*                  If null, member card operations will not be available.
	* @see MemberCardController
	*/
	public static void setMemberCardController(MemberCardController controller) {
		MemberCardController = controller;
	}

	/**
	* Closes the client connection and terminates the program.
	* Attempts to gracefully close the database connection before shutdown.
	* If connection closure fails, logs the error but continues with program termination.
	*
	* @see #closeConnection()
	*/
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
			System.err.println("Error closing connection: " + e.getMessage());
		}
		System.exit(0);
	}
	/**
	* Sets the static LibrarianScreenController instance for the application.
	* Updates the controller reference used for librarian-specific operations.
	* 
	* @param controller The new LibrarianScreenController instance to be used.
	*                  If null, librarian operations will not be available.
	* @see LibrarianScreenController
	*/
	public static void setLibrarianScreenController(LibrarianScreenController controller) {
		librarianScreenController = controller;
	}
}
