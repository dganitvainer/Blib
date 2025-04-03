package member;

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
import entities.BookLoanDetailsDTO;
import entities.Message;
import entities.SubscriberDTO;
import client.BLibClient;
import client.ClientController;
import enums.Commands;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Controller class for managing the book search and order functionality
 * for library members.
 */
public class BookSearchMemberController {

    @FXML
    private TableView<BookLoanDetailsDTO> bookTable;
    @FXML
    private TableColumn<BookLoanDetailsDTO, Integer> idColumn;
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
    private TextField textOrderBook;
    @FXML
    private Label ErrorLabel;

    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button OrderButton;

    private SubscriberDTO loggedMember;

    /**
     * Sets the logged-in member for this controller.
     *
     * @param subscriber the logged-in subscriber
     */
    public void setLoggedMember(SubscriberDTO subscriber) {
        this.loggedMember = subscriber;
    }

    /**
     * Starts the Book Search Member screen.
     *
     * @param stage      the current JavaFX stage
     * @param subscriber the logged-in subscriber
     * @throws IOException if the FXML file cannot be loaded
     */
    public void start(Stage stage, SubscriberDTO subscriber) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/member/BookSearchMember.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Search and Order Books");
        stage.show();

        // Access the controller and set the logged member
        BookSearchMemberController controller = loader.getController();
        controller.setLoggedMember(subscriber);
    }

    /**
     * Initializes the Book Search Member screen.
     * Sets up the table columns, populates the search type ComboBox, and fetches all books.
     */
    @FXML
    public void initialize() {
        // Initialize the table columns
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getBookId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthor()));
        themeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCopiesAvailable()).asObject());
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShelfLocation()));
        returnDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getReturnDate() != null ? cellData.getValue().getReturnDate() : ""));
        BLibClient.bookSearchMemberController = this;

        // Populate the ComboBox with search options
        cmbSearchType.setItems(FXCollections.observableArrayList("Title", "Author", "Theme", "Description"));
        cmbSearchType.getSelectionModel().selectFirst();

        // Fetch and display all books initially
        fetchAllBooks();
    }

    /**
     * Fetches all books from the server and displays them in the table.
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
     * Handles the search action. Searches for books based on the selected search type and input text.
     *
     * @param event the ActionEvent triggered by clicking the search button
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
     * Handles the action to order a book.
     * Prevents ordering if the subscriber's status is FROZEN.
     *
     * @param event the ActionEvent triggered by clicking the order button
     */
    @FXML
    public void handleOrderBook(ActionEvent event) {
        if (loggedMember == null) {
            showError("You are not logged in. Please log in and try again.");
            return;
        }

        if (loggedMember.getStatus().name().equals("FROZEN")) {
            showError("Your account is frozen. You cannot order books until your account is reactivated.");
            return;
        }

        String bookId = textOrderBook.getText().trim();

        if (bookId.isEmpty()) {
            showError("Please enter a Book ID to order.");
            return;
        }

        try {
            // Prepare data for order request
            Object[] orderData = new Object[]{loggedMember.getUserId(), Integer.parseInt(bookId)};

            // Send order request to the server
            Message orderMessage = new Message(orderData, Commands.OrderBook);
            ClientController.client.sendMessageToServer(orderMessage);

            textOrderBook.clear();
        } catch (NumberFormatException e) {
            showError("Book ID must be a number.");
        } catch (Exception e) {
            showError("Failed to order book. Please try again.");
            e.printStackTrace();
        }
    }

    /**
     * Displays a success message.
     *
     * @param message the success message to display
     */
    private void showSuccessMessage(String message) {
        ErrorLabel.setText(message);
        ErrorLabel.setStyle("-fx-text-fill: green;");
        ErrorLabel.setVisible(true);
    }

    /**
     * Displays an error message.
     *
     * @param message the error message to display
     */
    private void showError(String message) {
        ErrorLabel.setText(message);
        ErrorLabel.setStyle("-fx-text-fill: red;");
        ErrorLabel.setVisible(true);
    }

    /**
     * Handles the server response for book ordering.
     *
     * @param message the response message from the server
     */
    public void handleOrderBookResponse(String message) {
        switch (message.toLowerCase().trim()) {
            case "success":
                showSuccessMessage("The book has been successfully reserved.");
                break;
            case "alreadyborrowed":
                showError("You have already borrowed this book.");
                break;
            case "nocopiesavailable":
                showError("No available copies due to too many pending reservations.");
                break;
            case "alreadyreserved":
                showError("You had already reserved this book.");
                break;
            case "canborrow":
                showSuccessMessage("The book is available for borrowing.");
                break;
            case "databaseerror":
                showError("There was an error with the database. Please try again later.");
                break;
            case "error":
                showError("An unexpected error occurred. Please try again.");
                break;
            default:
                showError("Failed to reserve the book: " + message);
                break;
        }
    }

    /**
     * Handles the response from the server for book search results.
     *
     * @param books the list of books retrieved from the server
     */
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
     * Handles the action to navigate back to the previous screen.
     *
     * @param event the ActionEvent triggered by clicking the back button
     * @throws Exception if navigation fails
     */
    @FXML
    public void getBtnBack(ActionEvent event) throws Exception {
        if (!backButton.isDisable()) {
            ((Node) event.getSource()).getScene().getWindow().hide();
            MemberMenuController newScreen = new MemberMenuController();
            newScreen.start(new Stage(), loggedMember);
        }
    }
}
