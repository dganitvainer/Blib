package enums;

/**
 * Represents the available system commands in the library management system.
 * This enum defines all possible commands that can be executed between
 * client and server components, handling operations like user authentication,
 * book management, and system queries.
 */
public enum Commands {
    /** Validates user credentials during authentication */
    CheckUsername,
    
    /** Updates user profile information */
    UpdateUsername,
    
    /** Processes user login requests */
    Login,
    
    /** Handles book search operations */
    SearchBook,
    
    /** Processes user logout requests */
    Logout,
    
    /** Displays the main menu of the application */
    MainManu,
    
    /** Establishes connection with a client */
    ConnectClient,
    
    /** Handles client disconnection */
    ClientDisconnect,
    
    /** Updates member information in the system */
    UpdateMember,
    
    /** Retrieves a list of all library members */
    GetAllMembers,
    
    /** Retrieves book information by its ID */
    GetBookById,
    
    /** Retrieves a list of all books in the library */
    GetAllBooks,
    
    /** Searches for books by name */
    GetBookByName,
    
    /** Searches for books by theme/category */
    GetBookByTheme,
    
    /** Searches for books by description content */
    GetBookByDescription,
    
    /** Creates a new member account */
    CreateMember,
    
    /** Processes book return operations */
    ReturnBook,
    
    /** Processes book borrowing operations */
    BorrowBook,
    
    /** Extends the loan period for a borrowed book */
    ExtendBookLoan,
    
    /** Retrieves data for chart generation */
    GetChartData,
    
    /** Retrieves user notifications */
    GetNotifications,
    
    /** Retrieves system activity logs */
    GetActivityLogs,
    
    /** Retrieves filtered activity logs based on criteria */
    GetFilteredActivityLogs,
    
    /** Searches for books by author */
    GetBookByAuthor,
    
    /** Retrieves borrowing history for a member */
    GetBorrowHistory,
    
    /** Retrieves book name using its ID */
    GetBookNameById,
    
    /** Retrieves chart data for loan duration analysis */
    GetLoanDurationChart,
    
    /** Retrieves chart data for late return analysis */
    GetLateReturnChart,
    
    /** Retrieves the current status of a member */
    GetMemberStatus,
    
    /** Retrieves a simplified version of book details by ID */
    getSimpleBookById,
    
    /** Handles ordering of books */
    OrderBook,
    
    /** Deletes user notifications */
    DeleteNotifications,
    
    /** Retrieves activity logs for a specific member */
    GetActivityLogsByMember;
}