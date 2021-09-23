package vorotilenko.trendanalyzer.fragments.select.exchange

import androidx.lifecycle.ViewModel
import vorotilenko.trendanalyzer.Currencies
import vorotilenko.trendanalyzer.ObservedSymbol
import vorotilenko.trendanalyzer.R

class SelectExchangeViewModel(observedSymbols: List<ObservedSymbol>) : ViewModel() {
    /**
     * All exchanges available for user.
     */
    private val allExchanges: Array<ExchangesListItem> by lazy {
        arrayOf(
            ExchangesListItem("Binance", R.mipmap.binance_logo),
            ExchangesListItem("Huobi", R.mipmap.huobi_logo)
        )
    }

    /**
     * Exchanges available to select.
     */
    val availableExchanges: Array<ExchangesListItem> by lazy {
        val availableSymbols = Currencies.availableSymbols.toMutableMap().mapValues { entry ->
            entry.value.toMutableMap().mapValues { it.value.toMutableList() }
        }
        for (symbol in observedSymbols) {
            val tickers = Currencies.getTickers(symbol.symbolName) ?: continue
            availableSymbols[symbol.exchangeName]?.get(tickers[0])?.remove(tickers[1])
        }
        val filteredSymbols = availableSymbols.filter { entry ->
            entry.value.filter { it.value.isNotEmpty() }.isNotEmpty()
        }
        allExchanges.filter { filteredSymbols.containsKey(it.name) }.toTypedArray()
    }

    /**
     * Exchanges which are shown in the list.
     */
    val shownExchanges: MutableList<ExchangesListItem> by lazy {
        allExchanges.toMutableList()
    }
}