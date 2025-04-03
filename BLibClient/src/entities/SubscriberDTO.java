package entities;

import java.io.Serializable;

import enums.SubscriberStatus;

/**
 * Data Transfer Object (DTO) for subscriber information in the library system.
 * This class contains detailed subscriber information for transfer between
 * system components. Implements Serializable for network transmission and
 * persistence.
 * 
 * @author
 */
public class SubscriberDTO implements Serializable {
	/** Serial version UID for serialization */
	private static final long serialVersionUID = 1L;

	/** An identifier for the subscriber */
	private int userId;

	/** Full name of the subscriber */
	private String fullName;

	/** Subscriber's password (hashed) */
	private String password;

	/** Subscriber's email address */
	private String email;

	/** Subscriber's phone number */
	private String phone;

	/** Current status of the subscriber (ACTIVE or FROZEN) */
	private SubscriberStatus status;

	/** Subscriber's physical address */
	private String address;

	/**
	 * Constructs a new SubscriberDTO with the specified details.
	 *
	 * @param userId   An identifier for the subscriber
	 * @param fullName Full name of the subscriber
	 * @param password Subscriber's password (hashed)
	 * @param email    Subscriber's email address
	 * @param phone    Subscriber's phone number
	 * @param status   Current status of the subscriber
	 * @param address  Subscriber's physical address
	 */
	public SubscriberDTO(int userId, String fullName, String password, String email, String phone,
			SubscriberStatus status, String address) {
		this.userId = userId;
		this.fullName = fullName;
		this.password = password;
		this.email = email;
		this.phone = phone;
		this.status = status;
		this.address = address;
	}

	/**
	 * Gets the subscriber's full name.
	 * 
	 * @return The full name of the subscriber
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Gets the subscriber's unique identifier.
	 * 
	 * @return The user ID
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Gets the subscriber's password (hashed).
	 * 
	 * @return The hashed password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Gets the subscriber's email address.
	 * 
	 * @return The email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Gets the subscriber's phone number.
	 * 
	 * @return The phone number
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Gets the subscriber's current status.
	 * 
	 * @return The current status (ACTIVE or FROZEN)
	 */
	public SubscriberStatus getStatus() {
		return status;
	}

	/**
	 * Gets the subscriber's physical address.
	 * 
	 * @return The physical address
	 */
	public String getAddress() {
		return address;
	}

	// Setters
	/**
	 * Sets the subscriber's full name.
	 * 
	 * @param fullName The new full name
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Sets the subscriber's unique identifier.
	 * 
	 * @param userId The new user ID
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Sets the subscriber's password (hashed).
	 * 
	 * @param passwordHash The new hashed password
	 */
	public void setPassword(String passwordHash) {
		this.password = passwordHash;
	}

	/**
	 * Sets the subscriber's email address.
	 * 
	 * @param email The new email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the subscriber's phone number.
	 * 
	 * @param phone The new phone number
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Sets the subscriber's status.
	 * 
	 * @param status The new status
	 */
	public void setStatus(SubscriberStatus status) {
		this.status = status;
	}

	/**
	 * Sets the subscriber's physical address.
	 * 
	 * @param address The new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
}
