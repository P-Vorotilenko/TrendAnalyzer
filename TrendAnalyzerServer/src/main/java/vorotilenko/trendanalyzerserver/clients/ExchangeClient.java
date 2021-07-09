package vorotilenko.trendanalyzerserver.clients;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Client, which reads trade data by WebSocket protocol and writes it to DB
 * */
public abstract class ExchangeClient implements TradeInfoProvider {

    /**
     * Trade listeners
     * */
    protected final Map<TradeInfoListener, Set<String>> tradeInfoListeners =
            new ConcurrentHashMap<>();
    /**
     * Thread pool
     * */
    protected final ExecutorService executorService = Executors.newCachedThreadPool();

    /**
     * Returns exchange name as it is written in DB
     * */
    protected abstract String getExchangeName();

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribeToSymbolsUpdate(@NotNull TradeInfoListener listener,
                                         @NotNull Set<String> symbols) {

        executorService.submit(new SubscTIListenerWorker(
                listener, symbols, tradeInfoListeners, getExchangeName()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubFromSymbolsUpdate(TradeInfoListener listener, Set<String> symbols) {
        // Symbols listened at the current moment
        Set<String> listenedSymbols = tradeInfoListeners.get(listener);
        listenedSymbols.removeAll(symbols);
        if (listenedSymbols.isEmpty())
            tradeInfoListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyTradeInfoListeners(@NotNull TradeInfo tradeInfo) {
        tradeInfoListeners.forEach((listener, listenedSymbols) -> {
            if (listenedSymbols.contains(tradeInfo.symbol))
                listener.onAddData(tradeInfo);
        });
    }

    @Override
    public void finalize() throws Throwable {
        executorService.shutdownNow();
        while (true) {
            try {
                if (executorService.awaitTermination(5, TimeUnit.SECONDS))
                    break;
            } catch (InterruptedException ignored) {}
        }
        super.finalize();
    }
}
