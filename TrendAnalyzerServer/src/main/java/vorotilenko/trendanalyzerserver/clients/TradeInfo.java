package vorotilenko.trendanalyzerserver.clients;

/**
 * Stores data about trades
 */
public class TradeInfo {
    /**
     * Symbol
     */
    public String symbol;
    /**
     * Trade time in milliseconds
     */
    public long tradeTimeMillis;
    /**
     * Price of the trade
     */
    public double price;
    /**
     * Exchange name
     */
    public String exchange;

    /**
     * @param symbol Symbol
     * @param tradeTimeMillis Trade time in milliseconds
     * @param price Price of the trade
     * @param exchange Exchange name
     * */
    public TradeInfo(String symbol, long tradeTimeMillis, double price, String exchange) {
        this.symbol = symbol;
        this.tradeTimeMillis = tradeTimeMillis;
        this.price = price;
        this.exchange = exchange;
    }
}
