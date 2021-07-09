package vorotilenko.trendanalyzerserver.clients;

import java.util.List;

/**
 * Listener which reacts to the trade data updates
 */
public interface TradeInfoListener {
    /**
     * Called when the data is added
     */
    void onAddData(TradeInfo tradeInfo);

    /**
     * Called when the list of data about past trades is added for the first time
     */
    void onGetInitialData(List<List<TradeInfo>> tradeInfo);
}
