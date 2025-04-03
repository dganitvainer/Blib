package logIn;

import client.BLibClient;
import client.ClientController;
import entities.User;
import enums.Commands;
import enums.UserType;
import entities.Message;
import entities.SubscriberDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import member.MemberMenuController;
import librarian.LibrarianScreenController;
import library.BookSearchController;

/**
* Controller class for the main menu/login interface.
* Handles user authentication and initial navigation in the application.
*
* This controller manages the login form and authentication process,
* including input validation and user feedback.
*/
public class MainMenuController {

	@FXML
	private TextField idField;
	@FXML
	private TextField mPassField;
	@FXML
	private Button srchBtn;
	@FXML
	private Button loginBtn;
	@FXML
	private Label loginMsgLabel; // New label for displaying messages
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the FXML file has been loaded.
	 * 
	 * Sets this instance as the main menu controller in BLibClient for
	 * global access across the application.
	 * 
	 * 
	 */
	@FXML
	public void initialize() {
		BLibClient.mainMenuController = this;
	}

	/**
	 * Initializes and displays the main application window.
	 * Loads the MainMenu FXML file and sets it as the root scene for the primary stage.
	 *
	 * @param primaryStage The main stage provided by the JavaFX runtime
	 * @throws Exception If the FXML file cannot be loaded or another error occurs during initialization
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(this.getClass().getResource("/logIn/MainMenu.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Main Menu");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@FXML
	private void handleLogin(ActionEvent event) throws Exception {
		String userID = idField.getText();
		String userPass = mPassField.getText();

		if (userID.isEmpty() || userPass.isEmpty()) {
			System.out.println("Empty fields detected"); // Debugging line
			loginMsg("Username and password are required fields");
			return;
		}

		try {
			// Create a new User object
			int userIdInt = Integer.parseInt(userID);
			User user = new User(userIdInt, null, null, userPass, null, null, null, null, null);

			// Create and send a message to the server to check the username and password
			Message checkCredentialsMessage = new Message(user, Commands.CheckUsername);
			ClientController.client.sendMessageToServer(checkCredentialsMessage); // changed
		} catch (NumberFormatException e) {
			loginMsg("Invalid User ID format. Please enter a numeric value.");
		}
	}

	 /**
	 * Processes incoming messages for user authentication and menu navigation.
	 * Handles the login response and directs users to their appropriate interface
	 * based on their authentication status and user type.
	 *
	 * @param message A Message object containing the command and response data
	 *               Expected to contain either a User/SubscriberDTO object for successful login
	 *               or an error message string for failed attempts
	 * 
	 * @see Message
	 * @see User
	 * @see SubscriberDTO
	 * @see Commands#CheckUsername
	 * 
	 * The method performs the following steps:
	 * 1. Validates the incoming command matches CheckUsername
	 * 2. Processes the response object, which can be:
	 *    - User object for staff members
	 *    - SubscriberDTO object for subscribers
	 *    - String for error messages
	 * 3. Opens the appropriate window based on user type or displays error message
	 */
    public void openMenu(Message message) {
        // Debugging: Log the command
        System.out.println("Received command: " + message.getCmd());

        if (message.getCmd().equals(Commands.CheckUsername)) {
            Object response = message.getObj();

            // Debugging: Log the type of response object
            System.out.println("Response object type: " + response.getClass().getName());

            if (response instanceof User || response instanceof SubscriberDTO) {
                loginMsg("Login successful!");
                
                // Debugging: Log the details of the object
                if (response instanceof User) {
                    User user = (User) response;
                    System.out.println("User details: " + user.getFullName() + ", UserType: " + user.getUserType());
                } else if (response instanceof SubscriberDTO) {
                    SubscriberDTO subscriber = (SubscriberDTO) response;
                    System.out.println("Subscriber details: " + subscriber.getFullName() + ", Status: " + subscriber.getStatus());
                }

                // Open user-specific window based on user type
                Stage currentStage = (Stage) loginBtn.getScene().getWindow();
                openUserSpecificWindow(response, currentStage);
            } else {
                // Debugging: Log if the response is a string
                if (response instanceof String) {
                    System.out.println("Error message from server: " + response);
                } else {
                    System.out.println("Unexpected response type: " + response.getClass().getName());
                }

                loginMsg((String) message.getObj());
            }
        } else {
            // Debugging: Log invalid command handling
            System.out.println("Invalid command received: " + message.getCmd());
            loginMsg("Invalid username or password. Please try again.");
        }
    }



	/**
	 * Opens a specific window based on the user type or subscriber details.
	 *
	 * @param userObject   The object containing user details (can be User or
	 *                     SubscriberDTO).
	 * @param currentStage The current stage of the application.
	 */
	private void openUserSpecificWindow(Object userObject, Stage currentStage) {
		try {
			// Close or hide the current stage
			currentStage.hide();

			if (userObject instanceof SubscriberDTO) {
				// If it's a SubscriberDTO, open the MemberMenuController
				SubscriberDTO subscriber = (SubscriberDTO) userObject;
				System.out.println("Login successful for Subscriber: " + subscriber.getFullName());
				MemberMenuController memberMenuController = new MemberMenuController();
				memberMenuController.start(new Stage(), subscriber);
			} else if (userObject instanceof User) {
				// If it's a User, open the LibrarianScreenController
				User user = (User) userObject;

				if (user.getUserType() == UserType.Librarian) {
					System.out.println("Login successful for Librarian: " + user.getFullName());
					LibrarianScreenController librarianScreenController = new LibrarianScreenController();
					librarianScreenController.start(new Stage(), user);
				} else {
					// Handle unexpected UserType
					loginMsg("Access denied. Only librarians are allowed for this role.");
					currentStage.show(); // Re-show the login stage if the UserType is invalid
				}
			} else {
				// Handle invalid object types
				loginMsg("Invalid user object received. Access denied.");
				currentStage.show();
			}

		} catch (Exception e) {
			e.printStackTrace();
			loginMsg("Error loading the menu for the provided user details.");
			currentStage.show(); // Re-show the login stage in case of an error
		}
	}

	@FXML
	private void handleSearch(ActionEvent event) {
		try {
			Stage currentStage = (Stage) srchBtn.getScene().getWindow();
			// Close or hide the current stage
			currentStage.hide();
			BookSearchController bookSearchController = new BookSearchController();
			bookSearchController.start(new Stage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void loginMsg(String msg) {
		loginMsgLabel.setText(msg); // Display the message in the Label
		loginMsgLabel.setVisible(true);
	}

	@FXML
	private void handleCancel() {
		this.idField.getScene().getWindow().hide();
	}
}
