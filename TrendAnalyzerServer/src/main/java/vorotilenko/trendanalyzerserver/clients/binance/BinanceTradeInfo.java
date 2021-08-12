package vorotilenko.trendanalyzerserver.clients.binance;

/**
 * Info about price of some symbol at some moment of time on Binance
 * (for parsing Binance JSON messages)
 * */
public class BinanceTradeInfo {
    /**
     * Symbol
     * */
    public String s;
    /**
     * Time
     * */
    public long E;
    /**
     * Price
     * */
    public double c;
}
