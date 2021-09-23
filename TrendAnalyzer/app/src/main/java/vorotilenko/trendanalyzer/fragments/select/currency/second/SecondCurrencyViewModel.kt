package vorotilenko.trendanalyzer.fragments.select.currency.second

import androidx.lifecycle.ViewModel
import vorotilenko.trendanalyzer.Currencies
import vorotilenko.trendanalyzer.ObservedSymbol
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.fragments.select.currency.CurrenciesListItem

class SecondCurrencyViewModel(
    private val symbolToAdd: ObservedSymbol,
    private val observedSymbols: List<ObservedSymbol>
) : ViewModel() {

    /**
     * All currencies.
     */
    private val allCurrencies: Array<CurrenciesListItem> by lazy {
        arrayOf(
            CurrenciesListItem(Currencies.BITCOIN, Currencies.BTC, R.mipmap.bitcoin_logo),
            CurrenciesListItem(Currencies.ETHEREUM, Currencies.ETH, R.mipmap.ethereum_logo),
            CurrenciesListItem(Currencies.CARDANO, Currencies.ADA, R.mipmap.cardano_logo),
            CurrenciesListItem(Currencies.XRP, Currencies.XRP, R.mipmap.xrp_logo),
            CurrenciesListItem(Currencies.POLKADOT, Currencies.DOT, R.mipmap.polkadot_logo),
            CurrenciesListItem(Currencies.UNISWAP, Currencies.UNI, R.mipmap.uniswap_logo),
            CurrenciesListItem(Currencies.BITCOIN_CASH, Currencies.BCH, R.mipmap.bitcoin_cash_logo),
            CurrenciesListItem(Currencies.LITECOIN, Currencies.LTC, R.mipmap.litecoin_logo),
            CurrenciesListItem(Currencies.SOLANA, Currencies.SOL, R.mipmap.solana_logo),
            CurrenciesListItem(Currencies.CHAINLINK, Currencies.LINK, R.mipmap.chainlink_logo),
            CurrenciesListItem(Currencies.TETHER, Currencies.USDT, R.mipmap.tether_logo)
        )
    }

    /**
     * Currencies available to select according to the first selected currency.
     */
    val availableCurrencies: Array<CurrenciesListItem> by lazy {
        val exchangeName = symbolToAdd.exchangeName
        val firstCurrency = symbolToAdd.symbolTicker
        //TODO: refactor
        allCurrencies.filter {
            val availablePairs = Currencies.availableSymbols[exchangeName]?.get(firstCurrency)
                ?.filter { secondCurrency: String ->
                    var currencyIsFree = true
                    observedSymbols.forEach { symbol: ObservedSymbol ->
                        if (symbol.exchangeName == exchangeName &&
                            symbol.symbolTicker == "$firstCurrency$secondCurrency") {
                            currencyIsFree = false
                            return@forEach
                        }
                    }
                    currencyIsFree
                }
            availablePairs != null && availablePairs.contains(it.ticker)
        }.toTypedArray()
    }

    /**
     * Currencies which are shown in the list according to user's search.
     */
    val shownCurrencies: MutableList<CurrenciesListItem> by lazy {
        availableCurrencies.toMutableList()
    }
}