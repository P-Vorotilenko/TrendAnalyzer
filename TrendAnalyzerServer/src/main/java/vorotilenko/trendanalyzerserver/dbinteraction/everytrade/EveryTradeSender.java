package vorotilenko.trendanalyzerserver.dbinteraction.everytrade;

import vorotilenko.trendanalyzerserver.dbinteraction.DatabaseSender;
import vorotilenko.trendanalyzerserver.dbinteraction.TradesAccumulator;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Sends data about each trade to the DB
 */
public class EveryTradeSender extends DatabaseSender implements Closeable {

    /**
     * Object for sending data to the DB
     */
    private final TradesAccumulator tradesAccumulator;

    public EveryTradeSender(String exchangeName) throws SQLException {
        super(exchangeName);
        tradesAccumulator = new TradesAccumulator(new EveryTradeBatchSender());
    }

    /**
     * @param tradesToAccumulate After accumulating this amount of trades the data
     *                           will be sent to the DB
     */
    public EveryTradeSender(String exchangeName,
                            int tradesToAccumulate) throws SQLException {
        super(exchangeName);
        tradesAccumulator = new TradesAccumulator(new EveryTradeBatchSender(),
                tradesToAccumulate);
    }

    public EveryTradeSender(short exchangeId) throws SQLException {
        super(exchangeId);
        tradesAccumulator = new TradesAccumulator(new EveryTradeBatchSender());
    }

    /**
     * @param tradesToAccumulate After accumulating this amount of trades the data
     *                           will be sent to the DB
     */
    public EveryTradeSender(short exchangeId,
                            int tradesToAccumulate) throws SQLException {
        super(exchangeId);
        tradesAccumulator = new TradesAccumulator(new EveryTradeBatchSender(),
                tradesToAccumulate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putData(String symbol, long tradeTimeMillis, double price) {
        tradesAccumulator.add(new TradeData(symbol, tradeTimeMillis, price, exchangeId));
    }

    @Override
    public void close() throws IOException {
        tradesAccumulator.close();
    }
}
