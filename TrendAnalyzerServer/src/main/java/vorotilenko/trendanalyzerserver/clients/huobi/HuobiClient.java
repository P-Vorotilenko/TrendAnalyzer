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
     * DatabaseSender have to be stored here for stopping it before exiting the program
     */
    private DatabaseSender databaseSender;

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
     */
    private void subscToUpdates(String symbol) {
        MarketClient.create(new HuobiOptions()).subMarketTrade(
                SubMarketTradeRequest.builder().symbol(symbol.toLowerCase()).build(),
                new HuobiTradeCallback(symbol, databaseSender, this)
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
        try {
            databaseSender = new EveryTradeSender(huobiId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while connecting to DB");
        }
        subscToUpdates(Currencies.BTC + Currencies.USDT);
        subscToUpdates(Currencies.ETH + Currencies.USDT);
        subscToUpdates(Currencies.ETH + Currencies.BTC);
        subscToUpdates(Currencies.ADA + Currencies.USDT);
        subscToUpdates(Currencies.ADA + Currencies.BTC);
        subscToUpdates(Currencies.ADA + Currencies.ETH);
        subscToUpdates(Currencies.XRP + Currencies.USDT);
        subscToUpdates(Currencies.XRP + Currencies.BTC);
        subscToUpdates(Currencies.DOT + Currencies.USDT);
        subscToUpdates(Currencies.DOT + Currencies.BTC);
        subscToUpdates(Currencies.UNI + Currencies.USDT);
        subscToUpdates(Currencies.UNI + Currencies.BTC);
        subscToUpdates(Currencies.UNI + Currencies.ETH);
        subscToUpdates(Currencies.BCH + Currencies.USDT);
        subscToUpdates(Currencies.BCH + Currencies.BTC);
        subscToUpdates(Currencies.LTC + Currencies.USDT);
        subscToUpdates(Currencies.LTC + Currencies.BTC);
        subscToUpdates(Currencies.SOL + Currencies.USDT);
        subscToUpdates(Currencies.SOL + Currencies.BTC);
        subscToUpdates(Currencies.SOL + Currencies.ETH);
        subscToUpdates(Currencies.LINK + Currencies.USDT);
        subscToUpdates(Currencies.LINK + Currencies.BTC);
        subscToUpdates(Currencies.LINK + Currencies.ETH);
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
        try {
            databaseSender.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.stop();
    }
}
