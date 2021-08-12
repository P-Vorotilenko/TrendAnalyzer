package vorotilenko.trendanalyzerserver.server;

import vorotilenko.trendanalyzerserver.Currencies;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for storing exchange names and symbols associated with these exchange names.
 * {@link vorotilenko.trendanalyzerserver.ExchangeNames ExchangeNames} have to be used as the keys.
 * Symbols created by concatenation of {@link Currencies Currencies} have to be used as the values
 */
public class ExchangeMap extends ConcurrentHashMap<String, Set<String>> {

    /**
     * Returns a list of symbols for the specified exchange
     */
    public Set<String> getSymbols(String exchange) {
        return get(exchange);
    }

    public ExchangeMap() {
        super();
    }

    public ExchangeMap(ExchangeMap exchangeMap) {
        super(exchangeMap);
    }
}
