package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.net.InetAddress;
import java.sql.SQLException;
import ocsf.server.ConnectionToClient;
import Server.ServerUI;
import entities.ClientDetails;
import jdbc.mysqlConnection;
import jdbc.dbHandler;

/**
 * The ServerPortController class handles the server's graphical user interface (GUI). 
 * It allows users to manage the server, connect or disconnect from the server, handle client connections,
 * and interact with the database for server operations.
 */

public class ServerPortController {

    @FXML
    private TextField ipAddressT;
    @FXML
    private TextField portT;
    @FXML
    private TextField dbPathT;
    @FXML
    private TextField dbUsernameT;
    @FXML
    private TextField dbPasswordT;
    @FXML
    private Button connectBtn;
    @FXML
    private Button disconnectBtn;

    @FXML
    private TableView<ClientDetails> tableView; // Corrected type
    private ObservableList<ClientDetails> clientDetailsList = FXCollections.observableArrayList(); // ObservableList

    @FXML
    private TableColumn<ClientDetails, String> ipT;
    @FXML
    private TableColumn<ClientDetails, String> hostT;
    @FXML
    private TableColumn<ClientDetails, Boolean> statusT;

    @FXML
    private Label statusLabel;

    private String getPort() {
        return portT.getText();
    }

    private String getDbPath() {
        return dbPathT.getText();
    }

    private String getDbUsername() {
        return dbUsernameT.getText();
    }

    private String getDbPassword() {
        return dbPasswordT.getText();
    }
    /**
     * Initializes the ServerPortController, binds the table columns to ClientDetails properties,
     * and sets up the ObservableList for displaying client details in the TableView.
     */

    @FXML
    public void initialize() {
        // Bind columns to ClientDetails properties
        ipT.setCellValueFactory(new PropertyValueFactory<>("ip"));
        hostT.setCellValueFactory(new PropertyValueFactory<>("hostName"));
        statusT.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set the ObservableList to the TableView
        tableView.setItems(clientDetailsList);
    }
    /**
     * Disconnects the server and clears the client details table.
     * 
     * @param event The action event triggered by the disconnect button.
     */

    public void disconnect(ActionEvent event) {
        try {
            ServerUI.disconnect();
            clientDetailsList.clear(); // Clear the ObservableList
            connectBtn.setDisable(false);
            disconnectBtn.setDisable(true);
            disableDataInput(false);
            updateStatusLabel("Server has been disconnected.", "orange");
        } catch (Exception e) {
            updateStatusLabel("Error during disconnection: " + e.getMessage(), "red");
            e.printStackTrace();
        }
    }
    /**
     * Connects the server and the database, handles validation of input fields,
     * and updates the UI with success or error messages.
     * 
     * @param event The action event triggered by the connect button.
     */

    public void connect(ActionEvent event) {
        // Validate inputs
        if (getDbPath().isEmpty() || getDbUsername().isEmpty() || getDbPassword().isEmpty()) {
            updateStatusLabel("Please fill in all database fields.", "red");
            return;
        }

        if (!ServerUI.isServerRunning()) {
            try {
                // Attempt to start the server
                if (ServerUI.runServer(this.getPort())) {
                    // Attempt to connect to the database
                    mysqlConnection sqlconn = new mysqlConnection(getDbPath(), getDbUsername(), getDbPassword());
                    dbHandler dbconn = new dbHandler(sqlconn.ConnectToDB());

                    if (dbconn == null || dbconn.getConnection() == null) {
                        throw new SQLException("Unable to establish database connection.");
                    }

                    // Database connected successfully
                    ServerUI.sv.setDbHandlerController(dbconn);
                    ServerUI.sv.setServerPortController(this);

                    // Update UI for successful connection
                    disableDataInput(true);
                    connectBtn.setDisable(true);
                    disconnectBtn.setDisable(false);
                    updateStatusLabel("Server and database connected successfully.", "green");
                }
            } catch (SQLException e) {
                updateStatusLabel("Database connection failed: " + e.getMessage(), "red");
                ServerUI.disconnect();
            } catch (Exception e) {
                updateStatusLabel("Failed to connect.", "red");
                ServerUI.disconnect();
            }
        } else {
            updateStatusLabel("The server is already running.", "orange");
        }
    }
    /**
     * Updates the status label with a message and sets its text color.
     * 
     * @param message The message to be displayed on the status label.
     * @param color   The color for the status message (e.g., "green", "red").
     */

    private void updateStatusLabel(String message, String color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: " + color + ";");
        }
    }
    /**
     * Disables or enables input fields for server connection details based on the given condition.
     * 
     * @param condition If true, disables the input fields; if false, enables them.
     */

    void disableDataInput(boolean condition) {
        ipAddressT.setDisable(condition);
        portT.setDisable(condition);
        dbPathT.setDisable(condition);
        dbUsernameT.setDisable(condition);
        dbPasswordT.setDisable(condition);
    }
    /**
     * Starts the server GUI and initializes the user interface.
     * 
     * @param primaryStage The primary stage for the server GUI.
     * @throws Exception If an error occurs while loading the GUI or retrieving the IP address.
     */

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerPortScreen.fxml"));
        System.out.println("Starting server GUI");
        loader.setController(this); // Set the controller
        Parent root = loader.load();
        Scene scene = new Scene(root);
        primaryStage.setTitle("Server");
        primaryStage.setScene(scene);
        primaryStage.show();
        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            ipAddressT.setText(ipAddress);
        } catch (Exception e) {
            ipAddressT.setText("Unable to get IP address");
        }
    }
    /**
     * Adds a new client to the client details table.
     * 
     * @param client The client object to be added to the table.
     */

    public void addClientToTable(ConnectionToClient client) {
        Platform.runLater(() -> {
            if (client.getInetAddress() != null) {
                ClientDetails clientDetails = new ClientDetails(
                    client.getInetAddress().getCanonicalHostName(),
                    client.getInetAddress().getHostAddress(),
                    true
                );
                clientDetailsList.add(clientDetails); // Add to ObservableList
            } else {
                System.out.println("Cannot add client to table: InetAddress is null.");
            }
        });
    }


    
}
