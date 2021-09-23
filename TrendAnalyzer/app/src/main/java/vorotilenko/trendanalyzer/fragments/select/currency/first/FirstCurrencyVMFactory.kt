package vorotilenko.trendanalyzer.fragments.select.currency.first

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vorotilenko.trendanalyzer.ObservedSymbol

class FirstCurrencyVMFactory(
    private val symbolToAdd: ObservedSymbol,
    private val observedSymbols: List<ObservedSymbol>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirstCurrencyViewModel::class.java))
            return FirstCurrencyViewModel(symbolToAdd, observedSymbols) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}