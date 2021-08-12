package vorotilenko.trendanalyzerserver.clients.huobi;

import com.huobi.client.MarketClient;
import com.huobi.client.req.market.SubMarketTradeRequest;
import com.huobi.constant.HuobiOptions;
import vorotilenko.trendanalyzerserver.ExchangeNames;
import vorotilenko.trendanalyzerserver.Currencies;
import vorotilenko.trendanalyzerserver.clients.ExchangeClient;
import vorotilenko.trendanalyzerserver.dbinteraction.DBInteraction;
import vorotilenko.trendanalyzerserver.dbinteraction.DatabaseSender;
import vorotilenko.trendanalyzerserver.dbinteraction.everytrade.EveryTradeSender;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HuobiClient extends ExchangeClient {

    /**
     * Instance for Standalone pattern
     */
    private static final HuobiClient instance = new HuobiClient();
    static {
        instance.start();
    }

    /**
     * DatabaseSenders have to be stored here for stopping them before exiting the program
     */
    private final List<DatabaseSender> databaseSenders = new ArrayList<>();

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
     * Subscribes to updates by the passed symbol
     * @param symbol Symbol which has to be observed
     * @param huobiId Huobi ID in the Database
     */
    private void subscToUpdates(String symbol, short huobiId) {
        MarketClient marketClient = MarketClient.create(new HuobiOptions());
        final DatabaseSender sender;
        try {
            sender = new EveryTradeSender(huobiId, 1);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        databaseSenders.add(sender);
        marketClient.subMarketTrade(
                SubMarketTradeRequest.builder().symbol(symbol.toLowerCase()).build(),
                new HuobiTradeCallback(symbol, sender, this)
        );
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
        for (String firstCurrency : Currencies.getArray()) {
            for (String secondCurrency : Currencies.getArray()) {
                if (!firstCurrency.equals(secondCurrency))
                    subscToUpdates(firstCurrency + secondCurrency, huobiId);
            }
        }
    }

    @Override
    protected String getExchangeName() {
        return ExchangeNames.HUOBI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        databaseSenders.forEach(databaseSender -> {
            try {
                databaseSender.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        super.stop();
    }
}
