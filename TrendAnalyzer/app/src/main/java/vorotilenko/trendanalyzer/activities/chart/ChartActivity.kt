package vorotilenko.trendanalyzer.activities.chart

import android.content.Intent
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.DataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.gson.Gson
import vorotilenko.trendanalyzer.Constants
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.TradeInfo
import vorotilenko.trendanalyzer.activities.ObservedSymbol
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity
import vorotilenko.trendanalyzer.serverinteraction.ServerMessageTypes
import vorotilenko.trendanalyzer.serverinteraction.WSClientEndpoint
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChartActivity : AppCompatActivity() {
    /**
     * Chart
     */
    private lateinit var chart: LineChart

    /**
     * When button "Back" was pressed last time
     */
    private var backBtnPressedTimestamp: Long = 0

    /**
     * Launcher for [ObservedSymbolsActivity]
     */
    private val observedSymbolsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK)
                removeRedundantDataSets()
        }

    /**
     * Stores data about trades. Key - label as it is presented in datasets of [chart].
     * Value - list of [TradeData]
     */
    private val tradesMap = TradesMap(startTradeTime)

    /**
     * Compares shown datasets with preferences. Removes datasets which are not
     * written in preferences from chart
     */
    private fun removeRedundantDataSets() {
        val jsonList = getSharedPreferences(Constants.LISTENED_SYMBOLS, MODE_PRIVATE)
            .getString(Constants.LISTENED_SYMBOLS, "[]")
        val symbolsInPreferences: ArrayList<ObservedSymbol> =
            gson.fromJson(jsonList, Constants.OBSERVED_SYMBOLS_LIST_TYPE)
        val labels = symbolsInPreferences.map { "${it.symbolTicker} ${it.exchangeName}" }
        chart.data.dataSets.removeAll { !labels.contains(it.label) }
    }

    /**
     * Gets color of dataset from preferences
     */
    private fun getDataSetColor(exchange: String, symbol: String): Int {
        val listJson =
            getSharedPreferences(Constants.LISTENED_SYMBOLS, MODE_PRIVATE)
                .getString(Constants.LISTENED_SYMBOLS, null)
        val observedItems: ArrayList<ObservedSymbol> =
            gson.fromJson(listJson, Constants.OBSERVED_SYMBOLS_LIST_TYPE)
        val item = observedItems.firstOrNull {
            it.exchangeName == exchange && it.symbolTicker == symbol
        }
        return item!!.colorOnChart
    }

    private fun createDataSet(entries: List<Entry>, label: String, color: Int) =
        LineDataSet(entries, label)
            .apply {
                this.color = color
                valueTextColor = Color.WHITE
                setDrawVerticalHighlightIndicator(false)
                lineWidth = 1f
                setDrawCircles(false)
            }

    /**
     * Adds dataset to data in [chart]. If chart.data is null, creates new LineData and
     * attaches it to chart
     */
    private fun addToLineData(dataSet: LineDataSet) {
        // Chart doesn't work normally if we initialize it with empty LineData
        if (chart.data == null) {
            val data = LineData(dataSet)
            data.setDrawValues(false)
            chart.data = data
        } else
            chart.data.addDataSet(dataSet)
    }

    /**
     * Calls functions to notify chart about changes and invalidate it
     */
    private fun invalidateChart() {
        chart.data?.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    /**
     * Adds dataset to the chart
     */
    private fun addDataSet(tradeInfoList: List<TradeInfo>) {
        val entries = tradeInfoList.map {
            val x = (it.tradeTimeMillis - startTradeTime).toFloat()
            val y = 0.5f
            Entry(x, y)
        }

        val exchange = tradeInfoList[0].exchange
        val symbol = tradeInfoList[0].symbol
        val dataSet = createDataSet(
            entries,
            "$symbol $exchange",
            getDataSetColor(exchange, symbol)
        )
        addToLineData(dataSet)
        setYCoordinates()
        invalidateChart()
    }

    /**
     * Adjusts the chart axes
     */
    private fun adjustAxes(chart: LineChart) {
        chart.axisRight.apply {
            setDrawAxisLine(false)
            //setDrawLabels(false)
            gridColor = ContextCompat.getColor(applicationContext, R.color.gridColor)
            gridLineWidth = GRID_LINE_WIDTH
            setDrawZeroLine(false)
            labelCount = 6
        }
        chart.axisLeft.isEnabled = false
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM_INSIDE
            setDrawAxisLine(false)
            gridColor = ContextCompat.getColor(applicationContext, R.color.gridColor)
            gridLineWidth = GRID_LINE_WIDTH
            textColor = ContextCompat.getColor(applicationContext, R.color.xAxisLabelsTextColor)
            textSize = 9.0f
            valueFormatter = IAxisValueFormatter { value, _ ->
                dateFormat.format(Date(startTradeTime + value.toLong()))
            }
            setLabelCount(3, false)
        }
    }

    /**
     * Adds a record to the matrix about the shift of the chart to its right border if needed
     */
    private fun translateChartIfNeeded(
        x: Float, newMaxVisibleX: Float,
        contentRect: RectF, matrix: Matrix
    ) {
        // Getting the difference between the new max visible X and the new max X
        val xDelta = x - newMaxVisibleX
        if (xDelta > 0.001) {
            // Converting X values to pixels
            val newMinVisibleX = chart.lowestVisibleX
            val visibleXDelta = newMaxVisibleX - newMinVisibleX
            val pixelsToTranslate = xDelta * contentRect.width() / visibleXDelta
            // Moving the chart to the right
            matrix.postTranslate(-pixelsToTranslate, 0f)
        }
    }

    /**
     * Dynamically adding values to the chart
     */
    private fun addEntryDynamic(x: Float, dataSet: LineDataSet) {
        // Max X that is currently displayed on the chart
        val maxVisibleX = chart.highestVisibleX
        // Min X that is currently displayed on the chart
        val minVisibleX = chart.lowestVisibleX
        // Adding new value
        dataSet.addEntry(Entry(x, 0.5f))
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
        // Getting new max visible X
        val newMaxVisibleX = chart.highestVisibleX
        // Getting new min visible X
        val newMinVisibleX = chart.lowestVisibleX
        // Getting ViewPortHandler, Matrix and contentRect
        val vpHandler = chart.viewPortHandler
        val matrix = vpHandler.matrixTouch
        val contentRect = vpHandler.contentRect
        // If needed, shifting the chart to the right so that its right border
        // has a new max X value
        translateChartIfNeeded(x, newMaxVisibleX, contentRect, matrix)
        // Scaling
        matrix.postScale((newMaxVisibleX - newMinVisibleX) / (maxVisibleX - minVisibleX),
            0f, contentRect.width() + vpHandler.offsetLeft(), 0f)
        // Applying changes
        vpHandler.refresh(matrix, chart, false)
    }

    /**
     * Sets the Y-coordinates of all visible points on the chart so that each
     * dataset takes up optimal space
     */
    private fun setYCoordinates() {
        val minVisibleX = chart.lowestVisibleX
        chart.data?.dataSets?.forEach { dataSet ->
            tradesMap[dataSet.label]?.let { tradesList ->
                var maxPrice = Double.MIN_VALUE
                var minPrice = Double.MAX_VALUE
                val firstVisibleEntryIndex =
                    dataSet.getEntryIndex(minVisibleX, 0.5f, DataSet.Rounding.CLOSEST)
                tradesList.subList(firstVisibleEntryIndex, tradesList.size)
                    .forEach {
                        val price = it.price
                        if (price > maxPrice)
                            maxPrice = price
                        if (price < minPrice)
                            minPrice = price
                    }
                val scatter = maxPrice - minPrice
                val delta = minPrice / scatter
                val entryCount = dataSet.entryCount
                for (i in firstVisibleEntryIndex until entryCount) {
                    dataSet.getEntryForIndex(i).y =
                        (tradesList[i].price / scatter - delta).toFloat()
                }
            }
        }
    }

    /**
     * Adds value to the chart
     */
    fun addEntry(tradeInfo: TradeInfo) {
        val tradeInfoLabel = "${tradeInfo.symbol} ${tradeInfo.exchange}"
        val dataSet = chart.data?.dataSets?.firstOrNull { it.label == tradeInfoLabel }
        dataSet?.let {
            val x = (tradeInfo.tradeTimeMillis - startTradeTime).toFloat()
            if (x > dataSet.xMax) {
                addEntryDynamic(x, dataSet as LineDataSet)
                setYCoordinates()
                invalidateChart()
            }
        }
    }

    /**
     * @return Map of observed symbols from preferences.
     * Key - exchange name. Value - array of symbol tickers
     */
    private fun getObservedSymbolsMap(): Map<String, List<String>> {
        val json = getSharedPreferences(Constants.LISTENED_SYMBOLS, MODE_PRIVATE)
            .getString(Constants.LISTENED_SYMBOLS, "[]")
        val observedSymbols: ArrayList<ObservedSymbol> =
            gson.fromJson(json, Constants.OBSERVED_SYMBOLS_LIST_TYPE)
        @Suppress("UNCHECKED_CAST")
        return observedSymbols
            .groupBy { it.exchangeName }
            .filterKeys { it != null }
            .mapValues {
                it.value.mapNotNull { item -> item.symbolTicker }
            } as Map<String, List<String>>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        chart = findViewById(R.id.chart)
        adjustAxes(chart)
        chart.apply {
            isScaleYEnabled = false
            isScaleXEnabled = false
            isDragEnabled = false
            description.isEnabled = false
            isKeepPositionOnRotation = true
            isAutoScaleMinMaxEnabled = true
            legend.form = Legend.LegendForm.CIRCLE
            setDrawBorders(false)
            setNoDataText(resources.getString(R.string.loading_data))
            setNoDataTextColor(ContextCompat.getColor(applicationContext, R.color.teal_200))
        }

        if (clientEndpointHandler == null)
            clientEndpointHandler = ClientEndpointHandler(this)
        else
            clientEndpointHandler!!.chartActivity = this
        val observedSymbolsMap = getObservedSymbolsMap()
        WSClientEndpoint.start(clientEndpointHandler!!, observedSymbolsMap)

        findViewById<ImageButton>(R.id.listBtn).setOnClickListener {
            val intent = Intent(applicationContext, ObservedSymbolsActivity::class.java)
            observedSymbolsLauncher.launch(intent)
        }
    }

    override fun onBackPressed() {
        val currTime = System.currentTimeMillis()
        if (currTime - backBtnPressedTimestamp < 3000) {
            WSClientEndpoint.stop()
            super.onBackPressed()
        } else {
            backBtnPressedTimestamp = currTime
            Toast.makeText(
                applicationContext,
                R.string.press_once_again,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Saves chart data to [outState]
     */
    private fun saveChartData(outState: Bundle) {
        chart.data?.let { chartData ->
            outState.putInt(DATASETS_COUNT, chartData.dataSetCount)
            for (i in chartData.dataSets.indices) {
                val dataSet = chartData.dataSets[i]
                outState.putString("$DATASET_LABEL$i", dataSet.label)
                outState.putInt("$DATASET_COLOR$i", dataSet.color)
                val entriesCount = dataSet.entryCount
                val arrayList = ArrayList<Entry?>(entriesCount)
                for (j in 0 until entriesCount)
                    arrayList.add(dataSet.getEntryForIndex(j))
                outState.putParcelableArrayList("$DATASET$i", arrayList)
            }
        }
    }

    /**
     * Saves [tradesMap] to [outState]
     */
    private fun saveTradesMap(outState: Bundle) {
        val tradesMapKeys = tradesMap.keys
        if (!tradesMapKeys.isNullOrEmpty()) {
            outState.putStringArray(TRADES_MAP_KEYS, tradesMapKeys.toTypedArray())
            tradesMap.entries.forEach { outState.putParcelableArrayList(it.key, it.value) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveChartData(outState)
        saveTradesMap(outState)
        super.onSaveInstanceState(outState)
    }

    /**
     * Restores chart data from [savedInstanceState]
     */
    private fun restoreChartData(savedInstanceState: Bundle) {
        val datasetsCount: Int? = savedInstanceState[DATASETS_COUNT] as Int?
        if (datasetsCount == null || datasetsCount <= 0) return
        for (i in 0 until datasetsCount) {
            val entries = savedInstanceState.getParcelableArrayList<Entry>("$DATASET$i")
            val label = savedInstanceState["$DATASET_LABEL$i"] as String?
            val color = savedInstanceState["$DATASET_COLOR$i"] as Int?
            if (entries != null && label != null && color != null)
                addToLineData(createDataSet(entries, label, color))
        }
    }

    /**
     * Restores [tradesMap] from [savedInstanceState]
     */
    private fun restoreTradesMap(savedInstanceState: Bundle) {
        savedInstanceState.getStringArray(TRADES_MAP_KEYS)?.let { tradesMapKeys ->
            tradesMapKeys.forEach { key ->
                savedInstanceState.getParcelableArrayList<TradeData>(key)?.let { list ->
                    tradesMap[key] = list
                }
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreChartData(savedInstanceState)
        restoreTradesMap(savedInstanceState)
        invalidateChart()
    }

    override fun onDestroy() {
        clientEndpointHandler?.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        /**
         * Key for datasets count in [onSaveInstanceState] and [onRestoreInstanceState]
         */
        private const val DATASETS_COUNT = "datasetsCount"

        /**
         * Key for datasets in [onSaveInstanceState] and [onRestoreInstanceState].
         * Has to be concatenated with number
         */
        private const val DATASET = "dataset"

        /**
         * Key for dataset labels in [onSaveInstanceState] and [onRestoreInstanceState].
         * Has to be concatenated with number
         */
        private const val DATASET_LABEL = "datasetLabel"

        /**
         * Key for dataset colors in [onSaveInstanceState] and [onRestoreInstanceState].
         * Has to be concatenated with number
         */
        private const val DATASET_COLOR = "datasetColor"

        /**
         * Key for the list of keys of [tradesMap].
         * Used in [onSaveInstanceState] and [onRestoreInstanceState]
         */
        private const val TRADES_MAP_KEYS = "tradesMapKeys"

        /**
         * Chart grid line width
         */
        private const val GRID_LINE_WIDTH = 1.5f

        /**
         * The time from which the time of all transactions is counted
         */
        private val startTradeTime = System.currentTimeMillis()

        /**
         * Date format to be shown under the chart
         */
        private val dateFormat: DateFormat = SimpleDateFormat(
            "dd-MM HH:mm:ss",
            Locale.ENGLISH
        )

        /**
         * Object for parsing JSON
         */
        private val gson = Gson()

        /**
         * Handler for getting messages from WSClientEndpoint
         */
        private class ClientEndpointHandler(chartActivity: ChartActivity) :
            Handler(chartActivity.mainLooper) {

            /**
             * Weak reference to Activity passed in the constructor
             */
            private var wrActivity: WeakReference<ChartActivity> = WeakReference(chartActivity)

            /**
             * Chart activity
             */
            var chartActivity: ChartActivity?
                get() = wrActivity.get()
                set(value) {
                    wrActivity = WeakReference(value)
                }

            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    ServerMessageTypes.INIT -> {
                        val activity = chartActivity
                        activity?.let {
                            @Suppress("UNCHECKED_CAST")
                            val list = msg.obj as List<TradeInfo>
                            it.tradesMap.add(list)
                            it.addDataSet(list)
                        }
                    }
                    ServerMessageTypes.NORMAL_MESSAGE -> {
                        val activity = chartActivity
                        activity?.let {
                            val tradeInfo = msg.obj as TradeInfo
                            it.tradesMap.add(tradeInfo)
                            it.addEntry(tradeInfo)
                        }
                    }
                }
            }
        }
        private var clientEndpointHandler: ClientEndpointHandler? = null
    }
}