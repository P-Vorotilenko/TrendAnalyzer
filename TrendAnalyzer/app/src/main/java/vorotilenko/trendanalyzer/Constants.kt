package vorotilenko.trendanalyzer

import com.google.gson.reflect.TypeToken
import vorotilenko.trendanalyzer.activities.ObservedSymbol
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity

object Constants {
    /**
     * Key to listened symbols in preferences
     */
    const val LISTENED_SYMBOLS = "listenedSymbols"

    /**
     * Type of [ObservedSymbolsActivity.observedSymbols] list (for parsing json)
     */
    val OBSERVED_SYMBOLS_LIST_TYPE = object : TypeToken<ArrayList<ObservedSymbol>>(){}.type!!
}