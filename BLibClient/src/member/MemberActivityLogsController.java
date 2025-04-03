package member;

import client.BLibClient;
import client.ClientController;
import entities.ActivityLog;
import entities.Message;
import entities.SubscriberDTO;
import enums.Commands;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Controller for the Member Activity Logs screen.
 * This class handles retrieving and displaying the activity logs of a specific subscriber.
 */
public class MemberActivityLogsController {

    @FXML
    private TableView<ActivityLog> activityLogsTable;
    @FXML
    private TableColumn<ActivityLog, String> dateColumn; // Timestamp column
    @FXML
    private TableColumn<ActivityLog, String> messageColumn; // Activity message column
    @FXML
    private TableColumn<ActivityLog, String> librarianIdColumn; // Librarian ID column
    @FXML
    private Button logoutBtn;

    private SubscriberDTO loggedMember;

    /**
     * Sets the logged-in member for this controller.
     *
     * @param subscriber The subscriber whose activity logs will be displayed.
     */
    public void setLoggedMember(SubscriberDTO subscriber) {
        this.loggedMember = subscriber;
        if (subscriber != null) {
            fetchActivityLogs(); // Fetch logs after setting the subscriber
        }
    }
    /**
     * Initializes the Member Activity Logs window.
     *
     * @param stage     The stage where the scene will be displayed.
     * @param subscriber The subscriber whose activity logs are being accessed.
     * @throws IOException If the FXML file fails to load.
     */
    public void start(Stage stage, SubscriberDTO subscriber) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/member/MemberActivityLogs.fxml"));
        Parent root = loader.load();

        // Get controller before calling fetchActivityLogs()
        MemberActivityLogsController controller = loader.getController();
        controller.setLoggedMember(subscriber);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Member Activity Logs");
        stage.show();
    }
    /**
     * Initializes the table columns when the scene is loaded.
     */
    @FXML
    public void initialize() {
        System.out.println("DEBUG: Initializing MemberActivityLogsController...");

        // Setup table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("activityDate"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        librarianIdColumn.setCellValueFactory(new PropertyValueFactory<>("librarianId"));
        
        BLibClient.memberActivityLogsController = this;
    }

    /**
     * Populates the activity logs table with data.
     *
     * @param logs A list of activity logs retrieved from the database.
     */
    public void setActivityLogs(ArrayList<ActivityLog> logs) {
        System.out.println("DEBUG: Setting activity logs in table...");
        ObservableList<ActivityLog> logData = FXCollections.observableArrayList(logs);
        activityLogsTable.setItems(logData);
    }

    /**
     * Handles the logout action by redirecting the user to the Member Menu screen.
     */
    @FXML
    private void handleLogout() {
        try {
            System.out.println("DEBUG: Logging out and returning to Member Menu...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/member/MemberMenu.fxml"));
            Parent root = loader.load();

            MemberMenuController memberMenuController = loader.getController();
            memberMenuController.setLoggedMember(loggedMember);

            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Member Menu");
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load MemberMenu.fxml - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Requests the activity logs from the server for the logged-in subscriber.
     */
    public void fetchActivityLogs() {
        if (loggedMember == null) {
            System.err.println("ERROR: No logged member set. Cannot fetch activity logs.");
            return;
        }

        System.out.println("DEBUG: Fetching activity logs for Subscriber ID: " + loggedMember.getUserId());

        try {
            Message activityRequest = new Message(loggedMember.getUserId(), Commands.GetActivityLogsByMember);
            ClientController.client.sendMessageToServer(activityRequest);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to send activity log request - " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the server response containing the activity logs.
     *
     * @param logs A list of activity logs retrieved from the server.
     */
    public void handleActivityLogsResponse(ArrayList<ActivityLog> logs) {
        Platform.runLater(() -> {
            if (logs == null || logs.isEmpty()) {
                System.out.println("INFO: No activity logs found for this member.");
                return;
            }
            System.out.println("DEBUG: Received " + logs.size() + " activity logs from server.");
            setActivityLogs(logs);
        });
    }
}
