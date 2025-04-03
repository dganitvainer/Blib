package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles MySQL database connections and provides connection management functionality.
 * This class is responsible for establishing and managing database connections using
 * JDBC driver.
 *
 * @author YourName
 * @version 1.0
 * @since 1.0
 */
public class mysqlConnection {
    
    /**
     * Static connection instance shared across the application.
     */
    private static Connection con;
    
    /**
     * The URL of the MySQL database.
     * Format: jdbc:mysql://hostname:port/database_name
     */
    public String url;
    
    /**
     * Username for database authentication.
     */
    public String userName;
    
    /**
     * Password for database authentication.
     */
    public String password;
    
    /**
     * Constructs a new MySQL connection with the specified parameters.
     * Initializes the connection parameters but does not establish the connection.
     * Use {@link #ConnectToDB()} to establish the actual database connection.
     * 
     * @param url      The JDBC URL of the MySQL database
     *                 (format: jdbc:mysql://hostname:port/database_name)
     * @param userName The username for database authentication
     * @param password The password for database authentication
     * 
     * @see #ConnectToDB()
     */
    public mysqlConnection(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
    }
    
    /**
     * Establishes a connection to the MySQL database using the configured parameters.
     * First loads the JDBC driver, then attempts to establish the database connection.
     * 
     * @return Connection object if connection is successful, null if connection fails
     * 
     * @see java.sql.Connection
     * @see java.sql.DriverManager#getConnection(String, String, String)
     */
    public Connection ConnectToDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Driver definition failed");
        }
        
        try {
            con = DriverManager.getConnection(url, userName, password);
            System.out.println("SQL connection succeed");
            return con;
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return null;
    }
}