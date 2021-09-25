package vorotilenko.trendanalyzer.viewmodel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import vorotilenko.trendanalyzer.ObservedSymbol
import vorotilenko.trendanalyzer.TradeInfo
import vorotilenko.trendanalyzer.serverinteraction.ClientEndpointStates.LOADING
import vorotilenko.trendanalyzer.serverinteraction.WSClientEndpoint
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * View model of the application
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {

    init {
        WSClientEndpoint.start(this)
    }

    /**
     * Utility for getting colors for the chart datasets.
     */
    private val colorUtil = DatasetsColorUtil(getApplication<Application>().applicationContext)

    /**
     * [ObservedSymbol] which the user is going to add.
     */
    var symbolToAdd: ObservedSymbol? = null

    /**
     * Symbols which are currently observed by the app. Mutable.
     */
    private val mObservedSymbols: ArrayList<ObservedSymbol> by lazy {
        val observedSymbolsJson = getApplication<Application>().applicationContext
            .getSharedPreferences(LISTENED_SYMBOLS, MODE_PRIVATE)
            .getString(LISTENED_SYMBOLS, "[]")
        val observedSymbols: ArrayList<ObservedSymbol> =
            gson.fromJson(observedSymbolsJson, OBSERVED_SYMBOLS_LIST_TYPE)
        observedSymbols.onEach { it.color = reserveColor() }
    }

    /**
     * Symbols which are currently observed by the app.
     */
    val observedSymbols: List<ObservedSymbol>
        get() = mObservedSymbols.map { it.copy() }

    /**
     * LiveData with the queue of [TradeInfo] received from server
     * (in the same order as it was received).
     */
    val tradeInfoQueueData: MutableLiveData<Queue<TradeInfo>> by lazy {
        MutableLiveData<Queue<TradeInfo>>().also { it.postValue(ConcurrentLinkedQueue()) }
    }

    /**
     * LiveData with the queue of [TradeInfo] lists received from server
     * (in the same order as it was received).
     */
    val tradeInfoListsQueueData: MutableLiveData<Queue<List<TradeInfo>>> by lazy {
        MutableLiveData<Queue<List<TradeInfo>>>().also { it.postValue(ConcurrentLinkedQueue()) }
    }

    /**
     * LiveData with the state of [WSClientEndpoint].
     */
    val serverEndpointStateData =
        MutableLiveData<Int>().apply { value = LOADING }

    /**
     * @return The free color from array of free colors. If all colors in array are taken, returns
     * randomly generated color.
     */
    private fun reserveColor() = colorUtil.reserveColor()

    /**
     * Sets the dataset color to free. It can be reused in other dataset.
     */
    private fun recycleColor(color: Int?) = colorUtil.unlockColor(color)

    /**
     * @return Color for displaying on chart for this combination.
     */
    fun getDatasetColor(exchange: String?, symbolTicker: String?): Int? {
        return observedSymbols.firstOrNull {
            it.exchangeName == exchange && it.symbolTicker == symbolTicker
        }?.color
    }

    /**
     * Saves [mObservedSymbols] to preferences.
     */
    private fun saveObservedSymbols() = getApplication<Application>().applicationContext
        .getSharedPreferences(LISTENED_SYMBOLS, MODE_PRIVATE)
        .edit()
        .putString(LISTENED_SYMBOLS, gson.toJson(mObservedSymbols))
        .apply()

    /**
     * Adds the new symbol to the list of observed symbols. Saves the list to preferences.
     */
    fun addSymbol(symbol: ObservedSymbol) {
        val exchange = symbol.exchangeName ?: return
        val ticker = symbol.symbolTicker ?: return
        symbol.color = reserveColor()
        mObservedSymbols.add(symbol)
        saveObservedSymbols()
        WSClientEndpoint.addUpdates(exchange, ticker)
    }

    /**
     * Removes the symbol from the list of observed symbols. Saves the list to preferences.
     */
    fun removeSymbol(symbol: ObservedSymbol?) {
        val exchange = symbol?.exchangeName ?: return
        val ticker = symbol.symbolTicker ?: return
        WSClientEndpoint.removeUpdates(exchange, ticker)
        mObservedSymbols.remove(symbol)
        saveObservedSymbols()
        recycleColor(symbol.color)
    }

    override fun onCleared() {
        super.onCleared()
        WSClientEndpoint.stop()
    }

    companion object {
        /**
         * Object for parsing JSON
         */
        private val gson = Gson()

        /**
         * Key to listened symbols in preferences
         */
        private const val LISTENED_SYMBOLS = "listenedSymbols"

        /**
         * Type of [AppViewModel.observedSymbols] list (for parsing json)
         */
        private val OBSERVED_SYMBOLS_LIST_TYPE =
            object : TypeToken<ArrayList<ObservedSymbol>>(){}.type!!
    }
}