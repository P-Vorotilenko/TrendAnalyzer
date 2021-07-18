package vorotilenko.trendanalyzerserver.dbinteraction;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Class for sending data to the DB
 */
public abstract class DatabaseSender implements Closeable {

    /**
     * Logger
     */
    protected final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * ID of the exchange which this DatabaseSender object works with
     */
    protected final short exchangeId;

    public DatabaseSender(String exchangeName) throws SQLException {
        short id = DBInteraction.getExchangeID(exchangeName);
        if (id != -1)
            exchangeId = id;
        else {
            throw new RuntimeException(
                    String.format("Exchange %s was not found in the DB", exchangeName));
        }
    }

    public DatabaseSender(short exchangeId) {
        this.exchangeId = exchangeId;
    }

    /**
     * Puts data into the object of interaction with the DB
     */
    public abstract void putData(String symbol, long tradeTimeMillis, double price);
}
