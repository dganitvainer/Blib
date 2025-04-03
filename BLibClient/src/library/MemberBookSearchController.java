package library;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import logIn.ConnectionScreenController;
import logIn.MainMenuController;
import member.MemberMenuController;
import entities.BookLoanDetailsDTO;
import entities.Message;
import entities.SubscriberDTO;
import enums.Commands;
import client.BLibClient;
import client.ClientController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * The MemberBookSearchController class handles the search functionality for books from a member's perspective.
 * It manages the user interface elements, interactions, and communicates with the server to retrieve book information.
 */

public class MemberBookSearchController {

	@FXML
	private TableView<BookLoanDetailsDTO> bookTable;
	@FXML
	private TableColumn<BookLoanDetailsDTO, String> nameColumn;
	@FXML
	private TableColumn<BookLoanDetailsDTO, String> authorColumn;
	@FXML
	private TableColumn<BookLoanDetailsDTO, String> themeColumn;
	@FXML
	private TableColumn<BookLoanDetailsDTO, String> descriptionColumn;
	@FXML
	private TableColumn<BookLoanDetailsDTO, Integer> quantityColumn;
	@FXML
	private TableColumn<BookLoanDetailsDTO, String> locationColumn;
	@FXML
	private TableColumn<BookLoanDetailsDTO, String> returnDateColumn;

	@FXML
	private TextField searchNameField;
	@FXML
	private TextField searchThemeField;
	@FXML
	private TextField searchDescriptionField;

	@FXML
	private Button searchButton;
	@FXML
	private Button backButton;

	@FXML
	private Label ErrorLabel;

	private SubscriberDTO loggedMember;
    /**
     * Sets the logged member for the current session.
     * 
     * @param subscriber The logged-in member (SubscriberDTO).
     */

	public void setLoggedMember(SubscriberDTO subscriber) {
		this.loggedMember = subscriber;
		}
    /**
     * Starts the MemberBookSearchController by loading the FXML file and displaying the scene.
     * 
     * @param stage The primary stage where the scene will be displayed.
     * @throws IOException if there is an error loading the FXML file.
     */

	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/BookSearch.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Search A Book");
		stage.show();
	}

    /**
     * Initializes the MemberBookSearchController. Sets up the table columns and binds data to them.
     */

	@FXML
	public void initialize() {
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
		themeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject()));
		descriptionColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
		quantityColumn.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalCopies()).asObject());
		locationColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShelfLocation()));
		returnDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
				cellData.getValue().getReturnDate() != null ? cellData.getValue().getReturnDate().toString() : ""));
		authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
		BLibClient.MemberbookSearchController = this;
	}
    /**
     * Handles the search functionality for books based on the provided criteria (name, theme, or description).
     * Sends a request to the server with the search parameters.
     */

	@FXML
	private void handleSearch() {
		String name = searchNameField.getText().trim();
		String theme = searchThemeField.getText().trim();
		String description = searchDescriptionField.getText().trim();

		if (!name.isEmpty()) {
			Message message = new Message(name, Commands.GetBookByName);
			ClientController.client.sendMessageToServer(message);
		} else if (!theme.isEmpty()) {
			Message searchMessage = new Message(theme, Commands.GetBookByTheme);
			ClientController.client.sendMessageToServer(searchMessage);
		} else if (!description.isEmpty()) {
			Message searchMessage = new Message(description, Commands.GetBookByDescription);
			ClientController.client.sendMessageToServer(searchMessage);
		} else {
			ErrorLabel("Please enter a search criterion (Name, Theme, or Description).");
			return;
		}

	}


    /**
     * Handles the server response and updates the book table with the fetched book details.
     * 
     * @param books The list of books received from the server.
     */

	@FXML
	public void handleServerResponse(ArrayList<BookLoanDetailsDTO> books) {
		
		ObservableList<BookLoanDetailsDTO> bookLoanDetailsDTO = FXCollections.observableArrayList(books);
		bookTable.setItems(bookLoanDetailsDTO);
		ErrorLabel.setText("");

	}
    /**
     * Displays an error message in the ErrorLabel.
     * 
     * @param msg The error message to be displayed.
     */

	private void ErrorLabel(String msg) {
		ErrorLabel.setText(msg); // Display the message in the Label
		ErrorLabel.setVisible(true);
	}

    /**
     * Handles the exit functionality. Navigates the user back to the member menu.
     * 
     * @throws Exception if there is an error loading the MemberMenu.
     */

	@FXML
	private void handleExit() throws Exception {
		// Load MemberMenu.fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/member/MemberMenu.fxml"));
		Parent root = loader.load();

		// Pass the loggedMember back to MemberMenuController
		MemberMenuController memberMenuController = loader.getController();
		memberMenuController.setLoggedMember(loggedMember);

		// Set the scene for the current stage
		Stage currentStage = (Stage) backButton.getScene().getWindow();
		currentStage.setScene(new Scene(root));
		currentStage.setTitle("Member Menu");
	}

}
