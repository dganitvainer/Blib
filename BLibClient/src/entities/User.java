package entities;

import java.io.Serializable;

import enums.UserType;

/**
 * Represents a user in the library system. This class contains common user
 * information for all types of users in the system (subscribers, librarians,
 * etc.). Implements Serializable for data persistence.
 * 
 * @author
 */
public class User implements Serializable {
	/** Serial version UID for serialization */
	private static final long serialVersionUID = 1L;

	/** An identifier for the user */
	private int userId;

	/** Full name of the user */
	private String fullName;

	/** User's password (hashed) */
	private String password;

	/** User's email address */
	private String email;

	/** User's phone number */
	private String phone;

	/** Type of user (from UserType enum) */
	private UserType userType;

	/** Timestamp when the user account was created */
	private String createdAt;

	/** User's physical address */
	private String address;

	/**
	 * Constructs a new User with the specified details.
	 *
	 * @param userId       An identifier for the user
	 * @param fullName     Full name of the user
	 * @param username     Username for the account
	 * @param passwordHash Hashed password for the account
	 * @param email        User's email address
	 * @param phone        User's phone number
	 * @param userType     Type of user (from UserType enum)
	 * @param createdAt    Timestamp when the account was created
	 * @param address      User's physical address
	 */
	public User(int userId, String fullName, String username, String passwordHash, String email, String phone,
			UserType userType, String createdAt, String address) {
		this.userId = userId;
		this.fullName = fullName;
		this.password = passwordHash;
		this.email = email;
		this.phone = phone;
		this.userType = userType;
		this.createdAt = createdAt;
		this.address = address;
	}

	// Getters and Setters
	/**
	 * Gets the user's unique identifier.
	 * 
	 * @return The user ID
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * Sets the user's unique identifier.
	 * 
	 * @param userId The new user ID
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Gets the user's full name.
	 * 
	 * @return The full name
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * Sets the user's full name.
	 * 
	 * @param fullName The new full name
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * Gets the user's password (hashed).
	 * 
	 * @return The hashed password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the user's password hash.
	 * 
	 * @param passwordHash The new hashed password
	 */
	public void setPasswordHash(String passwordHash) {
		this.password = passwordHash;
	}

	/**
	 * Gets the user's email address.
	 * 
	 * @return The email address
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the user's email address.
	 * 
	 * @param email The new email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the user's phone number.
	 * 
	 * @return The phone number
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Sets the user's phone number.
	 * 
	 * @param phone The new phone number
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Gets the user's type.
	 * 
	 * @return The user type
	 */
	public UserType getUserType() {
		return userType;
	}

	/**
	 * Sets the user's type.
	 * 
	 * @param userType The new user type
	 */
	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	/**
	 * Gets the user's account creation timestamp
	 * 
	 * @return The timestamp when the user account was created
	 */
	public String getCreatedAt() {
		return createdAt;
	}

	/**
	 * Sets the user's account creation timestamp
	 * 
	 * @param createdAt The the account creation timestamp
	 */
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * Gets the user's physical address
	 * 
	 * @return The user's address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Sets the user's physical address
	 * 
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
}
