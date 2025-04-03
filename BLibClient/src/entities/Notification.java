package entities;

import java.io.Serializable;
import enums.NotificationType;

/**
 * Represents a notification within the library management system.
 * <p>
 * This class encapsulates all details of a notification, including its type, sender, recipient,
 * message content, and dates related to its creation and dispatch.
 * </p>
 * 
 * <p>
 * This class implements {@link Serializable} to allow notifications to be serialized and transmitted
 * or stored as needed.
 * </p>
 * 
 * @author 
 */
public class Notification implements Serializable {
    
    /**
     * Serialization identifier for ensuring class compatibility during deserialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for the notification.
     */
    private int notificationId;

    /**
     * Unique identifier for the subscriber associated with this notification.
     */
    private int subscriberId;

    /**
     * Message content of the notification.
     */
    private String message;

    /**
     * Date when the notification was created.
     */
    private String notificationDate;



    /**
     * Type of the notification, as defined in {@link NotificationType}.
     */
    private NotificationType type;

    /**
     * Indicates whether the notification has been selected (e.g., for user interaction).
     */
    private boolean selected; // Default value is false

    /**
     * Constructs a new Notification instance with the provided details.
     * 
     * @param notificationId the unique identifier of the notification
     * @param subscriberId the unique identifier of the associated subscriber
     * @param message the content of the notification
     * @param notificationDate the creation date of the notification
     * @param type the type of the notification
     */
    public Notification(int notificationId, int subscriberId, String message, String notificationDate, 
                        NotificationType type) {
        this.notificationId = notificationId;
        this.subscriberId = subscriberId;
        this.message = message;
        this.notificationDate = notificationDate;
        this.type = type;
        this.selected = false; // Default unchecked
    }

    /**
     * Gets the unique identifier of the notification.
     * 
     * @return the notification ID
     */
    public int getNotificationId() { return notificationId; }

    /**
     * Sets the unique identifier of the notification.
     * 
     * @param notificationId the notification ID to set
     */
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    /**
     * Gets the unique identifier of the subscriber associated with this notification.
     * 
     * @return the subscriber ID
     */
    public int getSubscriberId() { return subscriberId; }

    /**
     * Sets the unique identifier of the subscriber associated with this notification.
     * 
     * @param subscriberId the subscriber ID to set
     */
    public void setSubscriberId(int subscriberId) { this.subscriberId = subscriberId; }

    /**
     * Gets the content of the notification.
     * 
     * @return the notification message
     */
    public String getMessage() { return message; }

    /**
     * Sets the content of the notification.
     * 
     * @param message the notification message to set
     */
    public void setMessage(String message) { this.message = message; }

    /**
     * Gets the creation date of the notification.
     * 
     * @return the notification date
     */
    public String getNotificationDate() { return notificationDate; }

    /**
     * Sets the creation date of the notification.
     * 
     * @param notificationDate the notification date to set
     */
    public void setNotificationDate(String notificationDate) { this.notificationDate = notificationDate; }

    /**
     * Gets the type of the notification.
     * 
     * @return the notification type
     */
    public NotificationType getType() { return type; }

    /**
     * Sets the type of the notification.
     * 
     * @param type the notification type to set
     */
    public void setType(NotificationType type) { this.type = type; }

    /**
     * Checks whether the notification is selected.
     * 
     * @return {@code true} if the notification is selected, {@code false} otherwise
     */
    public boolean isSelected() { return selected; }

    /**
     * Sets the selection status of the notification.
     * 
     * @param selected the selection status to set
     */
    public void setSelected(boolean selected) { this.selected = selected; }
}
