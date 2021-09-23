package vorotilenko.trendanalyzer.fragments.observedsymbols

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vorotilenko.trendanalyzer.viewmodel.AppViewModel

class ObservedSymbolsVMFactory(private val appViewModel: AppViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObservedSymbolsViewModel::class.java))
            return ObservedSymbolsViewModel(appViewModel) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}