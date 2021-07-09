package vorotilenko.trendanalyzerserver.clients.huobi;

import com.huobi.client.MarketClient;
import com.huobi.client.req.market.SubMarketTradeRequest;
import com.huobi.constant.HuobiOptions;
import vorotilenko.trendanalyzerserver.ExchangeNames;
import vorotilenko.trendanalyzerserver.Symbols;
import vorotilenko.trendanalyzerserver.clients.ExchangeClient;
import vorotilenko.trendanalyzerserver.clients.TradeInfo;
import vorotilenko.trendanalyzerserver.dbinteraction.DatabaseSender;
import vorotilenko.trendanalyzerserver.dbinteraction.everytrade.EveryTradeSender;

import java.sql.SQLException;
import java.util.logging.Logger;

public class HuobiClient extends ExchangeClient {

    /**
     * Instance for Standalone pattern
     */
    private static final HuobiClient instance = new HuobiClient();;
    static {
        try {
            instance.start(new EveryTradeSender(ExchangeNames.HUOBI));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Class implements Standalone pattern
     */
    private HuobiClient(){}

    /**
     * Returns the only instance of the class
     */
    public static HuobiClient getInstance() {
        return instance;
    }

    /**
     * Runs the client
     */
    private void start(DatabaseSender sender) {
        MarketClient marketClient = MarketClient.create(new HuobiOptions());
        final String symbol = Symbols.BTCUSDT;
        marketClient.subMarketTrade(
                SubMarketTradeRequest.builder().symbol(symbol.toLowerCase()).build(),
                (marketTradeEvent) -> {

            marketTradeEvent.getList().forEach(marketTrade -> {
                long timestamp = marketTrade.getTs();
                double price = marketTrade.getPrice().doubleValue();
                // Sending data to the DB
                sender.putData(symbol, timestamp, price);
                // Sending data to listeners
                TradeInfo tradeInfo = new TradeInfo(symbol, timestamp, price, ExchangeNames.HUOBI);
                notifyTradeInfoListeners(tradeInfo);
                //logger.info("Huobi:\n" + marketTrade);
            });
        });
    }

    @Override
    protected String getExchangeName() {
        return ExchangeNames.HUOBI;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
    }
}
