package vorotilenko.trendanalyzerserver.clients.binance;

/**
 * Trading info aggregated for 1 order (for parsing Binance messages)
 * */
public class AggregateTradeInfo {
    /**
     * Symbol
     * */
    public String s;
    /**
     * Price
     * */
    public double p;
    /**
     * Quantity
     * */
    public double q;
    /**
     * Trade time
     * */
    public long T;
}
