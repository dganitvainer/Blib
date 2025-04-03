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
import logIn.MainMenuController;
import entities.BookLoanDetailsDTO;
import entities.Message;
import enums.Commands;
import client.BLibClient;
import client.ClientController;

import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.Parent;
/**
 * The BookSearchController class handles the user interface and actions for searching books in the library.
 * It manages the interaction between the user input, the search functionality, and the display of search results.
 */

public class BookSearchController {
	
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
    private ComboBox<String> cmbSearchType;
    @FXML
    private TextField searchField;
    @FXML
    private Label ErrorLabel;
    

    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button refreshButton;


    /**
     * Starts the BookSearchController by loading the FXML file and displaying the scene.
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
     * Initializes the BookSearchController. Sets up the table columns and ComboBox, and populates the table with books.
     */

    @FXML
    public void initialize() {
        // Initialize the table columns
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        themeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTotalCopies()).asObject());
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShelfLocation()));
        returnDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReturnDate() != null ? cellData.getValue().getReturnDate() : ""));
		BLibClient.bookSearchController = this;
        // Populate the ComboBox with search options
        cmbSearchType.setItems(FXCollections.observableArrayList("Title", "Author", "Theme", "Description"));
        cmbSearchType.getSelectionModel().selectFirst();

        // Fetch and display all books initially
        fetchAllBooks();
    }

    /**
     * Fetches all books from the server to be displayed in the table.
     */

	private void fetchAllBooks() {
		try {
			// Send a request to the server to fetch all books
			Message message = new Message(null, Commands.GetAllBooks);
			ClientController.client.sendMessageToServer(message);
		} catch (Exception e) {
			showError("Failed to fetch books. Please try again.");
			e.printStackTrace();
		}
	}

    /**
     * Handles the search functionality based on the selected search type (Title, Author, Theme, or Description).
     * Sends a request to the server with the search parameters.
     * 
     * @param event The action event triggered when the user initiates the search.
     */

    @FXML
    private void handleSearch(ActionEvent event) {
        String selectedType = cmbSearchType.getValue();
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            showError("Please enter text to search.");
            return;
        }

        Message message;
        switch (selectedType) {
            case "Title":
                message = new Message(searchText, Commands.GetBookByName);
                break;
            case "Author":
                message = new Message(searchText, Commands.GetBookByAuthor);
                break;
            case "Theme":
                message = new Message(searchText, Commands.GetBookByTheme);
                break;
            case "Description":
                message = new Message(searchText, Commands.GetBookByDescription);
                break;
            default:
                showError("Invalid search type selected.");
                return;
        }

        // Send the message to the server
        try {
            ClientController.client.sendMessageToServer(message);
        } catch (Exception e) {
            showError("Error sending search request: " + e.getMessage());
        }
    }
    
    /**
     * Displays an error message to the user.
     * 
     * @param msg The error message to be displayed.
     */

        private void showError(String msg) {
            ErrorLabel.setText(msg);
            ErrorLabel.setVisible(true);
        }
       
        /**
         * Handles the server response by updating the book table with the fetched book details.
         * 
         * @param books The list of books received from the server.
         */

        @FXML
        public void handleServerResponse(ArrayList<BookLoanDetailsDTO> books) {
            if (books == null || books.isEmpty()) {
                showError("No books found.");
            } else {
                ObservableList<BookLoanDetailsDTO> bookLoanDetailsDTO = FXCollections.observableArrayList(books);
                bookTable.setItems(bookLoanDetailsDTO);
                ErrorLabel.setText(""); // Clear error
            }
        }

        /**
         * Refreshes the page by clearing the search fields, error label, and fetching all books again.
         */

        @FXML
        public void refreshPage() {
            // Clear the ComboBox and TextField
            cmbSearchType.getSelectionModel().selectFirst();
            searchField.clear();

            // Clear the error label
            ErrorLabel.setText("");

            // Fetch and display all books again
            fetchAllBooks();
        }
        
        /**
         * Handles the back button functionality. Navigates the user back to the main menu.
         * 
         * @param event The action event triggered by clicking the back button.
         * @throws Exception if there is an error navigating to the main menu.
         */
    	@FXML
    	public void getBtnBack(ActionEvent event) throws Exception {
    		if (!backButton.isDisable()) {
    			((Node) event.getSource()).getScene().getWindow().hide();
    			MainMenuController newScreen = new MainMenuController();
    			newScreen.start(new Stage());
    		}
    	}

}


