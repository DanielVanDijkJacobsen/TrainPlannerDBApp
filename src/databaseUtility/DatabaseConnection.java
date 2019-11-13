package databaseUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String SQCONN = "jdbc:sqlite:database.sqlite";
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Connection established");
            return DriverManager.getConnection(SQCONN);
        }
        catch (ClassNotFoundException cnfex) {
            cnfex.printStackTrace();
            System.out.println("Unable to load the class. Terminating the program");
            System.exit(-1);
        }
        return null;
    }
}
