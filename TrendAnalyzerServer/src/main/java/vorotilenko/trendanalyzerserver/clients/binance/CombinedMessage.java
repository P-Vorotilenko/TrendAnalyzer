package vorotilenko.trendanalyzerserver.clients.binance;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
    public AggregateTradeInfo data;
}
