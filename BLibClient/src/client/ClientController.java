// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 
package client;

import java.io.*;
import java.net.ConnectException;

import entities.Message;
import enums.Commands;


/**
 * This class constructs the UI for a chat client. It implements the chat
 * interface in order to activate the display() method. Warning: Some of the
 * code here is cloned in ServerConsole
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @version July 2000
 */
public class ClientController {

	// Instance variables **********************************************

	/**
	 * The instance of the client that handles the connection to the server.
	 */
	static public BLibClient client;


	/**
	 * Constructs an instance of the ClientConsole UI.
	 *
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 * @throws ConnectException If unable to establish connection with the server
	 */
	public ClientController(String host, int port) throws ConnectException {
		try {
			client = new BLibClient(host, port, this);
			Message msg = new Message(null, Commands.ConnectClient);
			client.sendToServer(msg);
		} catch (IOException exception) {
			throw new ConnectException("Unable to connect to the IP address");
			// System.exit(1);
		}
	}

	/**
	 * This method overrides the method in the ChatIF interface. It displays a
	 * message onto the screen.
	 *
	 * @param message The string to be displayed.
	 */
	public void display(String message) {
		System.out.println("> " + message);
	}
}
