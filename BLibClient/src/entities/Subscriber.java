package entities;

import enums.SubscriberStatus;

/**
 * Represents a subscriber in the library system. This class maintains basic
 * subscriber information and their current status in the library system (ACTIVE
 * or FROZEN).
 * 
 * @author
 */
public class Subscriber {
	/** An identifier for the subscriber */
	private int subscriberId;
	/** Current status of the subscriber (ACTIVE or FROZEN) */
	private SubscriberStatus status;

	/**
	 * Constructs a new Subscriber with the specified ID. By default, new
	 * subscribers are assigned an ACTIVE status.
	 *
	 * @param subscriberId An identifier for the subscriber
	 */
	public Subscriber(int subscriberId) {
		this.subscriberId = subscriberId;
		this.status = SubscriberStatus.ACTIVE;
	}

	// Getters and Setters
	/**
	 * Gets the subscriber's unique identifier.
	 * 
	 * @return The subscriber ID
	 */
	public int getSubscriberId() {
		return subscriberId;
	}

	/**
	 * Sets the subscriber's unique identifier.
	 * 
	 * @param subscriberId The new subscriber ID
	 */
	public void setSubscriberId(int subscriberId) {
		this.subscriberId = subscriberId;
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
	 * Sets the subscriber's current status.
	 * @param status Current status of the subscriber
	 */
	public void setStatus(SubscriberStatus status) {
		this.status = status;
	}
}
