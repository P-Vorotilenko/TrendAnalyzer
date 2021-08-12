package vorotilenko.trendanalyzerserver.clients.huobi;

import com.huobi.model.market.MarketTradeEvent;
import com.huobi.utils.ResponseCallback;
import vorotilenko.trendanalyzerserver.ExchangeNames;
import vorotilenko.trendanalyzerserver.clients.TradeInfo;
import vorotilenko.trendanalyzerserver.dbinteraction.DatabaseSender;

/**
 * Callback for Huobi trades. Called when new info has come
 */
public class HuobiTradeCallback implements ResponseCallback<MarketTradeEvent> {

    private final String symbol;
    private final DatabaseSender sender;
    private final HuobiClient client;

    /**
     * The timestamp (ms) of the last trade which was sent to DB and listeners
     */
    private long lastTradeTime = 0;

    public HuobiTradeCallback(String symbol, DatabaseSender sender, HuobiClient client) {
        this.symbol = symbol;
        this.sender = sender;
        this.client = client;
    }

    @Override
    public void onResponse(MarketTradeEvent marketTradeEvent) {
        marketTradeEvent.getList().forEach(marketTrade -> {
            long timestamp = marketTrade.getTs();
            // Sending data not more often than once a second for each symbol
            if ((timestamp - lastTradeTime) >= 1000) {
                double price = marketTrade.getPrice().doubleValue();
                // Sending data to the DB
                sender.putData(symbol, timestamp, price);
                // Sending data to listeners
                TradeInfo tradeInfo =
                        new TradeInfo(symbol, timestamp, price, ExchangeNames.HUOBI);
                client.notifyTradeInfoListeners(tradeInfo);

                lastTradeTime = timestamp;
            }
        });
    }
}
