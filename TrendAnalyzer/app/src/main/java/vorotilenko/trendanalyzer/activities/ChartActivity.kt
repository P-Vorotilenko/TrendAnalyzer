package vorotilenko.trendanalyzer.activities

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.gson.Gson
import vorotilenko.trendanalyzer.Constants
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.TradeInfo
import vorotilenko.trendanalyzer.serverinteraction.ServerMessageTypes
import vorotilenko.trendanalyzer.serverinteraction.WSClientEndpoint
import java.lang.ref.WeakReference
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity

class ChartActivity : AppCompatActivity() {
    /**
     * Chart
     */
    private lateinit var chart: LineChart

    /**
     * The time from which the time of all transactions is counted
     */
    private var startTradeTime = System.currentTimeMillis()

    /**
     * Gets color of dataset from preferences
     */
    private fun getDatasetColor(exchange: String, symbol: String): Int {
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

    /**
     * Adds dataset to the chart
     */
    private fun addDataSet(tradeInfoList: List<TradeInfo>) {
        val entries = tradeInfoList.map {
            val x = (it.tradeTimeMillis - startTradeTime).toFloat()
            val y = it.price.toFloat()
            Entry(x, y)
        }

        val exchange = tradeInfoList[0].exchange
        val symbol = tradeInfoList[0].symbol
        val dataSet =
            LineDataSet(entries, "$symbol $exchange")
                .apply {
                    color = Color.CYAN
                    valueTextColor = Color.WHITE
                    setDrawVerticalHighlightIndicator(false)
                    lineWidth = 1f
                    setDrawCircles(false)
                }
        // Chart doesn't work normally if we initialize it with empty LineData
        if (chart.data == null) {
            val data = LineData(dataSet)
            data.setDrawValues(false)
            chart.data = data
        } else {
            dataSet.color = getDatasetColor(exchange, symbol)
            chart.data.addDataSet(dataSet)
        }
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.invalidate()
    }

    /**
     * Adjusts the chart axes
     */
    private fun adjustAxes(chart: LineChart) {
        chart.axisRight.apply {
            setDrawAxisLine(false)
            setDrawLabels(false)
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
            textColor = ContextCompat.getColor(applicationContext, R.color.gridColor)
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
    private fun addEntryDynamic(x: Float, y: Float, dataSet: LineDataSet) {
        // Max X that is currently displayed on the chart
        val maxVisibleX = chart.highestVisibleX
        // Min X that is currently displayed on the chart
        val minVisibleX = chart.lowestVisibleX
        // Adding new value
        dataSet.addEntry(Entry(x, y))
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
        vpHandler.refresh(matrix, chart, true)
    }

    fun addEntry(tradeInfo: TradeInfo) {
        val tradeInfoLabel = "${tradeInfo.symbol} ${tradeInfo.exchange}"
        chart.data?.dataSets?.forEach {
            if (it.label == tradeInfoLabel) {
                val x = (tradeInfo.tradeTimeMillis - startTradeTime).toFloat()
                val y = tradeInfo.price.toFloat()
                addEntryDynamic(x, y, it as LineDataSet)
                return@forEach
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
        }

        val observedSymbolsMap = getObservedSymbolsMap()

        //TODO: it has to be optimized
        clientEndpointHandler = ClientEndpointHandler(this)
        WSClientEndpoint.start(clientEndpointHandler, observedSymbolsMap)
    }

    override fun onDestroy() {
        clientEndpointHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    companion object {
        /**
         * Chart grid line width
         */
        private const val GRID_LINE_WIDTH = 1.5f

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
            private val wrActivity: WeakReference<ChartActivity> = WeakReference(chartActivity)

            override fun handleMessage(msg: Message) {
                val chartActivity = wrActivity.get()
                when (msg.what) {
                    ServerMessageTypes.INIT -> {
                        @Suppress("UNCHECKED_CAST")
                        chartActivity?.addDataSet(msg.obj as List<TradeInfo>)
                    }
                    ServerMessageTypes.NORMAL_MESSAGE -> {
                        chartActivity?.addEntry(msg.obj as TradeInfo)
                    }
                }
            }
        }
        private lateinit var clientEndpointHandler: ClientEndpointHandler
    }
}