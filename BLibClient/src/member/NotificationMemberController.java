package member;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.Stage;
import entities.Message;
import client.BLibClient;
import client.ClientController;
import enums.Commands;
import entities.Notification;
import entities.SubscriberDTO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
/**
 * The NotificationMemberController class manages the notifications UI for a logged-in member.
 * It allows the member to view and delete notifications.
 */

public class NotificationMemberController {
	@FXML
	private TableView<Notification> notificationsTable;
	@FXML
	private TableColumn<Notification, Boolean> checkBoxColumn;
	@FXML
	private TableColumn<Notification, String> typeColumn;
	@FXML
	private TableColumn<Notification, String> dateColumn;
	@FXML
	private TableColumn<Notification, String> messageColumn;

	@FXML
	private Button backButton;
	@FXML
	private Button  deleteButton;
	
	private ObservableList<Notification> notifications = FXCollections.observableArrayList();

	private SubscriberDTO loggedMember;
	/**
	 * Sets the currently logged-in member and initializes their notifications.
	 * If a valid subscriber is provided, their notifications are loaded.
	 * If null is provided, a warning is logged and notifications are not loaded.
	 *
	 * @param subscriber The SubscriberDTO object representing the logged-in member.
	 *                  Can be null, which indicates a logout or invalid login attempt.
	 * @see SubscriberDTO
	 */
	public void setLoggedMember(SubscriberDTO subscriber) {
		this.loggedMember = subscriber;

		if (loggedMember != null) {
			System.out.println("Logged member set: " + loggedMember.getUserId());
			loadNotifications(); // Explicitly load notifications after setting the member
		} else {
			System.err.println("Logged member is null. Notifications cannot be loaded.");
		}
	}
    /**
     * Starts the NotificationMemberController by loading the FXML file and displaying the scene.
     * 
     * @param stage The primary stage where the scene will be displayed.
     * @param subscriber The logged-in member whose notifications will be displayed.
     * @throws IOException if there is an error loading the FXML file or initializing the scene.
     */

	public void start(Stage stage, SubscriberDTO subscriber) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/member/NotificationsMember.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Notifications");
		stage.show();

		// Access the controller and set the logged member
		NotificationMemberController controller = loader.getController();
		controller.setLoggedMember(subscriber);
	}
    /**
     * Initializes the table columns and bindings for the notifications view.
     */

	@FXML
	public void initialize() {
		BLibClient.notificationMemberController = this;
		// Initialize checkbox column
	    checkBoxColumn.setCellValueFactory(cellData -> {
	        Notification notification = cellData.getValue();
	        SimpleBooleanProperty property = new SimpleBooleanProperty(notification.isSelected());

	        // Bind the property to the Notification object's selected field
	        property.addListener((observable, oldValue, newValue) -> notification.setSelected(newValue));

	        return property;
	    });
	    checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));

	    // Initialize other columns
	    typeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType().name()));
	    dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotificationDate()));
	    messageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMessage()));

	}
    /**
     * Loads notifications for the logged-in member by sending a request to the server.
     */

	private void loadNotifications() {
		try {
			if (loggedMember == null) {
				System.err.println("No logged member set. Cannot load notifications.");
				return;
			}

			System.out.println("Fetching notifications for subscriber ID: " + loggedMember.getUserId());
			// Send request to server with the subscriber's ID
			Message request = new Message(loggedMember.getUserId(), Commands.GetNotifications);
			ClientController.client.sendMessageToServer(request);
		} catch (Exception e) {
			System.err.println("Failed to load notifications: " + e.getMessage());
			e.printStackTrace();
		}
	}
    /**
     * Handles the response from the server with the notifications for the logged-in member.
     * 
     * @param notificationsFromServer The list of notifications received from the server.
     */

	public void handleNotificationsResponse(ArrayList<Notification> notificationsFromServer) {
		notifications.clear();
		notifications.addAll(notificationsFromServer);
		notificationsTable.setItems(notifications);
	}
	
    /**
     * Deletes the selected notifications after confirming the deletion with the user.
     */

	@FXML
	private void deleteSelectedNotifications() {
	    ArrayList<Integer> notificationsToDelete = new ArrayList<>();

	    // Collect IDs of selected notifications
	    for (Notification n : notifications) {
	        if (n.isSelected()) {
	            notificationsToDelete.add(n.getNotificationId());
	        }
	    }

	    if (notificationsToDelete.isEmpty()) {
	        System.out.println("No notifications selected for deletion.");
	        return;
	    }
	    
	    // Show confirmation alert
	    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
	    alert.setTitle("Confirm Deletion");
	    alert.setHeaderText("You are about to delete notifications.");
	    alert.setContentText("Are you sure you want to proceed?");

	    // Wait for user response
	    Optional<ButtonType> result = alert.showAndWait();
	    if (result.isPresent() && result.get() == ButtonType.OK) {
	        System.out.println("Deleting notifications: " + notificationsToDelete);

	        // Send request to server
	        Message request = new Message(notificationsToDelete, Commands.DeleteNotifications);
	        ClientController.client.sendMessageToServer(request);
	    } else {
	        System.out.println("Deletion canceled by user.");
	    }

	}

    /**
     * Handles the response from the server after attempting to delete notifications.
     * 
     * @param success A boolean indicating whether the deletion was successful.
     */

	// Handle delete response from the server
	public void handleDeleteNotificationsResponse(boolean success) {
	    if (success) {
	        System.out.println("Notifications successfully deleted.");
	        loadNotifications(); // Refresh the table after deletion
	    } else {
	        System.err.println("Failed to delete notifications.");
	    }
	}
    /**
     * Handles the back button event and navigates back to the member menu screen.
     * 
     * @param event The action event triggered by the back button.
     * @throws Exception if there is an error navigating to the member menu.
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
