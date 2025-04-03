package enums;
/**
 * Represents the types of activities that can occur in the library system.
 * This enum defines all possible activities that can be logged or tracked
 * within the library management system.
 * 
 */
public enum ActivityType {
	/** Represents a book loan activity */
    LOAN, 
    
    /** Represents a book reservation activity */
    RESERVATION, 
    
    /** Represents a notification being sent or received */
    NOTIFICATION, 
    
    /** Represents miscellaneous activities not covered by other types */
    OTHER, 
    
    /** Represents a book return activity */
    RETURN, 
    
    /** Represents a loan extension or renewal activity */
    EXTENSION
}