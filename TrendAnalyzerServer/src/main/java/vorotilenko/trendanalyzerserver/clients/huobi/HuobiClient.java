package vorotilenko.trendanalyzerserver.clients.huobi;

import com.huobi.client.MarketClient;
import com.huobi.client.req.market.SubMarketTradeRequest;
import com.huobi.constant.HuobiOptions;
import vorotilenko.trendanalyzerserver.ExchangeNames;
import vorotilenko.trendanalyzerserver.Symbols;
import vorotilenko.trendanalyzerserver.clients.ExchangeClient;
import vorotilenko.trendanalyzerserver.clients.TradeInfo;
import vorotilenko.trendanalyzerserver.dbinteraction.DBInteraction;
import vorotilenko.trendanalyzerserver.dbinteraction.DatabaseSender;
import vorotilenko.trendanalyzerserver.dbinteraction.everytrade.EveryTradeSender;

import java.sql.SQLException;
import java.util.logging.Logger;

public class HuobiClient extends ExchangeClient {

    /**
     * Instance for Standalone pattern
     */
    private static final HuobiClient instance = new HuobiClient();
    static {
        instance.start();
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
     * Subscribes to updates by the specified symbol
     * @param symbol Symbol which has to be observed
     */
    private void subscToUpdates(String symbol, short huobiId) {
        MarketClient marketClient = MarketClient.create(new HuobiOptions());
        final DatabaseSender sender;
        try {
            sender = new EveryTradeSender(huobiId, 20);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        marketClient.subMarketTrade(
                SubMarketTradeRequest.builder().symbol(symbol.toLowerCase()).build(),
                (marketTradeEvent) -> {

                    marketTradeEvent.getList().forEach(marketTrade -> {
                        long timestamp = marketTrade.getTs();
                        double price = marketTrade.getPrice().doubleValue();
                        // Sending data to the DB
                        sender.putData(symbol, timestamp, price);
                        // Sending data to listeners
                        TradeInfo tradeInfo =
                                new TradeInfo(symbol, timestamp, price, ExchangeNames.HUOBI);
                        notifyTradeInfoListeners(tradeInfo);
                        //logger.info("Huobi:\n" + marketTrade);
                    });
                });
    }

    /**
     * Runs the client
     */
    private void start() {
        short huobiId = DBInteraction.getExchangeID(ExchangeNames.HUOBI);
        if (huobiId == -1) {
            throw new RuntimeException(String.format(
                    "Exchange %s was not found in the DB", ExchangeNames.HUOBI));
        }
        subscToUpdates(Symbols.BTCUSDT, huobiId);
        subscToUpdates(Symbols.ETHUSDT, huobiId);
        subscToUpdates(Symbols.ADAUSDT, huobiId);
        subscToUpdates(Symbols.XRPUSDT, huobiId);
        subscToUpdates(Symbols.DOTUSDT, huobiId);
        subscToUpdates(Symbols.UNIUSDT, huobiId);
        subscToUpdates(Symbols.BCHUSDT, huobiId);
        subscToUpdates(Symbols.LTCUSDT, huobiId);
        subscToUpdates(Symbols.SOLUSDT, huobiId);
        subscToUpdates(Symbols.LINKUSDT, huobiId);
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
