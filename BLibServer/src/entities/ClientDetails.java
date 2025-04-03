package entities;

/**
 * Represents the details of a client, including its IP address, host name, and
 * status. This class provides methods to access these details and override the
 * equals method for comparison based on the IP address and host name.
 * 
 */
public class ClientDetails {

	/** The IP address of the client. */
	private String ip;

	/** The host name of the client. */
	private String hostName;

	/** The status of the client (e.g., connected or disconnected). */
	private boolean status;

	/**
	 * Constructs a new {@code ClientDetails} object with the specified IP address,
	 * host name, and status.
	 * 
	 * @param hostName The host name of the client.
	 * @param ip       The IP address of the client.
	 * @param status   The status of the client.
	 */
	public ClientDetails(String hostName, String ip, boolean status) {
		this.ip = ip;
		this.hostName = hostName;
		this.status = status;

	}

	/**
	 * Returns the IP address of the client.
	 * 
	 * @return The IP address of the client.
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Returns the host name of the client.
	 * 
	 * @return The host name of the client.
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Returns the status of the client.
	 * 
	 * @return The status of the client.
	 */
	public boolean getStatus() {
		return status;
	}

	/**
	 * Compares this {@code ClientDetails} object with another object for equality.
	 * The comparison is based on the IP address and host name.
	 * 
	 * @param obj The object to compare with.
	 * @return {@code true} if the IP address and host name are the same,
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		ClientDetails tmp = (ClientDetails) obj;
		if (tmp.getIp().equals(ip) && tmp.getHostName().equals(hostName))
			return true;
		return false;
	}
}