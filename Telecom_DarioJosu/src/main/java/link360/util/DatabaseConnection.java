package link360.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Maneja la conexion a la base de datos. Las credenciales NO van en el codigo:
 * se leen de db.properties (en la raiz del proyecto, junto al pom.xml). Ese
 * archivo no debe subirse al repositorio; se incluye db.properties.example como
 * plantilla.
 */
public class DatabaseConnection {

    private static final String CONFIG_FILE = "db.properties";

    private final String url;
    private final String user;
    private final String password;

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws ClassNotFoundException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        Properties props = loadProperties();
        String server = props.getProperty("db.server", "localhost");
        String port = props.getProperty("db.port", "1433");
        String database = props.getProperty("db.name", "Link360_DarioJosue");

        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password");
        this.url = "jdbc:sqlserver://" + server + ":" + port
                + ";databaseName=" + database
                + ";encrypt=true;trustServerCertificate=true";

        if (this.user == null || this.password == null) {
            throw new IllegalStateException(
                    "Faltan credenciales en " + CONFIG_FILE + ". "
                    + "Copie db.properties.example a db.properties y complete db.user / db.password.");
        }
    }

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(CONFIG_FILE)) {
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se encontro " + CONFIG_FILE + " en la raiz del proyecto. "
                    + "Copie db.properties.example, renombrelo a db.properties y complete sus datos.", e);
        }
        return props;
    }

    public static synchronized DatabaseConnection getInstance() throws ClassNotFoundException {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(5)) {
            connection = DriverManager.getConnection(url, user, password);
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
