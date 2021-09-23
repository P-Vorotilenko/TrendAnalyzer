package vorotilenko.trendanalyzer.fragments.observedsymbols

import android.app.Application
import androidx.lifecycle.ViewModel
import vorotilenko.trendanalyzer.viewmodel.AppViewModel
import vorotilenko.trendanalyzer.ObservedSymbol

/**
 * [ViewModel] for [ObservedSymbolsFragment].
 */
class ObservedSymbolsViewModel(private val appViewModel: AppViewModel) : ViewModel() {

    /**
     * Util for matching exchange and currency names with logo drawables.
     */
    private val logoMatcher =
        LogoMatcher(appViewModel.getApplication<Application>().applicationContext)

    /**
     * List of symbols observed by the application. Mutable.
     */
    private val mObservedSymbols: ArrayList<ObservedSymbol> by lazy {
        ArrayList(appViewModel.observedSymbols).onEach {
            it.exchangeLogo = logoMatcher.getLogoForExchange(it.exchangeName)
            logoMatcher.getLogosForCurrencyPair(it.symbolName)?.let { logos ->
                it.currency1Logo = logos[0]
                it.currency2Logo = logos[1]
            }
        }
    }

    /**
     * List of symbols observed by the application.
     */
    val observedSymbols: List<ObservedSymbol>
        get() = mObservedSymbols

    /**
     * Adds symbol to [appViewModel].
     */
    private fun addToAppViewModel(symbol: ObservedSymbol) {
        val symbolForAVM = symbol.copy().apply {
            exchangeLogo = null
            currency1Logo = null
            currency2Logo = null
            color = null
        }
        appViewModel.addSymbol(symbolForAVM)
    }

    /**
     * Adds the new symbol to the list of observed symbols.
     */
    fun addSymbol(symbol: ObservedSymbol) {
        mObservedSymbols.add(symbol)
        addToAppViewModel(symbol)
    }

    /**
     * Adds the new symbol to the list of observed symbols in the position.
     */
    fun addSymbol(position: Int, symbol: ObservedSymbol) {
        mObservedSymbols.add(position, symbol)
        addToAppViewModel(symbol)
    }

    /**
     * Removes the symbol from the list of observed symbols.
     */
    fun removeSymbol(symbol: ObservedSymbol?) {
        mObservedSymbols.remove(symbol)
        appViewModel.removeSymbol(symbol)
    }
}