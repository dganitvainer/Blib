package member;

import java.io.IOException;
import java.util.ArrayList;
import client.BLibClient;
import client.ClientController;
import enums.Commands;
import entities.SubscriberDTO;
import entities.ActivityLog;
import entities.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
/**
 * The MemberCardController class handles the member's card UI, allowing the user to view and update their personal information.
 * It also provides functionality for displaying activity logs and navigating back to the member menu.
 */
	
public class MemberCardController {

	@FXML
	private TextField idField;
	@FXML
	private TextField statusField;
	@FXML
	private TextField usernameField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField phoneField;
	@FXML
	private TextField addressField;
	@FXML
	private TextField passwordField;
	@FXML
	private Button saveButton;
	@FXML
	private Button cancelButton;
	@FXML
	private Button activityBtn;

	private SubscriberDTO loggedMember;

	/* needs to add status of update label */

    /**
     * Sets the logged-in member and populates the fields with their information.
     * 
     * @param subscriber The logged-in member (SubscriberDTO).
     */
	public void setLoggedMember(SubscriberDTO subscriber) {
	    this.loggedMember = subscriber;

	    if (subscriber != null) {
	        // Populate the non-editable fields
	        idField.setText(String.valueOf(subscriber.getUserId()));
	        statusField.setText(subscriber.getStatus().toString());

	        // Populate the editable fields
	        usernameField.setText(subscriber.getFullName());
	        emailField.setText(subscriber.getEmail());
	        phoneField.setText(subscriber.getPhone());
	        addressField.setText(subscriber.getAddress());
	        passwordField.setText(subscriber.getPassword());
	    }
	}
    /**
     * Starts the MemberCardController by loading the FXML file and displaying the scene.
     * 
     * @param currentStage The primary stage where the scene will be displayed.
     * @param loggedMember The logged-in member whose details are displayed.
     * @throws Exception if there is an error loading the FXML file or initializing the scene.
     */

	public void start(Stage currentStage, SubscriberDTO loggedMember) throws Exception {
	    // Load the FXML file
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/member/MemberCard.fxml"));
	    Parent root = loader.load();

	    // Retrieve the controller instance from the FXML loader
	    MemberCardController controller = loader.getController();

	    // Set the logged member in the controller
	    controller.setLoggedMember(loggedMember);

	    // Set the scene and show the current stage
	    Scene scene = new Scene(root);
	    currentStage.setTitle("Member Card");
	    currentStage.setScene(scene);
	    currentStage.show();
	}
    /**
     * Handles the save button click event to update the member's information.
     * Sends the updated member data to the server for processing.
     */

	@FXML
	private void handleSaveChanges() {
		if (loggedMember != null) {
			// Update the logged member's details locally
			loggedMember.setFullName(usernameField.getText());
			loggedMember.setEmail(emailField.getText());
			loggedMember.setPhone(phoneField.getText());
			loggedMember.setAddress(addressField.getText());
			loggedMember.setPassword(passwordField.getText());

			Message updateMessage = new Message(loggedMember, Commands.UpdateMember);

			try {// send the message to the server
				System.out.println("Creating UpdateMember message: " + updateMessage);

				ClientController.client.sendMessageToServer(updateMessage); // ********************

				System.out.println("Update request sent to the server for: " + loggedMember.getFullName()
						+ "  Member ID: " + loggedMember.getUserId());
			} catch (Exception e) {
				System.err.println("Error sending update request to server: " + e.getMessage());
			}
		}
	}
    /**
     * Handles the event of clicking the activity button to display the member's activity logs.
     * 
     * @param event The action event triggered by the activity button.
     * @throws Exception if there is an error navigating to the activity logs screen.
     */

    @FXML
    private void handleActivityLogsResponse(ActionEvent event) throws Exception {
        try {
            Stage currentStage = (Stage) activityBtn.getScene().getWindow();
            currentStage.hide();
            MemberActivityLogsController memberActivityLogsController = new MemberActivityLogsController();
            memberActivityLogsController.start(new Stage(), loggedMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    /**
     * Handles the server's response to the member's update request.
     * 
     * @param response The response message received from the server after attempting to update the member's details.
     */

	public void handleUpdateResponse(Message response) {
		Platform.runLater(() -> {
			if (response.getObj() instanceof String) {
				String result = (String) response.getObj();
				System.out.println("UpdateMember Response: " + result);

				if (result.equals("Success")) {
					showSuccess("Update Details Successfully!");
				} else {
					showError("Update Failed", result);
				}
			} else {
				showError("Unexpected Response", "Invalid data received from server.");
			}
		});
	}
    /**
     * Displays a success message in a popup alert.
     * 
     * @param message The success message to be displayed.
     */

	private void showSuccess(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Success");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
    /**
     * Displays an error message in a popup alert.
     * 
     * @param title The title of the error alert.
     * @param message The error message to be displayed.
     */

	private void showError(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
    /**
     * Handles the event of clicking the exit button to navigate back to the member menu.
     * 
     * @throws Exception if there is an error loading the member menu screen.
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
		Stage currentStage = (Stage) cancelButton.getScene().getWindow();
		currentStage.setScene(new Scene(root));
		currentStage.setTitle("Member Menu");
	}
    /**
     * Initializes the MemberCardController by setting the instance in the BLibClient.
     */

	@FXML
	public void initialize() {
		// Set this instance in BLibClient
		BLibClient.setMemberCardController(this);
	}
}
