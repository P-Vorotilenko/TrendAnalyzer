package vorotilenko.trendanalyzerserver.dbinteraction;

import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class for interacting with the DB
 */
public class DBInteraction {

    /**
     * Query to delete all data from tradeInfo
     */
    private static final String CLEAR_TRADEINFO_QUERY = "DELETE FROM tradeInfo";
    /**
     * Query to delete all records older than 10 minutes from tradeInfo
     */
    private static final String DELETE_OLD_RECORDS_QUERY =
            "DELETE FROM tradeInfo WHERE (? - TradeTimeMillis) > 600000;";

    /**
     * Executor for cleaning DB
     */
    private static final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();
    /**
     * True if DB cleanup is running. False if DB cleanup is not running
     */
    private static volatile boolean isCleaning = false;

    /**
     * Cleans up the DB
     */
    public static void clearDB() {
        try (Connection connection = DBConnection.getNewConnection();
             PreparedStatement pStatement = connection.prepareStatement(CLEAR_TRADEINFO_QUERY)) {

            pStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs a DB cleanup thread that removes old records.
     * Ensures that only one such thread will run in the program
     */
    public static void startDBCleaner() {
        if (!isCleaning) {
            synchronized (DBInteraction.class) {
                if (!isCleaning) {
                    isCleaning = true;
                    clearDB();
                    executor.scheduleAtFixedRate(() -> {
                        try (Connection connection = DBConnection.getNewConnection();
                             PreparedStatement pStatement =
                                     connection.prepareStatement(DELETE_OLD_RECORDS_QUERY)) {

                            pStatement.setLong(1, System.currentTimeMillis());
                            pStatement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }, 60000, 60000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    /**
     * @param exchangeName Name of the exchange
     * @return ID of the exchange or -1 if the exchange was not found
     */
    public static short getExchangeID(String exchangeName) {
        boolean idFound = false;
        short exchangeId = -1;
        String exchangeIdQuery = "SELECT Id FROM exchanges WHERE Exchange = ?";
        try (Connection dbConnection = DBConnection.getNewConnection();
             PreparedStatement pStatement = dbConnection.prepareStatement(exchangeIdQuery)) {

            pStatement.setString(1, exchangeName);
            ResultSet resultSet = pStatement.executeQuery();
            if (resultSet.next()) {
                exchangeId = resultSet.getShort(1);
                idFound = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
        if (idFound) return exchangeId;
        else return -1;
    }
}
