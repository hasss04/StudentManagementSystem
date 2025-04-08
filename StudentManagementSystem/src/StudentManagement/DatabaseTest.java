package StudentManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTest {

    // JDBC URL, username, and password for MySQL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/StudentManagement?serverTimezone=UTC";  // Added serverTimezone for compatibility
    private static final String DB_USER = "root";  // Your MySQL username
    private static final String DB_PASSWORD = "admin@123";  // No password (if it's empty for root)

    public static void main(String[] args) {
        // Load and register MySQL JDBC driver (This is required for older JDK versions, but not for JDK 9+)
        try {
            // This line is optional if you use JDK 9 or higher with auto-loading of JDBC drivers
            Class.forName("com.mysql.cj.jdbc.Driver"); 

            // Establish a connection to the database
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            
            // If connection is successful
            System.out.println("Connection successful!");

            // Perform any operations (optional)

            // Close the connection
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            // Handle any errors (e.g., connection issues, incorrect credentials)
            e.printStackTrace();
            System.out.println("Connection failed!");
        }
    }
}
