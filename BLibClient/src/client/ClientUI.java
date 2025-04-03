package client;

import javafx.application.Application;
import javafx.stage.Stage;
import logIn.ConnectionScreenController;

/**
 * The ClientUI class is the main entry point for launching the client application.
 * It initializes and launches the user interface for the client application,
 * starting with the connection screen.
 */
public class ClientUI extends Application {

	/**
	* The main entry point of the client application.
	* Launches the JavaFX application interface.
	*
	* @param args the command line arguments passed to the application
	* @throws Exception if the application fails to launch or encounters a fatal error
	* @throws IllegalStateException if the JavaFX runtime is not properly initialized
	* @throws IllegalArgumentException if required launch parameters are invalid
	* @throws RuntimeException if a runtime error occurs during application startup
	* @see javafx.application.Application#launch(String...)
	*/

	public static void main(String args[]) throws Exception {
		launch(args);
	} // end main

	/**
	 * Initializes the UI and displays the connection screen.
	 * 
	 * @param primaryStage the primary stage for this application
	 * @throws Exception if an error occurs during the initialization
	 */

	@Override
	public void start(Stage primaryStage) throws Exception {

		ConnectionScreenController aFrame = new ConnectionScreenController(); 

		aFrame.start(primaryStage);

	}

}
