package member;

import client.BLibClient;
import enums.SubscriberStatus;
import entities.SubscriberDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the Member Menu screen.
 * <p>
 * This class manages the actions and events related to the member menu, including navigation
 * to other screens, handling user interactions, and displaying relevant member information.
 * </p>
 * 
 * @author [Your Name]
 */
public class MemberMenuController {

    @FXML
    private Label welcomeHeader;

    @FXML
    private Label subscriberStatus;

    @FXML
    private Button searchBtn;

    @FXML
    private Button orderBtn;

    @FXML
    private Button notificationBtn;

    @FXML
    private Button cancelOrderBtn;

    @FXML
    private Button memberCardBtn; // Replacing updateBtn

    @FXML
    private Button mailBtn;

    @FXML
    private Button viewBrrwBookBtn;

    @FXML
    private Button extendBtn;

    @FXML
    private Button logoutBtn;

    /**
     * The logged-in member object containing subscriber details.
     */
    private SubscriberDTO loggedMember;

    /**
     * Sets the logged-in member and updates the display with their name and status.
     * 
     * @param loggedMember the subscriber details of the logged-in member
     */
    public void setLoggedMember(SubscriberDTO loggedMember) {
        this.loggedMember = loggedMember;
        if (loggedMember != null) {
            welcomeHeader.setText("Welcome " + loggedMember.getFullName());

            // Check and set the subscriber status with color
            subscriberStatus.setText("Status: " + loggedMember.getStatus());
            if (loggedMember.getStatus() == SubscriberStatus.FROZEN) {
                subscriberStatus.setStyle("-fx-text-fill: red;");
            } else {
                subscriberStatus.setStyle("-fx-text-fill: green;");
            }
        }
    }

    /**
     * Starts the Member Menu screen.
     * 
     * @param currentStage the current stage to display the Member Menu
     * @param user the logged-in member's details
     * @throws Exception if an error occurs while loading the screen
     */
    public void start(Stage currentStage, SubscriberDTO user) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/member/MemberMenu.fxml"));
        Parent root = loader.load();

        // Pass logged-in member to the controller
        MemberMenuController controller = loader.getController();
        controller.setLoggedMember(user);

        // Set scene and show current stage
        Scene scene = new Scene(root);
        currentStage.setTitle("Member Menu");
        currentStage.setScene(scene);
        currentStage.show();
    }

    /**
     * Initializes the controller and registers it with the client.
     */
    @FXML
    public void initialize() {
        BLibClient.memberMenuController = this;
    }

    /**
     * Logs out the current user, clears their session, and navigates to the Main Menu.
     * 
     * @param event the action event triggered by clicking the logout button
     * @throws Exception if an error occurs while loading the Main Menu
     */
    @FXML
    private void handleLogout(ActionEvent event) throws Exception {
        loggedMember = null; // Clear logged-in object

        // Load Main Menu
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/logIn/MainMenu.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Main Menu");
        stage.show();

        // Close Member Menu
        Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Handles the Order button action, navigating to the Book Search screen.
     * 
     * @param event the action event triggered by clicking the Order button
     * @throws Exception if an error occurs while navigating to the Book Search screen
     */
    @FXML
    private void handleOrderBook(ActionEvent event) throws Exception {
        try {
            Stage currentStage = (Stage) orderBtn.getScene().getWindow();
            currentStage.hide();
            BookSearchMemberController bookSearchMemberController = new BookSearchMemberController();
            bookSearchMemberController.start(new Stage(), loggedMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the Notification button action, navigating to the Notifications screen.
     * 
     * @param event the action event triggered by clicking the Notification button
     * @throws Exception if an error occurs while navigating to the Notifications screen
     */
    @FXML
    private void handleNotification(ActionEvent event) throws Exception {
        try {
            Stage currentStage = (Stage) notificationBtn.getScene().getWindow();
            currentStage.hide();
            NotificationMemberController notificationMemberController = new NotificationMemberController();
            notificationMemberController.start(new Stage(), loggedMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the Member Card button action, navigating to the Member Card screen.
     * 
     * @param event the action event triggered by clicking the Member Card button
     * @throws Exception if an error occurs while navigating to the Member Card screen
     */
    @FXML
    private void handleMemberCard(ActionEvent event) throws Exception {
        try {
            Stage currentStage = (Stage) memberCardBtn.getScene().getWindow();
            currentStage.hide();
            MemberCardController memberCardController = new MemberCardController();
            memberCardController.start(new Stage(), loggedMember);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the logged-in member to the View Borrowed Books screen to retrieve their borrowed books.
     * 
     * @param event the action event triggered by clicking the View Borrowed Books button
     * @throws Exception if an error occurs while navigating to the View Borrowed Books screen
     */
    @FXML
    private void handleViewBorrowedBooks(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/member/MembersBorrowHistory.fxml"));
        Parent root = loader.load();

        // Pass the logged-in member to the View Borrowed Books controller
        ViewBorrowedBooksController controller = loader.getController();
        BLibClient.ViewBorrowedBooksController = controller;
        controller.setLoggedMember(loggedMember);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("View Borrowed Books");
        stage.show();
        Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
        currentStage.close();
    }

    /**
     * Handles the Mail button action.
     * <p>
     * This method is currently a placeholder and should be implemented to provide functionality.
     * </p>
     * 
     * @param event the action event triggered by clicking the Mail button
     * @throws Exception if an error occurs
     */
    @FXML
    private void handleMailBtn(ActionEvent event) throws Exception {
        // TODO: Implement mail functionality
    }
}
