package vorotilenko.trendanalyzer.fragments.chart

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
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
import com.google.gson.reflect.TypeToken
import vorotilenko.trendanalyzer.ObservedSymbol
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.TradeInfo
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Upgraded version of [LineChart]. Simplifies displaying data about trades.
 */
class TrendAnalyzerLineChart(context: Context, attrs: AttributeSet) : LineChart(context, attrs) {

    /**
     * Stores data about trades. Key - label as it is presented in datasets of chart.
     * Value - list of [TradeData].
     */
    var tradesMap = TradesMap(startTradeTime)

    init {
        setAxesProperties(context)
        isScaleYEnabled = false
        isScaleXEnabled = false
        isDragEnabled = false
        description.isEnabled = false
        isKeepPositionOnRotation = true
        isAutoScaleMinMaxEnabled = true
        legend.form = Legend.LegendForm.CIRCLE
        setDrawBorders(false)
        setNoDataText(resources.getString(R.string.pls_select_symbols))
        setNoDataTextColor(ContextCompat.getColor(context, R.color.teal_200))
    }

    /**
     * Saves [tradesMap] to preferences.
     */
    private fun saveTradesMap(editor: SharedPreferences.Editor) {
        val json = gson.toJson(tradesMap as HashMap<String, ArrayList<TradeData>>)
        editor.putString(TRADES_MAP, json)
    }

    /**
     * Saves chart data to preferences.
     */
    private fun saveChartData(editor: SharedPreferences.Editor) {
        if (data == null) return
        editor.apply {
            putInt(DATASETS_COUNT, data.dataSetCount)
            for (i in data.dataSets.indices) {
                val dataSet = data.dataSets[i]
                putString("$DATASET_LABEL$i", dataSet.label)
                putInt("$DATASET_COLOR$i", dataSet.color)
                val entriesCount = dataSet.entryCount
                val entries = ArrayList<Entry?>(entriesCount)
                for (j in 0 until entriesCount)
                    entries.add(dataSet.getEntryForIndex(j))
                putString("$DATASET$i", gson.toJson(entries))
            }
        }
    }

    /**
     * Saves all chart data to preferences.
     */
    fun save() {
        context.getSharedPreferences(CHART_PREFS, MODE_PRIVATE).edit().run {
            putLong(START_TRADE_TIME, startTradeTime)
            saveTradesMap(this)
            saveChartData(this)
            apply()
        }
    }

    /**
     * Restores [tradesMap] from preferences.
     */
    private fun restoreTradesMap(prefs: SharedPreferences, currTime: Long) {
        val json = prefs.getString(TRADES_MAP, null) ?: return
        val map: HashMap<String, ArrayList<TradeData>> = gson.fromJson(json, TRADES_MAP_TYPE)
        tradesMap.clear()
        map.forEach { entry ->
            val trades = entry.value
            if (currTime - trades.last().tradeTimeMillis < 600000) {
                val tradesCleared = trades.dropWhile { currTime - it.tradeTimeMillis > 600000 }
                tradesMap[entry.key] = ArrayList(tradesCleared)
            }
        }
    }

    /**
     * Restores chart data from preferences.
     */
    private fun restoreChartData(prefs: SharedPreferences, currTime: Long) {
        data?.clearValues()
        val datasetsCount: Int = prefs.getInt(DATASETS_COUNT, -1)
        for (i in 0 until datasetsCount) {
            val entriesJson = prefs.getString("$DATASET$i", "[]")
            val entries: ArrayList<Entry> = gson.fromJson(entriesJson, ENTRIES_LIST_TYPE)
            if (currTime - entries.last().x > 600000) continue
            val label = prefs.getString("$DATASET_LABEL$i", null) ?: continue
            val color = prefs.getInt("$DATASET_COLOR$i", -1)
            val entriesCleared = entries.dropWhile { currTime - it.x > 600000 }
            if (color != -1 && entriesCleared.isNotEmpty())
                addToData(createDataSet(entriesCleared, label, color))
        }
    }

    /**
     * If the chart data stored in preferences is not too old, restores it.
     */
    fun restore() {
        with(context.getSharedPreferences(CHART_PREFS, MODE_PRIVATE)) {
            val recordedStartTime = getLong(START_TRADE_TIME, 0)
            if (recordedStartTime != startTradeTime) return
            val currTime = System.currentTimeMillis() - startTradeTime
            restoreTradesMap(this, currTime)
            restoreChartData(this, currTime)
        }
    }

    /**
     * Sets properties for the chart axes.
     */
    private fun setAxesProperties(context: Context) {
        axisRight.apply {
            setDrawAxisLine(false)
            //setDrawLabels(false)
            gridColor = ContextCompat.getColor(context, R.color.gridColor)
            gridLineWidth = GRID_LINE_WIDTH
            setDrawZeroLine(false)
            labelCount = 6
        }
        axisLeft.isEnabled = false
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM_INSIDE
            setDrawAxisLine(false)
            gridColor = ContextCompat.getColor(context, R.color.gridColor)
            gridLineWidth = GRID_LINE_WIDTH
            textColor = ContextCompat.getColor(context, R.color.xAxisLabelsTextColor)
            textSize = 9.0f
            valueFormatter = IAxisValueFormatter { value, _ ->
                dateFormat.format(Date(startTradeTime + value.toLong()))
            }
            setLabelCount(3, false)
        }
    }

    /**
     * Compares shown datasets with preferences. Removes datasets which are not
     * written in preferences from chart.
     */
    fun removeRedundantDataSets(observedSymbols: List<ObservedSymbol>) {
        if (data == null) return
        val labels = observedSymbols.map { "${it.symbolTicker} ${it.exchangeName}" }
        data?.dataSets?.removeAll { !labels.contains(it.label) }
        tradesMap.entries.removeAll{ !labels.contains(it.key) }
        if (data?.dataSetCount == 0) data = null
    }

    /**
     * Removes the corresponding dataset.
     * @return True if the dataset was removed. False otherwise.
     */
    fun removeDataSet(exchange: String?, symbolTicker: String?): Boolean {
        if (exchange == null || symbolTicker == null || data == null) return false
        val label = "$symbolTicker $exchange"
        val dataSets = data.dataSets
        dataSets.forEach {
            if (it.label == label) {
                data.removeDataSet(it)
                tradesMap.remove(label)
                if (dataSets.isEmpty()) data = null
                return true
            }
        }
        return false
    }

    /**
     * Creates new [LineDataSet].
     */
    private fun createDataSet(entries: List<Entry>, label: String, color: Int) =
        LineDataSet(entries, label).apply {
            this.color = color
            //valueTextColor = Color.WHITE
            setDrawVerticalHighlightIndicator(false)
            lineWidth = 1f
            setDrawCircles(false)
            setDrawValues(false)
        }

    /**
     * Adds dataset to the chart data. If chart.data is null, creates new LineData and
     * attaches it to chart.
     */
    private fun addToData(dataSet: LineDataSet) {
        // Chart doesn't work normally if we initialize it with empty LineData
        if (data == null)
            data = LineData(dataSet).also { it.setDrawValues(false) }
        else
            data.addDataSet(dataSet)
    }

    /**
     * Sets the Y-coordinates of all visible points on the chart so that each
     * dataset takes up optimal space.
     */
    private fun setYCoordinates() {
        val minVisibleX = lowestVisibleX
        if (data?.dataSets.isNullOrEmpty()) return
        for (dataSet in data.dataSets) {
            val tradesList = tradesMap[dataSet.label] ?: continue
            var maxPrice = Double.MIN_VALUE
            var minPrice = Double.MAX_VALUE
            val firstVisibleEntryIndex =
                dataSet.getEntryIndex(minVisibleX, 0.5f, DataSet.Rounding.CLOSEST)
            tradesList.subList(firstVisibleEntryIndex, tradesList.size).forEach {
                val price = it.price
                if (price > maxPrice)
                    maxPrice = price
                if (price < minPrice)
                    minPrice = price
            }
            val scatter = maxPrice - minPrice
            val delta = minPrice / scatter
            val entryCount = dataSet.entryCount
            for (i in firstVisibleEntryIndex until entryCount)
                dataSet.getEntryForIndex(i).y = (tradesList[i].price / scatter - delta).toFloat()
        }
    }

    /**
     * Calls methods to notify chart about changes and invalidate it.
     */
    private fun invalidateChart() {
        data?.notifyDataChanged()
        notifyDataSetChanged()
        invalidate()
    }

    /**
     * Adds dataset to the chart.
     */
    fun addDataSet(tradeInfoList: List<TradeInfo>, color: Int) {
        if (tradeInfoList.isEmpty()) return

        val lastItemTime = tradeInfoList.last().tradeTimeMillis
        val clearedList = tradeInfoList.dropWhile { lastItemTime - it.tradeTimeMillis > 600000 }
        val entries = clearedList.map {
            val x = (it.tradeTimeMillis - startTradeTime).toFloat()
            val y = 0.5f
            Entry(x, y)
        }

        val label = "${clearedList[0].symbol} ${clearedList[0].exchange}"
        tradesMap.add(clearedList, entries, label)

        addToData(createDataSet(entries, label, color))
        setYCoordinates()
        invalidateChart()
    }

    /**
     * Adds a record to the matrix about the shift of the chart to its right border if needed.
     */
    private fun translateChartIfNeeded(
        x: Float, newMaxVisibleX: Float,
        contentRect: RectF, matrix: Matrix
    ) {
        // Getting the difference between the new max visible X and the new max X
        val xDelta = x - newMaxVisibleX
        if (xDelta > 0.001) {
            // Converting X values to pixels
            val newMinVisibleX = lowestVisibleX
            val visibleXDelta = newMaxVisibleX - newMinVisibleX
            val pixelsToTranslate = xDelta * contentRect.width() / visibleXDelta
            // Moving the chart to the right
            matrix.postTranslate(-pixelsToTranslate, 0f)
        }
    }

    /**
     * Dynamically adding values to the chart.
     */
    private fun addEntryDynamic(x: Float, dataSet: LineDataSet) {
        // Max X that is currently displayed on the chart
        val maxVisibleX = highestVisibleX
        // Min X that is currently displayed on the chart
        val minVisibleX = lowestVisibleX
        // Adding new value
        dataSet.addEntry(Entry(x, 0.5f))
        data.notifyDataChanged()
        notifyDataSetChanged()
        // Getting new max visible X
        val newMaxVisibleX = highestVisibleX
        // Getting new min visible X
        val newMinVisibleX = lowestVisibleX
        // Getting ViewPortHandler, Matrix and contentRect
        val vpHandler = viewPortHandler
        val matrix = vpHandler.matrixTouch
        val contentRect = vpHandler.contentRect
        // If needed, shifting the chart to the right so that its right border
        // has a new max X value
        translateChartIfNeeded(x, newMaxVisibleX, contentRect, matrix)
        // Scaling
        matrix.postScale((newMaxVisibleX - newMinVisibleX) / (maxVisibleX - minVisibleX),
            0f, contentRect.width() + vpHandler.offsetLeft(), 0f)
        // Applying changes
        vpHandler.refresh(matrix, this, false)
    }

    /**
     * Adds value to the chart.
     */
    fun addEntry(tradeInfo: TradeInfo) {
        val tradeInfoLabel = "${tradeInfo.symbol} ${tradeInfo.exchange}"
        data?.dataSets?.firstOrNull { it.label == tradeInfoLabel }?.let { dataSet ->
            val relativeTradeTime = tradeInfo.tradeTimeMillis - startTradeTime
            tradesMap.add(tradeInfoLabel, relativeTradeTime, tradeInfo.price)
            val x = relativeTradeTime.toFloat()
            if (x > dataSet.xMax) {
                addEntryDynamic(x, dataSet as LineDataSet)
                setYCoordinates()
                invalidateChart()
            }
        }
    }

    companion object {
        /**
         * Name of the chart preferences.
         */
        private const val CHART_PREFS = "chartPreferences"

        /**
         * Key for [startTradeTime] in [save] and [restore].
         */
        private const val START_TRADE_TIME = "startTradeTime"

        /**
         * Key for datasets count in [save] and [restore].
         */
        private const val DATASETS_COUNT = "datasetsCount"

        /**
         * Key for datasets in [save] and [restore].
         * Has to be concatenated with number.
         */
        private const val DATASET = "dataset"

        /**
         * Key for dataset labels in [save] and [restore].
         * Has to be concatenated with number.
         */
        private const val DATASET_LABEL = "datasetLabel"

        /**
         * Key for dataset colors in [save] and [restore].
         * Has to be concatenated with number.
         */
        private const val DATASET_COLOR = "datasetColor"

        /**
         * Key for the [TrendAnalyzerLineChart.tradesMap].
         * Used in [save] and [restore].
         */
        private const val TRADES_MAP = "tradesMap"

        /**
         * Type which [TradesMap] extends. Needed for restoring [tradesMap].
         */
        private val TRADES_MAP_TYPE =
            object : TypeToken<HashMap<String, ArrayList<TradeData>>>(){}.type!!

        /**
         * Type of entries list for restoring chart data.
         */
        private val ENTRIES_LIST_TYPE = object : TypeToken<ArrayList<Entry>>(){}.type!!

        /**
         * The time from which the time of all transactions is counted.
         */
        private val startTradeTime = System.currentTimeMillis()

        /**
         * Object for parsing JSON.
         */
        private val gson = Gson()

        /**
         * Chart grid line width.
         */
        private const val GRID_LINE_WIDTH = 1.5f

        /**
         * Date format to be shown under the chart.
         */
        private val dateFormat: DateFormat = SimpleDateFormat(
            "dd-MM HH:mm:ss",
            Locale.ENGLISH
        )
    }
}