package vorotilenko.trendanalyzerserver.clients;

import java.util.Set;

/**
 * Provides trade data as a TradeInfo objects
 */
public interface TradeInfoProvider {
    /**
     * Subscribes the listener to updates by the specified symbols
     */
    void subscribeToSymbolsUpdate(TradeInfoListener listener, Set<String> symbols);
    /**
     * Unsubscribes the listener to updates by the specified symbols
     */
    void unsubFromSymbolsUpdate(TradeInfoListener listener, Set<String> symbols);
    /**
     * Notifies trade info listeners about data update
     */
    void notifyTradeInfoListeners(TradeInfo tradeInfo);
}
