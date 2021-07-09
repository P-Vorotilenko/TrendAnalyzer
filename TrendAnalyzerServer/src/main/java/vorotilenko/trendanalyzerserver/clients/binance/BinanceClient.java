package vorotilenko.trendanalyzerserver.clients.binance;

import com.google.gson.Gson;
import org.glassfish.tyrus.client.ClientManager;
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
        AggregateTradeInfo aggTradeInfo = gson.fromJson(message, AggregateTradeInfo.class);
        // Sending data to the DB
        databaseSender.putData(aggTradeInfo.s, aggTradeInfo.T, aggTradeInfo.p);
        // Sending data to the listeners
        notifyTradeInfoListeners(new TradeInfo(aggTradeInfo.s, aggTradeInfo.T,
                aggTradeInfo.p, ExchangeNames.BINANCE));
//        logger.info("Binance:\n" + message +
//                "\nCurrent BTC price: " + aggTradeInfo.p + "$");
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
        // so work has to be started again after stop
        //start();
    }

    /**
     * Runs the client
     */
    protected static void start() throws NullPointerException {
        try {
            ClientManager client = ClientManager.createClient();
            client.connectToServer(instance,
                    new URI("wss://stream.binance.com:9443/ws/btcusdt@aggTrade"));
        } catch (DeploymentException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getExchangeName() {
        return ExchangeNames.BINANCE;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
    }
}
