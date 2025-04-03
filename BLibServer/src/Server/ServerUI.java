package Server;

import javafx.application.Application;
import javafx.stage.Stage;
import ocsf.server.ConnectionToClient;
import java.io.IOException;
import controller.ServerPortController;

/**
 * The main server user interface class that manages the library server.
 * This class handles starting, running and stopping the server.
 */
public class ServerUI extends Application {

    /** The main server instance */
    public static BLibServer sv = null;
    
    /** Flag to track if server received response */
    public static boolean gotResponse = false;

    /**
     * Main method to launch the server application.
     * 
     * @param args Command line arguments
     * @throws Exception If there's an error starting the application
     */
    public static void main(String args[]) throws Exception {
        launch(args);
    }

    /**
     * Starts the server GUI by loading the server port controller screen.
     * 
     * @param primaryStage The main window of the application
     * @throws Exception If there's an error loading the GUI
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        ServerPortController aFrame = new ServerPortController();
        aFrame.start(primaryStage);
    }

    /**
     * Starts the server on the specified port.
     * 
     * @param p The port number to run the server on
     * @return true if server started successfully, false otherwise
     */
    public static boolean runServer(String p) {
        boolean flag = false;
        int port = 0;
        try {
            port = Integer.parseInt(p);
            sv = new BLibServer(port);
            try {
                sv.listen();
                System.out.println("Server is running on port " + port);
                flag = true;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("ERROR - Could not listen for clients!");
            }
        } catch (Throwable t) {
            System.out.println("ERROR - Could not connect!");
        }
        return flag;
    }

    /**
     * Shuts down the server and disconnects all clients.
     * This method:
     * 1. Sends termination messages to all connected clients
     * 2. Shuts down all running services
     * 3. Closes the server connection
     * 4. Clears the client list
     */
    public static void disconnect() {
        if (isServerRunning() == true) {
            // Disconnect all clients
            for (ConnectionToClient client : BLibServer.clients) {
                gotResponse = false;
                try {
                    client.sendToClient("COMMAND:TERMINATE");
                    client.close();
                } catch (IOException e) {
                    System.out.println("Error sending termination message to client: " + client);
                    e.printStackTrace();
                }
            }

            // Shutdown services if server exists
            if (sv != null) {
                if (sv.getReportManager() != null) {
                    sv.getReportManager().shutdown();
                }
                if (sv.getNotificationService() != null) {
                    sv.getNotificationService().shutdown();
                }
                if (sv.getStatusUpdateService() != null) {
                    sv.getStatusUpdateService().shutdown();
                }
                if (sv.getReservationExpiryService() != null) {
                    sv.getReservationExpiryService().shutdown();
                }
            }

            // Stop and close server
            try {
                sv.stopListening();
                sv.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BLibServer.clients.clear();
            System.out.println("Server Disconnected");
        }
    }

    /**
     * Checks if the server is currently running.
     * 
     * @return true if server is running and listening, false otherwise
     */
    public static boolean isServerRunning() {
        return (sv != null && sv.isListening());
    }
}