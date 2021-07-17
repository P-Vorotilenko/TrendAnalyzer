package vorotilenko.trendanalyzerserver.dbinteraction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Class for sending data to the DB
 */
public abstract class DatabaseSender {

    /**
     * Logger
     */
    protected final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * ID of the exchange which this DatabaseSender object works with
     */
    protected final short exchangeId;

    public DatabaseSender(String exchangeName) throws SQLException {
        // Getting the exchange ID
        Connection dbConnection = DBConnection.getNewConnection();
        String exchangeIdQuery = "SELECT Id FROM exchanges WHERE Exchange = ?";
        PreparedStatement pStatement = dbConnection.prepareStatement(exchangeIdQuery);
        pStatement.setString(1, exchangeName);
        ResultSet resultSet = pStatement.executeQuery();
        if (resultSet.next()) {
            exchangeId = resultSet.getShort(1);
            pStatement.close();
            dbConnection.close();
        } else {
            pStatement.close();
            dbConnection.close();
            throw new RuntimeException(
                    String.format("The exchange with name \"%s\" was not found.", exchangeName));
        }
    }

    /**
     * Puts data into the object of interaction with the DB
     */
    public abstract void putData(String symbol, long tradeTimeMillis, double price);
}
