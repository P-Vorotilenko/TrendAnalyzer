package vorotilenko.trendanalyzerserver.dbinteraction.candle;

/**
 * Trading candle
 */
public class Candle {
    /**
     * Candle open time
     */
    private long openTime = System.currentTimeMillis();
    /**
     * Min trade price for trading minute
     */
    private double minPrice = 0;
    /**
     * Max trade price for trading minute
     */
    private double maxPrice = 0;
    /**
     * Candle open price
     */
    private double openPrice = 0;
    /**
     * Candle close price
     */
    private double closePrice = 0;
    /**
     * Symbol
     */
    private String symbol = "BTCUSDT";
    /**
     * Exchange ID
     */
    private short exchangeId;

    /**
     * @param openTime   Candle open time
     * @param minPrice   Min trade price for trading minute
     * @param maxPrice   Max trade price for trading minute
     * @param openPrice  Candle open price
     * @param closePrice Candle close price
     * @param symbol     Symbol
     * @param exchangeId Exchange ID
     */
    public Candle(long openTime, double minPrice, double maxPrice,
                  double openPrice, double closePrice, String symbol, short exchangeId) {
        setOpenTime(openTime);
        setMinPrice(minPrice);
        setMaxPrice(maxPrice);
        setOpenPrice(openPrice);
        setClosePrice(closePrice);
        setSymbol(symbol);
        setExchangeId(exchangeId);
    }

    public long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(double openPrice) {
        this.openPrice = openPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(double closePrice) {
        this.closePrice = closePrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public short getExchangeId() {
        return exchangeId;
    }

    public void setExchangeId(short exchangeId) {
        this.exchangeId = exchangeId;
    }
}
