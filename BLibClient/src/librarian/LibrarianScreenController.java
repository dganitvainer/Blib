package librarian;

import javafx.fxml.FXML;
import java.nio.file.Files;
import java.io.ByteArrayInputStream;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.chart.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.concurrent.Task;
import java.sql.Timestamp; //
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import client.BLibClient;
import client.ClientController;
import entities.*;
import enums.*;
import logIn.MainMenuController;

/**
 * Controller class for the librarian screen interface.
 * Handles librarian-specific functionality and UI interactions.
 */
public class LibrarianScreenController {

	// Validation patterns
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$"); // keep the email pattern
																								// as example@domain
	private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$"); // keep the phone pattern as 10 numbers
	private static final Pattern ID_PATTERN = Pattern.compile("^\\d+$"); // keep the id as numbers only

	// Error messages
	private static final String INVALID_EMAIL = "Invalid email format";
	private static final String INVALID_PHONE = "Phone must be 10 digits";
	private static final String INVALID_ID = "ID must be numeric";
	private static final String INVALID_DATE = "Invalid date selection";
	private static final String REQUIRED_NAME = "Name field is required";
	private static final String REQUIRED_PASSWORD = "Password field is required";
	private static final String REQUIRED_ADDRESS = "Address field is required";

	// thread management
	private final List<Thread> activeThreads = new ArrayList<>();

	// FXML Injected components for Sign New Member tab
	@FXML
	private TextField memberIdField;
	@FXML
	private TextField passwordField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField extensionMemberField1;
	@FXML
	private TextField extensionMemberField2;
	@FXML
	private TextField phoneField;
	@FXML
	private TextField addressField;
	@FXML
	private Label welcomeLabel;
	@FXML
	private Button createMemberBtn;
	@FXML
	private Button logoutBtn;
	@FXML
	private ImageView imgBook;
	@FXML
	private Button btnShowName;

	// FXML Injected components for Return/Borrow Book tab
	@FXML
	private TextField returnBookIdField;
	@FXML
	private TextField returnMemberIdField;
	@FXML
	private Label returnErrorLabel;
	@FXML
	private Button submitReturnBtn;
	@FXML
	private TextField borrowBookIdField;
	@FXML
	private TextField borrowBookNameField;
	@FXML
	private TextField borrowMemberIdField;
	@FXML
	private Label borrowErrorLabel;
	@FXML
	private Button submitBorrowBtn;
	@FXML
	private Button refreshMembersBtn;

	// FXML Injected components for Member Card tabA
	@FXML
	private TextField extensionMemberIdField;
	@FXML
	private TextField extensionMemberIdField1;
	@FXML
	private DatePicker extensionDatePicker;
	@FXML
	private Label extensionErrorLabel;
	@FXML
	private Button submitExtensionBtn;
	@FXML
	private TableView<SubscriberDTO> memberCardsTable;
	@FXML
	private TableColumn<SubscriberDTO, String> idColumn;
	@FXML
	private TableColumn<SubscriberDTO, String> nameColumn;
	@FXML
	private TableColumn<SubscriberDTO, String> emailColumn;
	@FXML
	private TableColumn<SubscriberDTO, String> phoneColumn;
	@FXML
	private TableColumn<SubscriberDTO, String> addressColumn;
	@FXML
	private TableColumn<SubscriberDTO, String> statusColumn;
	@FXML
	private TextField memberFilterField;
	@FXML
	private Button filterMembersBtn;

	@FXML
	private Button refreshNotificationsBtn;

	// FXML Injected components for System Notifications tab
	@FXML
	private TableView<ActivityLog> notificationsTable;
	@FXML
	private TableColumn<ActivityLog, String> notificationIdColumn; // for user ID
	@FXML
	private TableColumn<ActivityLog, String> notificationIdColumn1; // for time stamp of the event
	@FXML
	private TableColumn<ActivityLog, String> messageColumn; // for the message of the activity log
	@FXML
	private TableColumn<ActivityLog, String> librarianIdColumn;
	@FXML
	private RadioButton subscriberFilterRadio;
	@FXML
	private RadioButton librarianFilterRadio;
	@FXML
	private RadioButton ReturnRadioButtn;
	@FXML
	private RadioButton LostRadioButtn;
	@FXML
	private ToggleGroup filterTypeGroup;
	@FXML
	private TextField notificationFilterField;
	@FXML
	private Button filterNotificationsBtn;

	// FXML components for Reports
	@FXML
	private BarChart<String, Number> borrowTimeChart;
	@FXML
	private CategoryAxis xAxis;
	@FXML
	private NumberAxis yAxis;
	@FXML
	private PieChart statusChart;

	@FXML
	private Button loanDurationGraphBtn;
	@FXML
	private Button lateReturnGraphBtn;
	@FXML
	private ToggleGroup daysToggleGroup;
	@FXML
	private Button generateMemberStatusBtn;
	@FXML
	private ToggleGroup statusPeriodGroup;
	@FXML
	private RadioButton sevenDaysRadio;
	@FXML
	private RadioButton fourteenDaysRadio;
	@FXML
	private RadioButton twentyOneDaysRadio;
	@FXML
	private RadioButton thirtyDaysRadio;

	private BLibClient client;
	private MainMenuController mainController;
	private User loggedLibrarian;
	private ObservableList<User> allMembers;
	private ObservableList<ActivityLog> allActivityLogs;

	/**
	 * Initializes the librarian screen by setting up controllers, tables, and
	 * loading necessary data.
	 */

	@FXML
	public void initialize() {
		BLibClient.setLibrarianScreenController(this);
		setupButtonHandlers();
		setupActivityLogTable();
		loadActivityLogs();
		setupMemberTable();
		loadAllMembers(); // Add this line
	}

	/**
	 * Sets up the member table with columns for user details.
	 */

	private void setupMemberTable() {
		idColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getUserId())));
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFullName()));
		emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
		phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
		addressColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
		statusColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));
	}

	/**
	 * Loads all members from the server.
	 */

	private void loadAllMembers() {
		System.out.println("Loading all members...");
		Message getMessage = new Message(null, Commands.GetAllMembers);
		ClientController.client.sendMessageToServer(getMessage);
	}

	/**
	 * Handles the response for ID validation check.
	 *
	 * @param exists boolean indicating if the ID already exists in the system.
	 */
	public void handleIdCheckResponse(boolean exists) {
		if (exists) {
			showError("Validation Error", "This ID already exists in the system");
		}
	}

	/**
	 * Sets up event handlers for all buttons in the librarian interface.
	 */
	private void setupButtonHandlers() {
		createMemberBtn.setOnAction(event -> handleCreateMember());
		submitReturnBtn.setOnAction(event -> handleBookReturn());
		submitBorrowBtn.setOnAction(event -> handleBookBorrow());
		submitExtensionBtn.setOnAction(event -> handleExtension());
		filterMembersBtn.setOnAction(event -> handleMemberFilter());
		filterNotificationsBtn.setOnAction(event -> handleNotificationFilter());
		loanDurationGraphBtn.setOnAction(event -> generateLoanDurationGraph());
		lateReturnGraphBtn.setOnAction(event -> generateLateReturnGraph());
		generateMemberStatusBtn.setOnAction(event -> handleGenerateMemberStatus());

		sevenDaysRadio.setToggleGroup(statusPeriodGroup);
		fourteenDaysRadio.setToggleGroup(statusPeriodGroup);
		twentyOneDaysRadio.setToggleGroup(statusPeriodGroup);
		thirtyDaysRadio.setToggleGroup(statusPeriodGroup);
		sevenDaysRadio.setSelected(true);

		logoutBtn.setOnAction(event -> {
			try {
				handleLogout(event);
			} catch (Exception e) {
				showError("Logout Error", e.getMessage());
			}
		});
	}

	/**
	 * Clears all error messages from the UI.
	 */

	private void clearAllErrors() {
		returnErrorLabel.setText("");
		borrowErrorLabel.setText("");
		extensionErrorLabel.setText("");
	}

	/**
	 * Validates the input fields for creating a new member.
	 * 
	 * @return ValidationResult containing success status and error message.
	 */
	private ValidationResult validateNewMemberFields() {
		List<String> errors = new ArrayList<>(); // array list for all the errors

		// ID validation - must be exactly 4 digits
		String id = memberIdField.getText();
		if (!id.matches("\\d{4}")) { // Changed to require exactly 4 digits
			errors.add("ID must be exactly 4 digits");
		}

		// Password validation - minimum 8 chars, without spaces
		String password = passwordField.getText();
		if (password.isEmpty() || password.contains(" ") || password.length() < 8 || password.length() > 30) {
			errors.add("Password must be 8-30 characters with no spaces");
		}

		// Full name validation - at least two words
		String name = nameField.getText().trim();
		if (name.isEmpty() || !name.contains(" ")) {
			errors.add("Please enter a full name (first and last name)");
		}

		// Email validation
		if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) { // the normal format of example@domain
			errors.add("Invalid email format");
		}

		// Phone validation - exactly 10 digits
		if (!phoneField.getText().matches("\\d{10}")) {
			errors.add("Phone must be exactly 10 digits");
		}

		return new ValidationResult(errors.isEmpty(), String.join("\n", errors));

	}

	/**
	 * Handles the refresh of notifications and activity logs.
	 */

	@FXML
	private void handleRefreshNotifications() {
		System.out.println("Refreshing activity logs");
		notificationFilterField.clear(); // Clear the filter field
		loadActivityLogs(); // Reuse existing method to load all logs
	}

	/**
	 * Handles member creation with validation
	 */
	@FXML
	private void handleCreateMember() {
		System.out.println("Starting create member process...");
		ValidationResult validation = validateNewMemberFields();
		if (!validation.isValid()) {
			showError("Validation Error", validation.getMessage());
			return;
		}

		try {
			User newUser = new User(Integer.parseInt(memberIdField.getText()), nameField.getText(),
					memberIdField.getText(), // username
					passwordField.getText(), // password
					emailField.getText(), phoneField.getText(), UserType.Subscriber, // Make sure this is set correctly
					java.time.LocalDateTime.now().toString(), addressField.getText());

			System.out.println("Created new User object: " + newUser.getUserId());
			Message createUserMsg = new Message(newUser, Commands.CreateMember);
			ClientController.client.sendMessageToServer(createUserMsg);

		} catch (Exception e) {
			System.err.println("Error in handleCreateMember: " + e.getMessage());
			e.printStackTrace();
			showError("Create Member Error", "Failed to create member: " + e.getMessage());
		}
	}

	/**
	 * Handles the return process for a book, including validation and sending the
	 * request.
	 */
	@FXML
	private void handleBookReturn() {
		if (!validateReturnInputs()) {
			return; // Stop if validation fails
		}

		try {
			System.out.println("Attempting to process book: " + returnBookIdField.getText() + " for member: "
					+ returnMemberIdField.getText());

			boolean isLost = LostRadioButtn.isSelected();

			Object[] returnData = { returnBookIdField.getText().trim(), returnMemberIdField.getText().trim(),
					loggedLibrarian.getUserId(), isLost };

			Message returnMsg = new Message(returnData, Commands.ReturnBook);
			client.sendToServer(returnMsg);
			System.out.println("Return/Lost book request sent to server");

		} catch (IOException e) {
			System.out.println("Error sending return request: " + e.getMessage());
			returnErrorLabel.setText("Error sending to server: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Validates book borrowing inputs and handles the borrow operation.
	 */
	@FXML
	private void handleBookBorrow() {
		if (!validateBorrowInputs()) {
			return; // Stop if validation fails
		}

		try {
			Object[] borrowData = { borrowBookIdField.getText().trim(), borrowBookNameField.getText().trim(),
					borrowMemberIdField.getText().trim(), loggedLibrarian.getUserId() };
			Message borrowMsg = new Message(borrowData, Commands.BorrowBook);
			client.sendToServer(borrowMsg);

		} catch (IOException e) {
			borrowErrorLabel.setText("Error: " + e.getMessage());
		}
	}

	/**
	 * Validates and handles loan extension requests.
	 */
	@FXML
	private void handleExtension() {
		try {
			int memberId = Integer.parseInt(extensionMemberIdField.getText());
			int bookId = Integer.parseInt(extensionMemberIdField1.getText());

			Object[] extensionData = { memberId, bookId, loggedLibrarian.getUserId() };
			Message extensionMsg = new Message(extensionData, Commands.ExtendBookLoan);
			ClientController.client.sendMessageToServer(extensionMsg);

		} catch (NumberFormatException e) {
			showError("Invalid Input", "Please enter valid numeric IDs");
		}
	}

	/**
	 * Validates IDs against the ID pattern.
	 * 
	 * @param ids Variable number of IDs to validate
	 * @return boolean indicating if all provided IDs are valid
	 */
	private boolean validateIds(String... ids) {
		for (String id : ids) {
			if (id != null && !ID_PATTERN.matcher(id).matches()) {
				return false;
			}
		}
		return true;
	}

	// [Previous methods remain unchanged]

	/**
	 * Represents a validation result with status and message.
	 */
	private static class ValidationResult {
		private final boolean valid;
		private final String message;

		public ValidationResult(boolean valid, String message) {
			this.valid = valid;
			this.message = message;
		}

		public boolean isValid() {
			return valid;
		}

		public String getMessage() {
			return message;
		}
	}

	/**
	 * Filters member table by ID from text input. Shows all members if input is
	 * empty.
	 */
	@FXML
	public void handleMemberFilter() {
		String filter = memberFilterField.getText();

		if (filter.isEmpty()) {
			loadAllMembers();
			return;
		}

		ObservableList<SubscriberDTO> allMembers = memberCardsTable.getItems();

		ObservableList<SubscriberDTO> filteredList = allMembers.stream()
				.filter(member -> String.valueOf(member.getUserId()).contains(filter))
				.collect(Collectors.toCollection(FXCollections::observableArrayList));

		memberCardsTable.setItems(filteredList);
	}

	/**
	 * Filters notifications based on the user input in the filter field.
	 */

	@FXML
	private void handleNotificationFilter() {
		String filter = notificationFilterField.getText();
		if (filter == null || filter.trim().isEmpty()) {
			loadActivityLogs();
			return;
		}

		try {
			int id = Integer.parseInt(filter);
			User filterUser = new User(id, null, null, null, null, null,
					librarianFilterRadio.isSelected() ? UserType.Librarian : UserType.Subscriber, null, null);

			Message getMessage = new Message(filterUser, Commands.GetFilteredActivityLogs);
			ClientController.client.sendMessageToServer(getMessage);
		} catch (NumberFormatException e) {
			showError("Invalid Input", "Please enter a valid numeric ID");
		}
	}

	/**
	 * Handles the response with activity logs and updates the UI accordingly.
	 * 
	 * @param logs a list of activity logs to display.
	 */

	public void handleActivityLogsResponse(ArrayList<ActivityLog> logs) {
		System.out.println("Received activity logs response. Number of logs: " + (logs != null ? logs.size() : "null"));

		Platform.runLater(() -> {
			if (logs != null) {
				allActivityLogs.clear();
				allActivityLogs.addAll(logs);
				System.out.println("Activity logs added to table");
			} else {
				System.err.println("Received null activity logs");
			}
		});
	}

	/**
	 * Handles the logout process, including sending the logout message to the
	 * server and navigating back to the login screen.
	 * 
	 * @param event the action event triggering the logout.
	 * @throws Exception if an error occurs during the logout process.
	 */

	@FXML
	public void handleLogout(ActionEvent event) throws Exception {
		Message logoutMsg = new Message(loggedLibrarian.getUserId(), Commands.Logout);
		client.sendToServer(logoutMsg);

		loggedLibrarian = null;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/logIn/MainMenu.fxml"));
		Parent root = loader.load();
		Stage stage = new Stage();
		stage.setScene(new Scene(root));
		stage.setTitle("Main Menu");
		stage.show();

		Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
		currentStage.close();
	}

	/**
	 * Clears all input fields related to member creation.
	 */

	private void clearNewMemberFields() {
		memberIdField.clear();
		passwordField.clear();
		nameField.clear();
		emailField.clear();
		phoneField.clear();
		addressField.clear();
	}

	/**
	 * Displays an error message in an alert dialog.
	 * 
	 * @param title   the title of the error alert.
	 * @param message the error message to display.
	 */

	private void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Handles server responses based on the command type in the response.
	 * 
	 * @param response the server response message to process.
	 */
	public void handleServerResponse(Message response) {
		switch (response.getCmd()) {
		case CreateMember:
			handleCreateMemberResponse(response);
			break;
		case ReturnBook:
			handleReturnBookResponse(response);
			break;
		case BorrowBook:
			handleBorrowBookResponse(response);
			break;
		case GetChartData:
			updateCharts(response);
			break;
		default:
			System.out.println("Unhandled response type: " + response.getCmd());
		}
	}

	/**
	 * Handles the response for creating a new member.
	 * 
	 * @param response the server response for the member creation.
	 */

	public void handleCreateMemberResponse(Message response) {
		if (response.getObj() instanceof Boolean && (Boolean) response.getObj()) {
			showSuccess("Member created successfully");
			clearNewMemberFields();
		} else if (response.getObj() instanceof String) {
			// Show the specific error message
			showError("Create Member Error", (String) response.getObj());
		} else {
			showError("Create Member Error", "Failed to create member");
		}
	}

	/**
	 * Handles the response for returning a book.
	 * 
	 * @param response the server response for the book return.
	 */

	public void handleReturnBookResponse(Message response) {
		if (response.getObj() instanceof String) {
			String message = (String) response.getObj();
			if (message.startsWith("Successfully")) {
				showSuccess(message);
				returnBookIdField.clear();
				returnMemberIdField.clear();
			} else {
				showError("Return Failed: ", message);
			}
		}
	}

	/**
	 * Handles the response for borrowing a book.
	 * 
	 * @param response the server response for the book borrow.
	 */

	public void handleBorrowBookResponse(Message response) {
		if (response.getObj() instanceof String) {
			String message = (String) response.getObj();
			if (message.startsWith("Successfully")) {
				showSuccess(message);
				borrowBookIdField.clear();
				borrowBookNameField.clear();
				borrowMemberIdField.clear();
			} else {
				showError("Borrow Failed: ", message);
			}
		}
	}

	/**
	 * Handles the response for extending a loan.
	 * 
	 * @param response the server response for loan extension.
	 */

	public void handleExtendLoanResponse(Message response) {
		if (response.getObj() instanceof Boolean && (Boolean) response.getObj()) {
			extensionErrorLabel.setText("Loan extended successfully");
			extensionMemberIdField.clear();
			extensionDatePicker.setValue(null);
		} else {
			extensionErrorLabel.setText("Failed to extend loan");
		}
	}

	/**
	 * Updates the charts based on the server response containing chart data.
	 * 
	 * @param response the server response containing the chart data.
	 */

	private void updateCharts(Message response) {
		if (response.getObj() instanceof Object[]) {
			Object[] chartData = (Object[]) response.getObj();
			updateBorrowTimeChart((List<Number>) chartData[0]);
			updateStatusChart((List<Number>) chartData[1]);
		}
	}

	/**
	 * Updates the borrow time chart with the provided data.
	 * 
	 * @param returnCounts a list of return counts per week to plot on the chart.
	 */

	private void updateBorrowTimeChart(List<Number> returnCounts) {
		borrowTimeChart.getData().clear();
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Returns per Week");

		for (int i = 0; i < returnCounts.size(); i++) {
			series.getData().add(new XYChart.Data<>("Week " + (i + 1), returnCounts.get(i)));
		}

		borrowTimeChart.getData().add(series);
	}

	/**
	 * Updates the status chart with the given data for active and frozen statuses.
	 * 
	 * @param statusCounts a list of counts representing the status distribution
	 *                     (active, frozen).
	 */

	private void updateStatusChart(List<Number> statusCounts) {
		statusChart.getData().clear();

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("Active", statusCounts.get(0).doubleValue()),
				new PieChart.Data("Frozen", statusCounts.get(1).doubleValue()));

		statusChart.setData(pieChartData);
	}

	/**
	 * Displays a success message in an information alert dialog.
	 * 
	 * @param message the success message to display.
	 */

	private void showSuccess(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 * Sets the client object for server communication.
	 * 
	 * @param client the client object to set.
	 */

	public void setClient(BLibClient client) {
		this.client = client;
	}

	/**
	 * Sets the main controller for the interface.
	 * 
	 * @param mainController the main menu controller to set.
	 */

	public void setMainController(MainMenuController mainController) {
		this.mainController = mainController;
	}

	/**
	 * Sets the logged-in librarian and updates the welcome label.
	 * 
	 * @param librarian the librarian object representing the logged-in user.
	 */

	public void setLoggedLibrarian(User librarian) {
		this.loggedLibrarian = librarian;
		welcomeLabel.setText("Welcome, " + librarian.getFullName() + "!");

	}

	/**
	 * Handles the response containing member data and updates the UI.
	 * 
	 * @param members a list of subscriber data transfer objects to display.
	 */

	public void handleMemberDataResponse(ArrayList<SubscriberDTO> members) {
		System.out.println("Received " + members.size() + " members");
		Platform.runLater(() -> {
			memberCardsTable.getItems().clear();
			memberCardsTable.getItems().addAll(members);
		});
	}

	/**
	 * Validates the extension date to check if it is between 1 and 7 days from
	 * today.
	 * 
	 * @param extensionDate the extension date to validate.
	 * @return true if the extension date is valid (1-7 days), false otherwise.
	 */
	private boolean validateExtensionDate(LocalDate extensionDate) {
		if (extensionDate == null) {
			return false;
		}
		LocalDate today = LocalDate.now();
		long daysBetween = ChronoUnit.DAYS.between(today, extensionDate);
		return daysBetween >= 1 && daysBetween <= 7;
	}

	/**
	 * Executes a task with UI feedback and manages the thread.
	 * 
	 * @param task           the task to execute.
	 * @param successMessage the message to display on success.
	 */
	private void executeWithFeedback(Task<?> task, String successMessage) {

		task.setOnSucceeded(e -> {
			showSuccess(successMessage);
			cleanupThread(Thread.currentThread());
		});

		task.setOnFailed(e -> {
			showError("Operation Failed", "The operation could not be completed, Please try again.");
			cleanupThread(Thread.currentThread());
		});

		Thread thread = new Thread(task);
		activeThreads.add(thread);
		thread.start();
	}

	/**
	 * Removes an active thread from the active threads list.
	 * 
	 * @param thread the thread to remove.
	 */
	private void cleanupThread(Thread thread) {
		activeThreads.remove(thread);
	}

	/**
	 * Cleans up resources and interrupts any active threads when the controller is
	 * destroyed.
	 */
	public void cleanup() {
		for (Thread thread : activeThreads) {
			if (thread.isAlive()) {
				thread.interrupt();
			}
		}
		activeThreads.clear();
	}

	/**
	 * Starts the librarian interface by loading the librarian screen and setting up
	 * the controller.
	 * 
	 * @param stage the stage where the scene will be displayed.
	 * @param user  the logged-in user (librarian) to set.
	 * @throws Exception if an error occurs while loading the scene or setting up
	 *                   the controller.
	 */

	public void start(Stage stage, User user) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/librarian/LibrarianScreen.fxml"));
		Parent root = loader.load();

		LibrarianScreenController controller = loader.getController();
		controller.setClient(ClientController.client);
		controller.setLoggedLibrarian(user);
		controller.initialize();

		Scene scene = new Scene(root);
		stage.setTitle("Librarian Interface");
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * Sets up the activity log table, including column mappings and loading of
	 * activity logs.
	 */

	private void setupActivityLogTable() {
		System.out.println("Setting up activity log table");

		notificationIdColumn.setCellValueFactory(cellData -> {
			Timestamp date = cellData.getValue().getActivityDate();
			return new SimpleStringProperty(date != null ? date.toString() : "");
		});

		notificationIdColumn1.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSubscriberId())));

		librarianIdColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getLibrarianId())));

		messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));

		allActivityLogs = FXCollections.observableArrayList();
		notificationsTable.setItems(allActivityLogs);

		loadActivityLogs();
	}

	/**
	 * Loads activity logs from the server and updates the UI.
	 */

	private void loadActivityLogs() {
		System.out.println("Attempting to load activity logs from server");
		try {
			Message getMessage = new Message(null, Commands.GetActivityLogs);
			ClientController.client.sendMessageToServer(getMessage);
			System.out.println("Activity logs request sent to server");
		} catch (Exception e) {
			System.err.println("Error sending activity logs request: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Handles the action of refreshing the members list.
	 */

	@FXML
	private void handleRefreshMembers() {
		System.out.println("Refreshing member cards table");
		memberFilterField.clear(); // Clear the filter field
		loadAllMembers(); // Reuse existing method to load all members
	}

	/**
	 * Handles the response for the extension request.
	 * 
	 * @param response the message response containing the result of the extension
	 *                 request
	 */

	public void handleExtensionResponse(Message response) {
		if (response.getObj() instanceof String) {
			String result = (String) response.getObj();
			if (result.startsWith("Successfully")) {
				showSuccess(result);
				extensionMemberIdField.clear();
				extensionMemberIdField1.clear();
			} else {
				showError("Extension Failed", result);
			}
		}
	}

	/*********************************************************************************************/
	// borrow time charts methods //
	/*********************************************************************************************/
	/**
	 * Generates the loan duration graph and sends a request to the server.
	 */

	private void generateLoanDurationGraph() {
		Message graphMsg = new Message(30, Commands.GetLoanDurationChart);
		ClientController.client.sendMessageToServer(graphMsg);
	}

	/**
	 * Generates the late return graph and sends a request to the server.
	 */

	private void generateLateReturnGraph() {
		Message graphMsg = new Message(30, Commands.GetLateReturnChart);
		ClientController.client.sendMessageToServer(graphMsg);
	}

	/**
	 * Handles the loan duration chart response from the server.
	 * 
	 * @param chartData the list of data to be displayed on the loan duration graph
	 */

	public void handleLoanDurationChartResponse(ArrayList<Number> chartData) {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(30);

		if (chartData != null && !chartData.isEmpty()) {
			String title = "Loan Duration Report (" + startDate + " to " + endDate + ")";
			updateBorrowTimeChart(chartData);
			borrowTimeChart.setTitle(title);
		} else {
			showError("Graph Generation Error", "No data available for loan duration graph");
		}
	}

	/**
	 * Handles the late return chart response from the server.
	 * 
	 * @param chartData the list of data to be displayed on the late return graph
	 */

	public void handleLateReturnChartResponse(ArrayList<Number> chartData) {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(30);

		if (chartData != null && !chartData.isEmpty()) {
			String title = "Return Status Report (" + startDate + " to " + endDate + ")";
			updateLateReturnChart(chartData);
			borrowTimeChart.setTitle(title);
		} else {
			showError("Graph Generation Error", "No data available for return status graph");
		}
	}

	/**
	 * Updates the borrow time chart with the provided duration counts.
	 * 
	 * @param durationCounts the list of borrow duration counts
	 */

	private void updateBorrowTimeChart(ArrayList<Number> durationCounts) {
		borrowTimeChart.getData().clear();
		XYChart.Series<String, Number> series = new XYChart.Series<>();

		String[] categories = { "0-7 days", "8-14 days", "15-21 days", "22+ days" };
		String barColor = "#2196F3"; // Blue color

		for (int i = 0; i < durationCounts.size(); i++) {
			XYChart.Data<String, Number> data = new XYChart.Data<>(categories[i], durationCounts.get(i));
			series.getData().add(data);
		}

		borrowTimeChart.getData().add(series);

		// Improve styling
		borrowTimeChart.setAnimated(false);
		borrowTimeChart.setLegendVisible(false); // Hide legend
		borrowTimeChart.getXAxis().setLabel("Loan Duration");
		borrowTimeChart.getYAxis().setLabel("Number of Books");

		// Apply custom colors to bars
		series.getData().forEach(data -> {
			data.getNode().setStyle("-fx-bar-fill: " + barColor + ";" + "-fx-border-color: derive(" + barColor
					+ ", -30%);" + "-fx-border-width: 1;");
		});
	}

	/**
	 * Updates the late return chart with the provided chart data.
	 * 
	 * @param chartData the list of chart data to update the late return chart
	 */

	private void updateLateReturnChart(ArrayList<Number> chartData) {/////////////////////////////////////////////////////////
		borrowTimeChart.getData().clear();
		XYChart.Series<String, Number> series = new XYChart.Series<>();

		String[] categories = { "On Time", "Grace Period", "Overdue" };
		String[] colors = { "#4CAF50", "#FFC107", "#F44336" }; // Green, Yellow, Red

		for (int i = 0; i < chartData.size(); i++) {
			XYChart.Data<String, Number> data = new XYChart.Data<>(categories[i], chartData.get(i));
			series.getData().add(data);
		}

		borrowTimeChart.getData().add(series);

		// Improve styling
		borrowTimeChart.setAnimated(false);
		borrowTimeChart.setLegendVisible(false); // Hide legend
		borrowTimeChart.getXAxis().setLabel("Return Status");
		borrowTimeChart.getYAxis().setLabel("Number of Books");

		// Apply different colors for each status
		for (int i = 0; i < series.getData().size(); i++) {
			Node barNode = series.getData().get(i).getNode();
			if (barNode != null) {
				barNode.setStyle("-fx-bar-fill: " + colors[i] + ";" + "-fx-border-color: derive(" + colors[i]
						+ ", -30%);" + "-fx-border-width: 1;");
			}
		}
	}

	/*********************************************************************************************/
	// end borrow time charts methods //
	/*********************************************************************************************/

	/*********************************************************************************************/
	// validation functions //
	/*********************************************************************************************/
	/**
	 * Validates the borrow inputs (book and member ID).
	 * 
	 * @return true if all borrow inputs are valid, false otherwise
	 */

	private boolean validateBorrowInputs() {
		// Check for empty fields
		if (borrowBookIdField.getText().trim().isEmpty()) {
			showError("Input Error", "Book barcode cannot be empty");
			return false;
		}
		if (borrowMemberIdField.getText().trim().isEmpty()) {
			showError("Input Error", "Member ID cannot be empty");
			return false;
		}

		// Validate numeric values
		try {
			Integer.parseInt(borrowBookIdField.getText().trim());
		} catch (NumberFormatException e) {
			showError("Input Error", "Book barcode must be a number");
			return false;
		}

		try {
			Integer.parseInt(borrowMemberIdField.getText().trim());
		} catch (NumberFormatException e) {
			showError("Input Error", "Member ID must be a number");
			return false;
		}

		return true;
	}

	/**
	 * Validates the return inputs (book and member ID).
	 * 
	 * @return true if all return inputs are valid, false otherwise
	 */

	private boolean validateReturnInputs() {
		// Check for empty fields
		if (returnBookIdField.getText().trim().isEmpty()) {
			showError("Input Error", "Book barcode cannot be empty");
			return false;
		}
		if (returnMemberIdField.getText().trim().isEmpty()) {
			showError("Input Error", "Member ID cannot be empty");
			return false;
		}

		// Validate numeric values
		try {
			Integer.parseInt(returnBookIdField.getText().trim());
		} catch (NumberFormatException e) {
			showError("Input Error", "Book barcode must be a number");
			return false;
		}

		try {
			Integer.parseInt(returnMemberIdField.getText().trim());
		} catch (NumberFormatException e) {
			showError("Input Error", "Member ID must be a number");
			return false;
		}

		return true;
	}

	/*********************************************************************************************/
	// start activity status report methods //
	/*********************************************************************************************/

	/**
	 * Handles the action of generating the member status report based on selected
	 * period.
	 */

	@FXML
	private void handleGenerateMemberStatus() {
		// Get selected period from radio buttons
		int days = 7; // Default to 7 days
		if (fourteenDaysRadio.isSelected()) {
			days = 14;
		} else if (twentyOneDaysRadio.isSelected()) {
			days = 21;
		} else if (thirtyDaysRadio.isSelected()) {
			days = 30;
		}

		Message statusMsg = new Message(days, Commands.GetMemberStatus);
		ClientController.client.sendMessageToServer(statusMsg);
	}

	/**
	 * Updates the status pie chart with the provided data.
	 * 
	 * @param periodData the data for the different periods to display on the chart
	 */

	private void updateStatusPieChart(Map<String, ArrayList<Number>> periodData) {
		statusChart.getData().clear();
		statusChart.setLegendVisible(false); // Remove legends

		// Get data for selected period
		String selectedPeriod = getSelectedPeriod();
		ArrayList<Number> data = periodData.get(selectedPeriod);

		LocalDate endDate = LocalDate.now();
		LocalDate startDate;

		// Determine date range based on selected period
		switch (selectedPeriod) {
		case "8-14":
			startDate = endDate.minusDays(14);
			endDate = endDate.minusDays(8);
			break;
		case "15-21":
			startDate = endDate.minusDays(21);
			endDate = endDate.minusDays(15);
			break;
		case "22-30":
			startDate = endDate.minusDays(30);
			endDate = endDate.minusDays(22);
			break;
		default: // "0-7"
			startDate = endDate.minusDays(7);
			endDate = endDate.minusDays(1);
			break;
		}

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		String dateRange = formatter.format(startDate) + " - " + formatter.format(endDate);

		if (data != null && data.size() >= 2) {
			if (data.get(0).intValue() == 0 && data.get(1).intValue() == 0) {
				// No data scenario
				statusChart.setTitle("No data for the dates: " + dateRange);
				showError("No Data Available", "There is no activity data available for the period: " + dateRange);
				return;
			}

			ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
					new PieChart.Data("Active (" + data.get(0).intValue() + ")", data.get(0).doubleValue()),
					new PieChart.Data("Frozen (" + data.get(1).intValue() + ")", data.get(1).doubleValue()));

			statusChart.setData(pieChartData);
			statusChart.setTitle("Activity Member Status: " + dateRange);

			// Apply colors and add click handlers for visual feedback
			pieChartData.forEach(data_ -> {
				if (data_.getName().startsWith("Active")) {
					data_.getNode().setStyle("-fx-pie-color: #4CAF50;"); // Green for active
				} else {
					data_.getNode().setStyle("-fx-pie-color: #F44336;"); // Red for frozen
				}

				// Add hover effect
				data_.getNode().setOnMouseEntered(event -> data_.getNode().setStyle(data_.getNode().getStyle()
						+ "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);"));

				data_.getNode().setOnMouseExited(event -> data_.getNode().setStyle(data_.getNode().getStyle()
						.replace("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);", "")));
			});

		} else {
			// No data scenario
			statusChart.setTitle("No data for the dates: " + dateRange);
			showError("No Data Available", "There is no activity data available for the period: " + dateRange);
		}
	}

	/**
	 * Returns the selected period for the status report.
	 * 
	 * @return the selected period as a string (e.g., "0-7", "8-14")
	 */

	private String getSelectedPeriod() {
		if (fourteenDaysRadio.isSelected())
			return "8-14";
		if (twentyOneDaysRadio.isSelected())
			return "15-21";
		if (thirtyDaysRadio.isSelected())
			return "22-30";
		return "0-7"; // Default
	}

	/**
	 * Returns the number of days for the selected period.
	 * 
	 * @return the number of days for the selected period
	 */

	private int getSelectedDays() {
		if (fourteenDaysRadio.isSelected())
			return 14;
		if (twentyOneDaysRadio.isSelected())
			return 21;
		if (thirtyDaysRadio.isSelected())
			return 30;
		return 7;
	}

	/**
	 * Handles the member status response from the server.
	 * 
	 * @param statusData the map containing the status data for different periods
	 */

	public void handleMemberStatusResponse(Map<String, ArrayList<Number>> statusData) {
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusDays(getSelectedDays());
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		String formattedStartDate = formatter.format(startDate);
		String formattedEndDate = formatter.format(endDate);

		String selectedPeriod = getSelectedPeriod();
		String title = "Member Status Distribution (" + formattedStartDate + " - " + formattedEndDate + ")";

		if (statusData != null) {
			updateStatusPieChart(statusData);

			// Check if any period has non-zero data
			boolean hasNonZeroData = statusData.values().stream()
					.anyMatch(list -> list.get(0).intValue() > 0 || list.get(1).intValue() > 0);

			if (!hasNonZeroData) {
				statusChart.setTitle("No Data Available: " + formattedStartDate + " - " + formattedEndDate);
			}
		} else {
			statusChart.setTitle("No Data Available: " + formattedStartDate + " - " + formattedEndDate);
		}
	}

	/*********************************************************************************************/
	// end activity status report methods //
	/*********************************************************************************************/

	/*********************************************************************************************/
	// show books //
	/*********************************************************************************************/
	/**
	 * Handles the action of showing the book name based on the provided book ID.
	 */

	@FXML
	private void handleShowBookName() {
		try {
			String bookIdText = borrowBookIdField.getText();
			if (bookIdText.isEmpty()) {
				showError("Input Error", "Please enter a book ID");
				return;
			}

			int bookId = Integer.parseInt(bookIdText);
			Message getMessage = new Message(bookId, Commands.getSimpleBookById);

			// Change this line to use ClientController.client instead
			ClientController.client.sendMessageToServer(getMessage);

		} catch (NumberFormatException e) {
			showError("Input Error", "Please enter a valid numeric book ID");
		} catch (Exception e) {
			showError("Server Error", "Could not communicate with server: " + e.getMessage());
		}
	}

	/**
	 * Handles the book response, updating the UI with the book details.
	 * 
	 * @param book the book object containing the details to be displayed
	 */

	public void handleBookResponse(Book book) {
		if (book != null) {
			Platform.runLater(() -> {
				System.out.println("\nClient: Received book: " + book.getTitle());
				borrowBookNameField.setText(book.getTitle());

				byte[] imageData = book.getImageData();
				if (imageData != null) {
					System.out.println("Client: Received image data, size: " + imageData.length + " bytes");
					try {
						Image bookImage = new Image(new ByteArrayInputStream(imageData));
						System.out.println(
								"Client: Created image, size: " + bookImage.getWidth() + "x" + bookImage.getHeight());
						imgBook.setImage(bookImage);
					} catch (Exception e) {
						System.out.println("Client: Error creating image: " + e.getMessage());
						e.printStackTrace();
						imgBook.setImage(null);
					}
				} else {
					System.out.println("Client: No image data received for book: " + book.getTitle());
					imgBook.setImage(null);
				}
			});
		} else {
			Platform.runLater(() -> {
				System.out.println("Client: Received null book");
				borrowBookNameField.clear();
				imgBook.setImage(null);
				showError("Book Not Found", "No book found with this ID");
			});
		}
	}

}
