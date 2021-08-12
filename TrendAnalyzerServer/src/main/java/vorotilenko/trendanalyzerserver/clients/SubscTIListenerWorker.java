package vorotilenko.trendanalyzerserver.clients;

import vorotilenko.trendanalyzerserver.dbinteraction.DBConnection;

import javax.swing.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Subscribes listener of {@link TradeInfoProvider} to updates of requested symbols
 */
public class SubscTIListenerWorker extends SwingWorker<List<List<TradeInfo>>, Integer> {
    /**
     * Query for getting all of the trade data from the DB
     */
    private static final String GET_TRADE_INFO_QUERY =
            "SELECT S.Symbol AS Symbol, TradeTimeMillis, Price FROM tradeInfo\n" +
                    "JOIN symbols AS S ON S.Id = tradeInfo.SymbolId\n" +
                    "   \tAND S.Symbol IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n" +
                    "JOIN exchanges AS E ON E.Id = tradeInfo.ExchangeId\n" +
                    "   \tAND E.Exchange = ?\n" +
                    "ORDER BY Symbol, TradeTimeMillis";
    /**
     * Max params num for the PreparedStatement
     */
    protected static final int MAX_SYMBOLS_NUM = 10;

    /**
     * Listener which has to be subscribed
     */
    private final TradeInfoListener listener;
    /**
     * Symbols which listener wants to subscribe on
     */
    private final Set<String> symbols;
    /**
     * Current listeners list
     */
    private final Map<TradeInfoListener, Set<String>> tradeInfoListeners;
    /**
     * Exchange name which the object is associated with
     */
    private final String exchangeName;

    /**
     * Constructor
     */
    public SubscTIListenerWorker(TradeInfoListener listener, Set<String> symbols,
                                 Map<TradeInfoListener, Set<String>> tradeInfoListeners,
                                 String exchangeName) {
        super();
        this.listener = listener;
        this.symbols = symbols;
        this.tradeInfoListeners = tradeInfoListeners;
        this.exchangeName = exchangeName;
    }

    /**
     * Adds records from the ResultSet to the list of lists of TradeInfo
     *
     * @param resultSet ResultSet which is set up to the first symbol
     * @return List of lists of TradeInfo sorted by symbols
     */
    private List<List<TradeInfo>> getSortedTradeInfoLists(ResultSet resultSet)
            throws SQLException {

        List<List<TradeInfo>> sortedTradeInfoLists = new ArrayList<>();
        // Sublist which will be added to the sortedTradeInfoLists
        List<TradeInfo> tradeInfoList = new ArrayList<>();
        // Getting symbol int the first record
        String currRecordsSymbol = resultSet.getString(1);

        do {
            String recordSymbol = resultSet.getString(1);
            // If the symbol int the record doesn't equal the symbol which we work with
            // then add the sublist to the main list
            if (!recordSymbol.equals(currRecordsSymbol)) {
                sortedTradeInfoLists.add(tradeInfoList);
                tradeInfoList = new ArrayList<>();
                currRecordsSymbol = recordSymbol;
            }
            // Adding record to the sublist
            tradeInfoList.add(new TradeInfo(
                    recordSymbol,
                    resultSet.getLong(2),
                    resultSet.getDouble(3),
                    exchangeName));
        } while (resultSet.next());

        sortedTradeInfoLists.add(tradeInfoList);

        return sortedTradeInfoLists;
    }

    /**
     * Forms the list to be sent to listener
     */
    protected List<List<TradeInfo>> createTradeinfoLists(PreparedStatement pStatement)
            throws SQLException {
        // Filling pStatement params with the keys
        int symbolsSize = symbols.size();
        int i = 1;
        for (String symbol : symbols)
            pStatement.setString(i++, symbol);
        // Filling unused params with NULL
        for (i = symbolsSize + 1; i <= MAX_SYMBOLS_NUM; i++)
            pStatement.setNull(i, Types.VARCHAR);
        pStatement.setString(MAX_SYMBOLS_NUM + 1, exchangeName);
        // Getting data
        ResultSet resultSet = pStatement.executeQuery();
        List<List<TradeInfo>> sortedTradeInfoLists = null;
        // If data isn't empty
        if (resultSet.next()) {
            // Putting data to the list of lists. In each sublist there are datasets
            // which have the same symbols
            sortedTradeInfoLists = getSortedTradeInfoLists(resultSet);
        }
        resultSet.close();

        return sortedTradeInfoLists;
    }

    /**
     * Sends the last 10 minutes trade info to listener and subscribes it to the specified
     * symbols updates. Adds listener to the listeners list
     */
    protected List<List<TradeInfo>> addNewListener(PreparedStatement pStatement)
            throws SQLException {
        //TODO: Compensate the pause between sending data from the DB and the subscription

        // Sending data to the trades listener
        List<List<TradeInfo>> tradeInfo = createTradeinfoLists(pStatement);
        // Turning passed Set into the ConcurrentSet
        Set<String> symbolsConcurrent = ConcurrentHashMap.newKeySet(symbols.size());
        symbolsConcurrent.addAll(symbols);
        // Putting the new listener into the listeners list
        tradeInfoListeners.put(listener, symbolsConcurrent);

        return tradeInfo;
    }

    /**
     * Sends the last 10 minutes trade info to listener and subscribes it to the specified
     * symbols updates
     */
    protected List<List<TradeInfo>> subscExistingListenerToSymbols(PreparedStatement pStatement)
            throws SQLException {

        //TODO: Compensate the pause between sending data from the DB and the subscription

        // The list of symbols which the listener has already subscribed to
        Set<String> subscribedSymbols = tradeInfoListeners.get(listener);
        // Removing the symbols which the listener hsa already subscribed to
        symbols.removeAll(subscribedSymbols);
        // Sending the trade info to the listener
        List<List<TradeInfo>> tradeInfo = createTradeinfoLists(pStatement);
        // Subscribing the listener to the new symbols
        subscribedSymbols.addAll(symbols);

        return tradeInfo;
    }

    @Override
    protected List<List<TradeInfo>> doInBackground() {
        List<List<TradeInfo>> tradeInfo = null;
        try (Connection connection = DBConnection.getNewConnection();
             PreparedStatement pStatement = connection.prepareStatement(GET_TRADE_INFO_QUERY)) {
            // Sending to the listener info about the past 10 trading minutes
            // for the specified symbols.
            // Subscribing the listener to the updates of this symbols.
            if (!tradeInfoListeners.containsKey(listener))
                tradeInfo = addNewListener(pStatement);
            else
                tradeInfo = subscExistingListenerToSymbols(pStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tradeInfo;
    }

    @Override
    protected void done() {
        try {
            List<List<TradeInfo>> sortedTradeInfoLists = get();
            if (sortedTradeInfoLists != null)
                listener.onGetInitialData(sortedTradeInfoLists);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
