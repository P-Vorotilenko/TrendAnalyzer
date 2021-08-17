package vorotilenko.trendanalyzer.activities.observedsymbols

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import vorotilenko.trendanalyzer.Constants
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.activities.ObservedSymbol
import vorotilenko.trendanalyzer.activities.select.exchange.SelectExchangeActivity
import vorotilenko.trendanalyzer.serverinteraction.WSClientEndpoint
import kotlin.random.Random

class ObservedSymbolsActivity : AppCompatActivity() {
    private lateinit var observedSymbols: ArrayList<ObservedSymbol>

    /**
     * Adapter for the RecyclerView
     */
    private lateinit var adapter: ObservedAdapter

    /**
     * Launcher for [SelectExchangeActivity]
     */
    private val selectActivitiesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK)
                return@registerForActivityResult
            val newItem: ObservedSymbol? = result.data?.extras?.getParcelable(NEW_ITEM)
            if (newItem == null)
                showWarningToast()
            else if (!observedSymbols.contains(newItem)) {
                setItemColor(newItem)
                observedSymbols.add(newItem)
                adapter.notifyItemInserted(observedSymbols.size - 1)
                handleItemAdded(observedSymbols.size - 1, newItem)
                finish()
            }
        }

    /**
     * Shows warning when item received from [SelectExchangeActivity] in
     * [selectActivitiesLauncher] is null
     */
    private fun showWarningToast() {
        Toast.makeText(
            applicationContext,
            R.string.err_selecting_symbol,
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Sets values to color components
     */
    private fun setColorComponents(
        component1: IntArray,
        component2: IntArray,
        component3: IntArray,
        random: Random
    ) {
        component1[0] = 218
        when (random.nextInt(1, 3)) {
            1 -> {
                component2[0] = 3
                component3[0] = random.nextInt(3, 219)
            }
            2 -> {
                component3[0] = 3
                component2[0] = random.nextInt(3, 219)
            }
        }
    }

    /**
     * Returns pretty random color
     */
    private fun getRandomItemColor(): Int {
        val random = Random(System.currentTimeMillis())
        val r = intArrayOf(0)
        val g = intArrayOf(0)
        val b = intArrayOf(0)

        when (random.nextInt(1, 4)) {
            1 -> setColorComponents(r, g, b, random)
            2 -> setColorComponents(g, r, b, random)
            3 -> setColorComponents(b, r, g, random)
        }
        return Color.rgb(r[0], g[0], b[0])
    }

    /**
     * Sets the color of this symbol on chart
     */
    private fun setItemColor(item: ObservedSymbol) {
        item.colorOnChart = when (observedSymbols.size) {
            0 -> ContextCompat.getColor(applicationContext, R.color.teal_200)
            1 -> ContextCompat.getColor(applicationContext, R.color.chart_color_2)
            2 -> ContextCompat.getColor(applicationContext, R.color.chart_color_3)
            3 -> ContextCompat.getColor(applicationContext, R.color.chart_color_4)
            4 -> ContextCompat.getColor(applicationContext, R.color.chart_color_5)
            5 -> ContextCompat.getColor(applicationContext, R.color.chart_color_6)
            6 -> ContextCompat.getColor(applicationContext, R.color.chart_color_7)
            7 -> ContextCompat.getColor(applicationContext, R.color.chart_color_8)
            8 -> ContextCompat.getColor(applicationContext, R.color.chart_color_9)
            9 -> ContextCompat.getColor(applicationContext, R.color.chart_color_10)
            else -> getRandomItemColor()
        }
    }

    /**
     * Saves [observedSymbols] to preferences
     */
    private fun saveToPrefs() {
        getSharedPreferences(Constants.LISTENED_SYMBOLS, MODE_PRIVATE)
            .edit()
            .putString(Constants.LISTENED_SYMBOLS, gson.toJson(observedSymbols))
            .apply()
    }

    /**
     * Initialization when savedInstanceState is null
     */
    private fun commonInit(rvObservedSymbols: RecyclerView) {
        val observedSymbolsJson =
            getSharedPreferences(Constants.LISTENED_SYMBOLS, MODE_PRIVATE)
                .getString(Constants.LISTENED_SYMBOLS, "[]")
        observedSymbols = gson.fromJson(observedSymbolsJson, Constants.OBSERVED_SYMBOLS_LIST_TYPE)
        if (observedSymbols.isEmpty()) {
            findViewById<TextView>(R.id.tvNothingObserved).visibility = View.VISIBLE
            rvObservedSymbols.visibility = View.INVISIBLE
        }
    }

    /**
     * Restoring from savedInstanceState
     */
    private fun restore(savedInstanceState: Bundle?, rvObservedSymbols: RecyclerView) {
        findViewById<TextView>(R.id.tvNothingObserved).visibility =
            savedInstanceState?.get(TV_VISIBILITY) as Int
        rvObservedSymbols.visibility = savedInstanceState[RV_VISIBILITY] as Int
        observedSymbols =
            savedInstanceState.getParcelableArrayList<ObservedSymbol>(OBSERVED_SYMBOLS)
                    as ArrayList<ObservedSymbol>
    }

    /**
     * Saves list to preferences, sets visibility of views,
     * says [WSClientEndpoint] to remove updates and sets activity result to OK
     */
    private fun handleItemDeleted(position: Int, item: ObservedSymbol?) {
        saveToPrefs()
        if (observedSymbols.isEmpty()) {
            findViewById<RecyclerView>(R.id.rvObservedSymbols).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.tvNothingObserved).visibility = View.VISIBLE
        }
        val exchangeName = item?.exchangeName
        val symbolTicker = item?.symbolTicker
        if (exchangeName != null && symbolTicker != null)
            WSClientEndpoint.removeUpdates(exchangeName, symbolTicker)
        setResult(RESULT_OK)
    }

    /**
     * Saves list to preferences, sets visibility of views,
     * says [WSClientEndpoint] to add updates and sets activity result to OK
     */
    private fun handleItemAdded(position: Int, item: ObservedSymbol?) {
        saveToPrefs()
        if (observedSymbols.size == 1) {
            findViewById<RecyclerView>(R.id.rvObservedSymbols).visibility = View.VISIBLE
            findViewById<TextView>(R.id.tvNothingObserved).visibility = View.INVISIBLE
        }
        val exchangeName = item?.exchangeName
        val symbolTicker = item?.symbolTicker
        if (exchangeName != null && symbolTicker != null)
            WSClientEndpoint.addUpdates(exchangeName, symbolTicker)
        setResult(RESULT_OK)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observed_symbols)

        val rvObservedSymbols = findViewById<RecyclerView>(R.id.rvObservedSymbols)

        if (savedInstanceState == null) commonInit(rvObservedSymbols)
        else restore(savedInstanceState, rvObservedSymbols)

        adapter = ObservedAdapter(
            applicationContext, observedSymbols, ::handleItemDeleted, ::handleItemAdded
        )
        rvObservedSymbols.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(rvObservedSymbols)

        findViewById<FloatingActionButton>(R.id.addSymbolBtn).setOnClickListener {
            val intent = Intent(applicationContext, SelectExchangeActivity::class.java)
            selectActivitiesLauncher.launch(intent)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(TV_VISIBILITY,
            findViewById<TextView>(R.id.tvNothingObserved).visibility)
        outState.putInt(RV_VISIBILITY,
            findViewById<RecyclerView>(R.id.rvObservedSymbols).visibility)
        outState.putParcelableArrayList(OBSERVED_SYMBOLS, observedSymbols)
        super.onSaveInstanceState(outState)
    }

    companion object {

        /**
         * Key for activity result passed from
         * [SelectExchangeActivity]
         */
        const val NEW_ITEM = "newItem"

        /**
         * Key for restoring visibility of TextView with text "No symbols are currently observed"
         * from savedInstanceState
         */
        private const val TV_VISIBILITY = "tvVisibility"

        /**
         * Key for restoring visibility of RecyclerView from savedInstanceState
         */
        private const val RV_VISIBILITY = "rvVisibility"

        /**
         * Key for restoring the list of symbols from savedInstanceState
         */
        private const val OBSERVED_SYMBOLS = "observedSymbols"

        /**
         * For parsing json in onCreate
         */
        private val gson = Gson()
    }
}