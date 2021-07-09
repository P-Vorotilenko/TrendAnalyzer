package vorotilenko.trendanalyzerserver.dbinteraction.everytrade;

import vorotilenko.trendanalyzerserver.dbinteraction.DatabaseSender;
import vorotilenko.trendanalyzerserver.dbinteraction.TradeDataAccumulator;

import java.sql.SQLException;

/**
 * Sends data about each trade to the DB
 */
public class EveryTradeSender extends DatabaseSender {

    /**
     * Object for sending data to the DB
     */
    private final TradeDataAccumulator tradeDataAccumulator;

    public EveryTradeSender(String exchangeName) throws SQLException {
        super(exchangeName);
        tradeDataAccumulator = new TradeDataAccumulator(new EveryTradeBatchSender());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putData(String symbol, long tradeTimeMillis, double price) {
        tradeDataAccumulator.add(new TradeData(symbol, tradeTimeMillis, price, exchangeId));
    }
}
