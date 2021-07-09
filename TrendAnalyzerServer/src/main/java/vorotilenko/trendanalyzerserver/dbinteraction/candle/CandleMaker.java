package vorotilenko.trendanalyzerserver.dbinteraction.candle;

import vorotilenko.trendanalyzerserver.dbinteraction.DatabaseSender;
import vorotilenko.trendanalyzerserver.dbinteraction.TradeDataAccumulator;

import java.sql.*;

/**
 * Object for organizing interaction with the DB.
 * Receives data about the price of the symbol and the time of the transaction.
 * Automatically sends information to the DB that forms the minute candlesticks.
 */
public class CandleMaker extends DatabaseSender {
    /**
     * Object for sending data to the DB
     */
    private final TradeDataAccumulator tradeDataAccumulator;

    /**
     * Current trading minute
     */
    private long currMinute = 0;
    /**
     * Candle open time
     */
    private long openTime = System.currentTimeMillis();
    /**
     * Min trade price per trading minute
     */
    private double minPrice = 0;
    /**
     * Max trade price per trading minute
     */
    private double maxPrice = 0;
    /**
     * Candle open price
     */
    private double openPrice = 0;
    /**
     * Current price
     */
    private double currPrice = 0;

    public CandleMaker(String exchangeName) throws SQLException {
        super(exchangeName);
        // Creating TradeDataAccumulator
        tradeDataAccumulator = new TradeDataAccumulator(new CandleBatchSender());
    }

    /**
     * Converts milliseconds to minutes
     */
    private long getMinutes(long milliseconds) {
        return milliseconds / 60000;
    }

    /**
     * Sends data about the past trading minute to the DB.
     * Forms a candlestick
     */
    private void sendDataToDB(String symbol) {
        tradeDataAccumulator.add(new Candle(openTime, minPrice, maxPrice, openPrice, currPrice,
                symbol, exchangeId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putData(String symbol, long tradeTimeMillis, double price) {
        // currMinute is the current candlestick minute.
        // And tradeMinute is the current trade minute.
        final long tradeMinute = getMinutes(tradeTimeMillis);
        if (tradeMinute > currMinute) {
            // If a new minute has been started then we put data to the DB and update fields
            sendDataToDB(symbol);
            currMinute = tradeMinute;
            openTime = tradeTimeMillis;
            minPrice = maxPrice = openPrice = currPrice = price;
        } else {
            // If a new minute hasn't been started then we compare
            // the current field values with the price to form the candlestick
            currPrice = price;
            if (currPrice < minPrice)
                minPrice = currPrice;
            else if (currPrice > maxPrice)
                maxPrice = currPrice;
        }
    }
}
