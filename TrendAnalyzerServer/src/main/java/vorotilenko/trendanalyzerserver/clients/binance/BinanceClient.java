package vorotilenko.trendanalyzerserver.clients.binance;

import com.google.gson.Gson;
import org.glassfish.tyrus.client.ClientManager;
import vorotilenko.trendanalyzerserver.Currencies;
import vorotilenko.trendanalyzerserver.ExchangeNames;
import vorotilenko.trendanalyzerserver.clients.ExchangeClient;
import vorotilenko.trendanalyzerserver.clients.TradeInfo;
import vorotilenko.trendanalyzerserver.dbinteraction.DatabaseSender;
import vorotilenko.trendanalyzerserver.dbinteraction.everytrade.EveryTradeSender;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.logging.Logger;

@ClientEndpoint
public class BinanceClient extends ExchangeClient {

    /**
     * Instance for Standalone pattern
     * */
    private static final BinanceClient instance = new BinanceClient();
    /**
     * Object for adding trade data to DB
     */
    private static DatabaseSender databaseSender;
    static {
        try {
            databaseSender = new EveryTradeSender(ExchangeNames.BINANCE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(BinanceClient.class.getName());
    /**
     * Gson object for reading data from incoming message
     */
    private static final Gson gson = new Gson();
    /**
     * Flag telling if the client is running
     */
    private static volatile boolean started = false;

    /**
     * Class implements Standalone pattern
     */
    private BinanceClient() {}

    /**
     * Returns the only instance of the class
     */
    public static BinanceClient getInstance() {
        if (!started) {
            synchronized (BinanceClient.class) {
                if (!started) {
                    start();
                    started = true;
                }
            }
        }
        return instance;
    }

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Connection to Binance established. Session: " + session.getId());
    }

    /**
     * Handles the message from server
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // Parsing JSON
        BinanceTradeInfo tradeInfo = gson.fromJson(message, CombinedMessage.class).data;
        String symbol = tradeInfo.s;
        long time = tradeInfo.E;
        double price = tradeInfo.c;
        // Sending data to the DB
        databaseSender.putData(symbol, time, price);
        // Sending data to listeners
        notifyTradeInfoListeners(new TradeInfo(symbol, time, price, ExchangeNames.BINANCE));
    }

    /**
     * Handles the ping-message from server
     */
    @OnMessage
    public void onMessage(PongMessage msg, Session session) {
        try {
            session.getBasicRemote().sendPong(msg.getApplicationData());
        } catch (IOException e) {
            logger.info("A problem while sending PONG-message occurred:\n");
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("Error:\n");
        throwable.printStackTrace();
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info(String.format("Session %s closed because: %s",
                session.getId(), closeReason));
        // Stream closes automatically every 24 hours,
        // so work has to be restarted
        //start();
    }

    /**
     * Appends symbol of currency1 and currency2 to url
     */
    private static void appendSymbol(StringBuilder url, String currency1, String currency2) {
        url.append(currency1.toLowerCase(Locale.ROOT))
                .append(currency2.toLowerCase(Locale.ROOT))
                .append("@miniTicker/");
    }

    /**
     * Creates url for connection to Binance
     */
    private static String createUrl() {
        StringBuilder url = new StringBuilder("wss://stream.binance.com:9443/stream?streams=");
        appendSymbol(url, Currencies.BTC, Currencies.USDT);
        appendSymbol(url, Currencies.ETH, Currencies.BTC);
        appendSymbol(url, Currencies.ETH, Currencies.USDT);
        appendSymbol(url, Currencies.ADA, Currencies.BTC);
        appendSymbol(url, Currencies.ADA, Currencies.ETH);
        appendSymbol(url, Currencies.ADA, Currencies.USDT);
        appendSymbol(url, Currencies.XRP, Currencies.BTC);
        appendSymbol(url, Currencies.XRP, Currencies.ETH);
        appendSymbol(url, Currencies.XRP, Currencies.USDT);
        appendSymbol(url, Currencies.DOT, Currencies.BTC);
        appendSymbol(url, Currencies.DOT, Currencies.USDT);
        appendSymbol(url, Currencies.UNI, Currencies.BTC);
        appendSymbol(url, Currencies.UNI, Currencies.USDT);
        appendSymbol(url, Currencies.BCH, Currencies.BTC);
        appendSymbol(url, Currencies.BCH, Currencies.USDT);
        appendSymbol(url, Currencies.LTC, Currencies.BTC);
        appendSymbol(url, Currencies.LTC, Currencies.ETH);
        appendSymbol(url, Currencies.LTC, Currencies.USDT);
        appendSymbol(url, Currencies.SOL, Currencies.BTC);
        appendSymbol(url, Currencies.SOL, Currencies.USDT);
        appendSymbol(url, Currencies.LINK, Currencies.BTC);
        appendSymbol(url, Currencies.LINK, Currencies.ETH);
        appendSymbol(url, Currencies.LINK, Currencies.USDT);
        url.deleteCharAt(url.length() - 1);
        return url.toString();
    }

    /**
     * Runs the client
     */
    protected static void start() throws NullPointerException {
        try {
            ClientManager client = ClientManager.createClient();
            client.connectToServer(instance, new URI(createUrl()));
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getExchangeName() {
        return ExchangeNames.BINANCE;
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
