package link360.util;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 

public class DatabaseConnection {
 
    //Connection parameters 
    private static final String SERVER   = "localhost";
    private static final String PORT     = "1433";
    private static final String DATABASE = "Link360_DarioJosue";
    private static final String USER     = "basesdedatos";
    private static final String PASSWORD = "BasesRP2025";  
 
  
    private static final String URL =
            "jdbc:sqlserver://" + SERVER + ":" + PORT
            + ";databaseName=" + DATABASE
            + ";encrypt=true;trustServerCertificate=true";
 
    private static DatabaseConnection instance;
    private Connection connection;
 
    private DatabaseConnection() throws ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }
 
 
    public static synchronized DatabaseConnection getInstance() throws ClassNotFoundException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
 
  
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }
 
  
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Error closing connection: " + e.getMessage());
        }
    }
}