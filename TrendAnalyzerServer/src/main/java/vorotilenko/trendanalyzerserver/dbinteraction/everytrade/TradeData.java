package vorotilenko.trendanalyzerserver.dbinteraction.everytrade;

/**
 * Storing trade data for separate trades
 */
public class TradeData {
    /**
     * Symbol
     */
    private String symbol;
    /**
     * Trade time in milliseconds
     */
    private long tradeTimeMillis;
    /**
     * Trade price
     */
    private double price;
    /**
     * Exchange ID
     */
    private short exchangeId;

    /**
     * @param symbol Symbol
     * @param tradeTimeMillis Trade time in milliseconds
     * @param price Trade price
     * @param exchangeId Exchange ID
     * */
    public TradeData(String symbol, long tradeTimeMillis, double price, short exchangeId) {
        setSymbol(symbol);
        setTradeTimeMillis(tradeTimeMillis);
        setPrice(price);
        setExchangeId(exchangeId);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getTradeTimeMillis() {
        return tradeTimeMillis;
    }

    public void setTradeTimeMillis(long tradeTimeMillis) {
        this.tradeTimeMillis = tradeTimeMillis;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public short getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(short exchangeId) {
        this.exchangeId = exchangeId;
    }
}
