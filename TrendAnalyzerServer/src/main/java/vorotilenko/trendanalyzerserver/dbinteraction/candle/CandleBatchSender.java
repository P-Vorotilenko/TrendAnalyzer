package vorotilenko.trendanalyzerserver.dbinteraction.candle;

import com.sun.istack.internal.Nullable;
import vorotilenko.trendanalyzerserver.dbinteraction.BatchSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Forms a BATCH request from the transmitted data array and
 * sends this request to the DB
 */
public class CandleBatchSender implements BatchSender {

    /**
     * Request for entering data into the DB
     */
    private static final String INSERT_QUERY =
            "INSERT INTO candleTradeInfo (OpenTime, OpenPrice, ClosePrice, MinPrice, \n" +
                    "\t\t\t\t\t   MaxPrice, SymbolId, ExchangeId) VALUES\n" +
                    "(?, \n" +
                    " ?, \n" +
                    " ?, \n" +
                    " ?, \n" +
                    " ?,\n" +
                    " (SELECT Id FROM symbols WHERE Symbol = ?), \n" +
                    " ?\n" +
                    ")";

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * If candles not null, forms a BATCH-query from this data and sends it to the DB
     *
     * @param data A set of candles that will be sent to the database with a single BATCH request
     */
    public void sendBatch(@Nullable Object[] data, Connection dbConnection) {
        if (data != null) {
            try (PreparedStatement pStatement = dbConnection.prepareStatement(INSERT_QUERY)) {
                for (Object entry: data) {
                    Candle candle = (Candle) entry;
                    pStatement.setLong(1, candle.getOpenTime());
                    pStatement.setDouble(2, candle.getOpenPrice());
                    pStatement.setDouble(3, candle.getClosePrice());
                    pStatement.setDouble(4, candle.getMinPrice());
                    pStatement.setDouble(5, candle.getMaxPrice());
                    pStatement.setString(6, candle.getSymbol());
                    pStatement.setShort(7, candle.getExchangeId());
                    pStatement.addBatch();
                }
                pStatement.executeBatch();
            } catch (SQLException e) {
                logger.info("A problem while adding data about " +
                        "the candlestick to the DB occurred.");
                e.printStackTrace();
            }
        }
    }
}
