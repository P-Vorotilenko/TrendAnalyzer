package vorotilenko.trendanalyzerserver.server;

import com.google.gson.Gson;
import org.glassfish.tyrus.server.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vorotilenko.trendanalyzerserver.ExchangeNames;
import vorotilenko.trendanalyzerserver.clients.ExchangeClient;
import vorotilenko.trendanalyzerserver.clients.TradeInfo;
import vorotilenko.trendanalyzerserver.clients.TradeInfoListener;
import vorotilenko.trendanalyzerserver.clients.binance.BinanceClient;
import vorotilenko.trendanalyzerserver.clients.huobi.HuobiClient;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@ServerEndpoint(value = "/taserver")
public class WSServerEndpoint implements TradeInfoListener {

    //TODO: Organize WSS

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    /**
     * Gson object for parsing incoming TradeInfo
     */
    private final Gson gson = new Gson();

    /**
     * Map of exchanges and symbols to which the client is subscribed
     */
    private final ExchangeMap subscribedExchanges = new ExchangeMap();
    /**
     * A map of exchanges and symbols to which the client has requested a subscription,
     * but the data on the past trades have not yet been sent to him
     */
    private final ExchangeMap requestedInitialDataMap = new ExchangeMap();
    /**
     * Current session
     */
    private Session session;

    /**
     * Starts the server
     */
    public static void start() {
        Server server = new Server("localhost", 8025, "",
                null, WSServerEndpoint.class);

        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        finally {
//            server.stop();
//        }
    }

    @OnOpen
    public void onOpen(Session session) {
        logger.info("New connection.");
        this.session = session;
        ServerMessage message = new ServerMessage(
                ServerMessageTypes.INFO,
                "Connection to atserver established!"
        );
        session.getAsyncRemote().sendText(gson.toJson(message));
    }

    /**
     * @return ExchangeClient matching the passed name,
     * or null if no matching ExchangeClient was found
     */
    private @Nullable ExchangeClient getExchangeClient(String exchangeName) {
        switch (exchangeName) {
            case ExchangeNames.BINANCE: return BinanceClient.getInstance();
            case ExchangeNames.HUOBI: return HuobiClient.getInstance();
            default: return null;
        }
    }

    /**
     * Asks trade data providers for updates
     *
     * @param exchangeMap ExchangeMap to be added to updates
     */
    private void requestClientUpdates(@NotNull ExchangeMap exchangeMap) {
        exchangeMap.forEach((requestedExchange, requestedSymbols) -> {
            // Adding new requests to the requests map considering that it might not be empty
            Set<String> alreadyRequestedSymbols = requestedInitialDataMap.get(requestedExchange);
            if (alreadyRequestedSymbols == null)
                requestedInitialDataMap.put(requestedExchange, requestedSymbols);
            else if (requestedSymbols != null)
                alreadyRequestedSymbols.addAll(requestedSymbols);
            // Requesting subscription to updates from ExchangeClients
            if (requestedSymbols != null && !requestedSymbols.isEmpty()) {
                ExchangeClient exchangeClient = getExchangeClient(requestedExchange);
                if (exchangeClient != null)
                    exchangeClient.subscribeToSymbolsUpdate(this, requestedSymbols);
            }
        });
    }

    /**
     * Removes all subscriptions of the client and subscribes it to the exchanges specified in msg
     */
    private void subscribeToUpdates(ClientMessage msg) {

        ExchangeMap exchangeMap = gson.fromJson(msg.message, ExchangeMap.class);
        if (exchangeMap == null || exchangeMap.isEmpty())
            subscribedExchanges.clear();
        else
            requestClientUpdates(exchangeMap);
    }

    /**
     * Subscribes the client to updates of the specified exchanges and symbols
     */
    private void addUpdates(ClientMessage msg) {
        // Updates which have to be added
        ExchangeMap exchangesToAdd = gson.fromJson(msg.message, ExchangeMap.class);

        // Removing from the list of required exchanges and symbols
        // everything that the client has already subscribed to
        exchangesToAdd.forEach((exchangeToAdd, symbolsToAdd) -> {
            Set<String> subscribedSymbols = subscribedExchanges.get(exchangeToAdd);
            if (subscribedSymbols != null)
                symbolsToAdd.removeAll(subscribedSymbols);
        });

        requestClientUpdates(exchangesToAdd);
    }

    /**
     * Unsubscribes this WSServerEndpoint object from updates in the corresponding ExchangeClients
     */
    private void unsubFromExchangeClients(ExchangeMap exchangesToUnsub) {
        exchangesToUnsub.forEach((exchangeToUnsub, symbolsToUnsub) -> {
            if (symbolsToUnsub != null) {
                ExchangeClient exchangeClient = getExchangeClient(exchangeToUnsub);
                if (exchangeClient != null)
                    exchangeClient.unsubFromSymbolsUpdate(this, symbolsToUnsub);
            }
        });
    }

    /**
     * Removes the specified updates for the client
     */
    private void removeSomeUpdates(ClientMessage msg) {
        // Exchanges the client wants to unsubscribe from
        ExchangeMap exchangesToRemove = gson.fromJson(msg.message, ExchangeMap.class);
        if (exchangesToRemove == null)
            return;

        // Sorting out the exchanges which the client wants to unsubscribe from
        exchangesToRemove.forEach((exchangeToRemove, symbolsToRemove) -> {
            // If among the exchanges which the client is subscribed to there is an exchange
            // with the same name as exchangeToRemove, then then remove from the list of
            // signed symbols of this exchange the symbols that correspond to symbolsToRemove
            Set<String> subscribedSymbols = subscribedExchanges.getSymbols(exchangeToRemove);
            if (subscribedSymbols != null) {
                subscribedSymbols.removeAll(symbolsToRemove);
                // If there is no symbols left in the list of subscribed symbols, then remove
                // the exchange from the client's subscriptions
                if (subscribedSymbols.isEmpty())
                    subscribedExchanges.remove(exchangeToRemove);
            }
        });
        unsubFromExchangeClients(exchangesToRemove);
    }

    /**
     * Removes all updates for the client
     */
    private void removeAllUpdates() {
        unsubFromExchangeClients(subscribedExchanges);
        subscribedExchanges.clear();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        ClientMessage msg = gson.fromJson(message, ClientMessage.class);
        switch (msg.messageType) {
            case ClientMessageTypes.SUBSCRIBE_TO_UPD:
                subscribeToUpdates(msg);
                break;
            case ClientMessageTypes.UNSUB_FROM_ALL_UPD:
                removeAllUpdates();
                break;
            case ClientMessageTypes.ADD_UPD:
                addUpdates(msg);
                break;
            case ClientMessageTypes.REMOVE_UPD:
                removeSomeUpdates(msg);
                break;
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.info("Error:\n");
        throwable.printStackTrace();
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        removeAllUpdates();
        logger.info(String.format("Session %s has been closed. Reason: %s", session, closeReason));
    }

    /**
     * {@inheritDoc}.
     * Loops through the list of all subscribed clients and looks for matches.
     * If a match is found, sends data to the client
     */
    @Override
    public void onAddData(TradeInfo tradeInfo) {
        // Symbols which the client is subscribed to
        Set<String> symbols = subscribedExchanges.getSymbols(tradeInfo.exchange);
        // If among the symbols which the client is subscribed to there is a match
        // with received data, then sending the data to the client
        if (symbols != null && symbols.contains(tradeInfo.symbol)) {
            ServerMessage serverMessage = new ServerMessage(ServerMessageTypes.NORMAL_MESSAGE,
                    gson.toJson(tradeInfo));
            session.getAsyncRemote().sendText(gson.toJson(serverMessage));
        }
    }

    /**
     * Subscribes a session to updates of the specified symbols on the specified exchange
     */
    private void subscribeToDataUpdate(String exchangeName,
                                       Set<String> symbols) {

        // Symbols which the client is subscribed to (on "exchangeName" exchange)
        Set<String> subscribedSymbols = subscribedExchanges.getSymbols(exchangeName);
        // If the client hasn't been subscribed to the "exchangeName" yet
        if (subscribedSymbols == null)
            subscribedExchanges.put(exchangeName, symbols);
        else
            subscribedSymbols.addAll(symbols);
    }

    /**
     * Sends data about past trades to the client,
     * removes sent symbols from the list of symbols waiting to be sent.
     * Subscribes the client to updates by symbols
     */
    private void sendInitialDataAndSubscribe(Set<String> requestedSymbols,
                                             List<List<TradeInfo>> tradeInfoListsFromProvider,
                                             String exchangeName) {

        // The list which will be sent to the client.
        // Contains of the sublists with the same symbols
        List<List<TradeInfo>> listToSend = new ArrayList<>();
        // Matches in requested and received symbols
        Set<String> coincidentSymbols = ConcurrentHashMap.newKeySet();
        // Sorting out list which came from the data provider
        for (List<TradeInfo> tradeInfoListFromProvider : tradeInfoListsFromProvider) {
            if (tradeInfoListFromProvider.isEmpty())
                continue;
            String providedSymbol = tradeInfoListFromProvider.get(0).symbol;
            if (requestedSymbols.contains(providedSymbol)) {
                listToSend.add(tradeInfoListFromProvider);
                coincidentSymbols.add(providedSymbol);
                requestedSymbols.remove(providedSymbol);
            }
        }
        // Sending message to the client
        ServerMessage serverMessage = new ServerMessage(ServerMessageTypes.INIT,
                gson.toJson(listToSend));
        String json = gson.toJson(serverMessage);
        logger.info(json);
        session.getAsyncRemote().sendText(gson.toJson(serverMessage));
        // Subscribing the client to the new data updates
        subscribeToDataUpdate(exchangeName, coincidentSymbols);
    }

    /**
     * Loops through the list of all subscribed clients and looks for matches.
     * If a match is found, sends data to the client
     */
    @Override
    public void onGetInitialData(@NotNull List<List<TradeInfo>> tradeInfo) {
        if (tradeInfo.isEmpty())
            return;
        // Exchange which received data refers to
        String exchangeName = tradeInfo.get(0).get(0).exchange;
        // If the session wants to subscribe to exchange from tradeInfo, then we get the
        // list of symbols which it wants to subscribe to
        Set<String> requestedSymbols = requestedInitialDataMap.getSymbols(exchangeName);
        if (requestedSymbols != null) {
            // Sending initial data to the client, deleting sent symbols from the list of
            // symbols which have to be sent. Subscribing the client to further
            // updates of the sent symbols
            sendInitialDataAndSubscribe(requestedSymbols, tradeInfo, exchangeName);
            // If there's no symbols left in the exchange symbols list, then removing the
            // exchange from the exchanges list
            if (requestedSymbols.isEmpty())
                requestedInitialDataMap.remove(exchangeName);
        }
    }
}
