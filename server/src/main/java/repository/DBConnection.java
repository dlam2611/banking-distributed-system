package repository;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static String host;
    private static int port;
    private static String dbName;
    private static String user;
    private static String password;

    static {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (Exception e) {
            System.err.println("Could not load db.properties, using fallback defaults: " + e.getMessage());
        }

        host = props.getProperty("db.host", "localhost");
        port = Integer.parseInt(props.getProperty("db.port", "5433"));
        dbName = props.getProperty("db.name", "bankingDB");
        user = props.getProperty("db.username", "postgres");
        password = props.getProperty("db.password", "Lam2006@123");
    }

    public static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
        return DriverManager.getConnection(url, user, password);
    }
}
