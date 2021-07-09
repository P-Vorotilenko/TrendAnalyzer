package vorotilenko.trendanalyzerserver.dbinteraction;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Class for connecting to the DB
 */
public class DBConnection {

    private static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/trendAnalyzer";
    private static final String USER = "postgres";
    private static final String PASS = "HRxtyQDoWYVCgXbwdq0L";

    private static final Logger logger = Logger.getLogger(DBConnection.class.getName());

    static {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("PostgreSQL driver has been connected.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private DBConnection() {}

    /**
     * Opens connection to the DB
     */
    @NotNull
    public static Connection getNewConnection() throws SQLException {

        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASS);
            if (connection != null)
                return connection;
            else {
                throw new NullPointerException(
                        "An error while connecting to TrendAnalyzer DB occurred.");
            }
        } catch (SQLException | NullPointerException e) {
            logger.info("Failed to connect to TrendAnalyzer DB.");
            e.printStackTrace();
            throw e;
        }
    }
}
