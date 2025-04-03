package member;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import client.ClientController;
import enums.Commands;
import entities.HistoryForSubscriber;
import entities.Message;
import entities.SubscriberDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
/**
 * The ViewBorrowedBooksController class manages the user interface for viewing the borrowed book history 
 * of a logged-in member. It handles displaying the member's loan history in a table, extending book loans, 
 * and navigating back to the member menu.
 * This controller interacts with the server to load and display the history of books borrowed by the logged-in member.
 * It also allows the user to request an extension for a book loan and handles the server's response for the extension request.
 */
public class ViewBorrowedBooksController {

	@FXML
	private TableView<HistoryForSubscriber> borrowHistoryTable;
	@FXML
	private TableColumn<HistoryForSubscriber, String> bookIdColumn;
	@FXML
	private TableColumn<HistoryForSubscriber, String> titleColumn;
	@FXML
	private TableColumn<HistoryForSubscriber, String> loanIdColumn;
	@FXML
	private TableColumn<HistoryForSubscriber, String> borrowDateColumn;
	@FXML
	private TableColumn<HistoryForSubscriber, String> returnDateColumn;
	@FXML
	private TableColumn<HistoryForSubscriber, String> actualRetDateColumn;
	@FXML
	private Button exitButton;
	@FXML
	private Button extendBtn;

	private ObservableList<HistoryForSubscriber> bookLoanDetails;

	private SubscriberDTO loggedMember; // logged in member obj

	/**
	 *  initialize the table
	 */
	@FXML
	public void initialize() {
		System.out.println("ViewBorrowedBooksController initialized.");
		setupLoanHistoryTable();
		
		

	}
	/**
	 * loads book loan history of the logged in subscriber
	 */
	private void loadBookLoanHistory() {
		if (loggedMember == null) {
	        System.err.println("Error: Attempted to load history with a null loggedMember!");
	        return;
	    }

	    System.out.println("Attempting to load history for member ID: " + loggedMember.getUserId());
		try {
			Message getMessage = new Message(loggedMember, Commands.GetBorrowHistory);
			ClientController.client.sendMessageToServer(getMessage);
			System.out.println("history request sent to server" + loggedMember.getUserId());
		} catch (Exception e) {
			System.err.println("Error sending history request: " + e.getMessage());
			e.printStackTrace();
		}

	}
	/**
	 * sets up the history table, bind value to column
	 */
	
	private void setupLoanHistoryTable() {
		System.out.println("Setting up history table");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
		loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("loanId"));
		borrowDateColumn.setCellValueFactory(cellData -> 
        new SimpleStringProperty(
            cellData.getValue().getLoanDate() != null ? cellData.getValue().getLoanDate().format(formatter) : ""
        )
    );
		returnDateColumn.setCellValueFactory(cellData -> 
        new SimpleStringProperty(
            cellData.getValue().getReturnDate() != null ? cellData.getValue().getReturnDate().format(formatter) : ""
        )
    );
		actualRetDateColumn.setCellValueFactory(cellData -> 
        new SimpleStringProperty(
            cellData.getValue().getActualReturn() != null ? cellData.getValue().getActualReturn().format(formatter) : ""
        )
    );
		bookLoanDetails = FXCollections.observableArrayList();
		borrowHistoryTable.setItems(bookLoanDetails);

		loadBookLoanHistory();
	}

	/**
	 * set the logged-in member and populates the table.
	 *
	 * @param loggedMember The logged-in Member object.
	 */
	public void setLoggedMember(SubscriberDTO loggedMember) {
		this.loggedMember = loggedMember;

		if (this.loggedMember != null) {
			System.out.println("Logged member set: " + this.loggedMember.getUserId());
			loadBookLoanHistory(); 
		} else {
			System.err.println("Error: Logged member is NULL when loading history!");
		}
		System.out.println("did it print it?");
	}
	
	/**
	 * handles exit button - returns to Member Menu (previous screen)
	 * @throws Exception
	 */

	@FXML
	private void handleExit() throws Exception {
		// Load MemberMenu.fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/member/MemberMenu.fxml"));
		Parent root = loader.load();

		// Pass the loggedMember back to MemberMenuController
		MemberMenuController memberMenuController = loader.getController();
		memberMenuController.setLoggedMember(loggedMember);

		// Show the MemberMenu in a new stage
		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Member Menu");
		stage.show();

		// Close the current ViewBorrowedBooks window
		Stage currentStage = (Stage) exitButton.getScene().getWindow();
		currentStage.close();
	}
	/**
	 * handle extend request by the subscriber.
	 * checks if the loan can be extended and sends request to server
	 */

	@FXML
	private void handleExtend() {
	    // Get the selected book from the table
	    HistoryForSubscriber selectedLoan = borrowHistoryTable.getSelectionModel().getSelectedItem();

	    if (selectedLoan == null) {
	        System.out.println("Error: No book selected. Please select a book to extend.");
	        return;
	    }

	    try {
	        // Get necessary details
	        int memberId = loggedMember.getUserId(); // The logged-in member ID
	        int bookId = selectedLoan.getBookId(); // Book ID from selected row

	        // Validate input
	        if (bookId <= 0) {
	            System.out.println("Error: Invalid book ID selected.");
	            return;
	        }

	        // Create Object[] with null librarianId (since a member is making the request)
	        Object[] extensionData = { memberId, bookId, 0 };

	        // Create the message object
	        Message extensionMsg = new Message(extensionData, Commands.ExtendBookLoan);

	        // Send the message to the server
	        ClientController.client.sendMessageToServer(extensionMsg);

	        System.out.println("Extension request sent: Member ID = " + memberId + ", Book ID = " + bookId);

	    } catch (Exception e) {
	        System.err.println("Error sending extension request: " + e.getMessage());
	        e.printStackTrace();
	    }
	    
	}
    /**
     * Handles response from the server regarding a loan extension.
     * Displays a success message if the extension was approved or an error message otherwise.
     *
     * @param response The response message containing loan extension details.
     */
	public void handleExtensionResponse(Message response) {
	    if (response.getObj() instanceof String) {
	        String result = (String) response.getObj();
	        System.out.println("Loan extension response received: " + result);

	        if (result.startsWith("Successfully")) {
	        	showSuccess(result);
	            System.out.println("Loan successfully extended.");
	            loadBookLoanHistory(); // Refresh the loan history table
	        } else {
	        	showError("Loan Extension Failed", result); // Display error message
	            System.out.println("Extension failed: " + result);
	        }
	    } else {
	        System.err.println("Invalid response type received for loan extension.");
	        showError("Error", "Invalid response received from server.");
	    }
	}
	
	/**
	 * gets list of loan history by logged in member.
	 * sets the data into the table and refreshes it to present loans.
	 * @param history The history of the borrower activites. 
	 */
	public void setBorrowHistoryList(ArrayList<HistoryForSubscriber> history) {
		System.out.println(
				"Received activity logs response. Number of logs: " + (history != null ? history.size() : "null"));
		Platform.runLater(() -> {
			if (history != null) {
				bookLoanDetails.clear();
				bookLoanDetails.addAll(history);
				borrowHistoryTable.refresh(); 
				System.out.println("history added to table");
			} else {
				System.err.println("Received null activity logs");
			}
		});

	}
	
	private void showSuccess(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	private void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
