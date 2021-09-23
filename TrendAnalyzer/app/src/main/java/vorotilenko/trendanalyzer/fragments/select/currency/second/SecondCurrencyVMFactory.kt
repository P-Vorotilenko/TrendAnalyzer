package vorotilenko.trendanalyzer.fragments.select.currency.second

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vorotilenko.trendanalyzer.ObservedSymbol

class SecondCurrencyVMFactory(
    private val symbolToAdd: ObservedSymbol,
    private val observedSymbols: List<ObservedSymbol>
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecondCurrencyViewModel::class.java))
            return SecondCurrencyViewModel(symbolToAdd, observedSymbols) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}