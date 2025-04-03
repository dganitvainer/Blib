package enums;

/**
 * Represents the possible notifications in the library management system.
 * <p>
 * This enum defines all possible types of notifications that can be sent or received
 * within the library system, ensuring consistent communication between users
 * and the system.
 * </p>
 * <ul>
 * <li>{@link #REMINDER} - Notification to remind users to return borrowed books.</li>
 * <li>{@link #RESERVATION_READY} - Notification to inform users that a reserved book is ready for pickup.</li>
 * <li>{@link #LATE_RETURN} - Notification to alert users about overdue book returns.</li>
 * <li>{@link #EXTENSION_APPROVED} - Notification to inform users that their extension request has been approved.</li>
 * <li>{@link #EXTENSION_DECLINED} - Notification to inform users that their extension request has been declined.</li>
 * <li>{@link #CANCELLED_RESERVATION} - Notification to inform users that their reservation has been canceled.</li>
 * <li>{@link #RESERVATION_EXPIRED} - Notification to inform users that their reservation has expired.</li>
 * </ul>
 * 
 */
public enum NotificationType {

    /**
     * Notification to remind users to return borrowed books.
     */
    REMINDER,

    /**
     * Notification to inform users that a reserved book is ready for pickup.
     */
    RESERVATION_READY,

    /**
     * Notification to alert users about overdue book returns.
     */
    LATE_RETURN,

    /**
     * Notification to inform users that their extension request has been approved.
     */
    EXTENSION_APPROVED,

    /**
     * Notification to inform users that their extension request has been declined.
     */
    EXTENSION_DECLINED,

    /**
     * Notification to inform users that their reservation has been canceled.
     */
    CANCELLED_RESERVATION,

    /**
     * Notification to inform users that their reservation has expired.
     */
    RESERVATION_EXPIRED,
    
    /**
     * Other relevant reasoning.
     */
    OTHER
}
