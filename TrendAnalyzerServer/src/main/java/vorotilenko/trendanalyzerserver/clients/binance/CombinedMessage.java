package vorotilenko.trendanalyzerserver.clients.binance;

/**
 * Message that comes from Binance when the client is subscribed on
 * updates by multiple symbols
 */
public class CombinedMessage {
    /**
     * Stream name
     */
    public String stream;
    /**
     * Data
     */
    public BinanceTradeInfo data;
}
