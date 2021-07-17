package vorotilenko.trendanalyzerserver.dbinteraction.everytrade;

import vorotilenko.trendanalyzerserver.dbinteraction.BatchSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Forms a BATCH-query from the passed data array and sends this query to the DB
 */
public class EveryTradeBatchSender implements BatchSender {

    /**
     * Query for entering data into the DB
     */
    private static final String INSERT_QUERY =
            "INSERT INTO tradeInfo(SymbolId, TradeTimeMillis, Price, ExchangeId) VALUES\n" +
                    "(\n" +
                    "   (SELECT Id FROM symbols WHERE Symbol = ?),\n" +
                    "   ?,\n" +
                    "   ?,\n" +
                    "   ?\n" +
                    ")";

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendBatch(Object[] data, Connection dbConnection) {
        if (data != null) {
            try (PreparedStatement pStatement = dbConnection.prepareStatement(INSERT_QUERY)) {
                for (Object entry : data) {
                    TradeData tradeData = (TradeData) entry;
                    pStatement.setString(1, tradeData.getSymbol());
                    pStatement.setLong(2, tradeData.getTradeTimeMillis());
                    pStatement.setDouble(3, tradeData.getPrice());
                    pStatement.setShort(4, tradeData.getExchangeId());
                    pStatement.addBatch();
                }
                pStatement.executeBatch();
            } catch (SQLException e) {
                logger.info("A problem while adding record to the DB occurred.");
                e.printStackTrace();
            }
        }
    }
}
