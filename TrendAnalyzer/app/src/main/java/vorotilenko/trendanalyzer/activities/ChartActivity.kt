package vorotilenko.trendanalyzer.activities

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.TradeData
import vorotilenko.trendanalyzer.TradeInfo
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
     * The time from which the time of all transactions is counted
     */
    private var startTradeTime = System.currentTimeMillis()

    /**
     * @returns Test dataset of trades
     */
    private val testData: Array<TradeData>
        get() = arrayOf(
            TradeData(0, 50031.040000),
            TradeData(59, 50031.040000),
            TradeData(169, 50031.040000),
            TradeData(307, 50031.040000),
            TradeData(342, 50031.040000),
            TradeData(344, 50031.040000),
            TradeData(402, 50031.030000),
            TradeData(746, 50028.570000),
            TradeData(943, 50027.530000),
            TradeData(1061, 50027.530000),
            TradeData(1283, 50030.060000),
            TradeData(1454, 50030.050000),
            TradeData(1482, 50030.060000),
            TradeData(1519, 50030.060000),
            TradeData(1555, 50030.050000),
            TradeData(1727, 50030.060000),
            TradeData(1937, 50030.050000),
            TradeData(1994, 50030.050000),
            TradeData(2001, 50030.050000),
            TradeData(2005, 50030.050000),
            TradeData(2221, 50024.040000),
            TradeData(2266, 50024.050000),
            TradeData(2285, 50024.040000),
            TradeData(2303, 50024.050000),
            TradeData(2351, 50024.050000),
            TradeData(2370, 50024.050000),
            TradeData(2678, 50024.000000),
            TradeData(2733, 50024.010000),
            TradeData(3345, 50024.000000),
            TradeData(3752, 50023.990000),
            TradeData(4004, 50021.480000),
            TradeData(4405, 50020.170000),
            TradeData(4442, 50020.160000),
            TradeData(4442, 50020.140000),
            TradeData(4520, 50023.110000),
            TradeData(4946, 50023.110000),
            TradeData(5519, 50023.110000),
            TradeData(5543, 50023.110000),
            TradeData(5744, 50023.110000),
            TradeData(5744, 50027.470000),
            TradeData(5744, 50027.470000),
            TradeData(5744, 50027.480000),
            TradeData(5744, 50028.410000),
            TradeData(5788, 50028.140000),
            TradeData(6482, 50028.140000),
            TradeData(6624, 50028.150000),
            TradeData(6762, 50028.140000),
            TradeData(6779, 50028.150000),
            TradeData(6828, 50028.150000),
            TradeData(6852, 50028.150000),
            TradeData(6852, 50030.920000),
            TradeData(6852, 50031.040000),
            TradeData(6852, 50034.200000),
            TradeData(6854, 50034.200000),
            TradeData(6916, 50028.150000),
            TradeData(7290, 50028.140000),
            TradeData(7319, 50028.150000),
            TradeData(7388, 50028.150000),
            TradeData(8348, 50024.060000),
            TradeData(8386, 50024.060000),
            TradeData(8386, 50024.050000),
            TradeData(8470, 50024.060000),
            TradeData(8518, 50024.060000),
            TradeData(8754, 50024.050000),
            TradeData(9003, 50024.060000),
            TradeData(9382, 50024.060000),
            TradeData(9440, 50024.050000),
            TradeData(9449, 50024.060000),
            TradeData(9471, 50024.050000),
            TradeData(9588, 50024.060000),
            TradeData(9597, 50024.050000),
            TradeData(9742, 50024.050000),
            TradeData(9772, 50024.050000),
            TradeData(9774, 50024.050000),
            TradeData(10037, 50024.060000),
            TradeData(10256, 50024.050000),
            TradeData(10314, 50024.050000),
            TradeData(11036, 50024.050000),
            TradeData(11273, 50024.060000),
            TradeData(11392, 50024.050000),
            TradeData(11964, 50024.060000),
            TradeData(12286, 50024.060000),
            TradeData(12313, 50024.060000),
            TradeData(12343, 50024.060000),
            TradeData(12487, 50024.060000),
            TradeData(12544, 50024.060000),
            TradeData(12564, 50024.050000),
            TradeData(12855, 50024.050000),
            TradeData(12913, 50024.060000),
            TradeData(13916, 50024.060000),
            TradeData(14311, 50024.050000),
            TradeData(14338, 50024.060000),
            TradeData(14338, 50026.360000),
            TradeData(14585, 50028.150000),
            TradeData(14678, 50028.140000),
            TradeData(15033, 50027.290000),
            TradeData(15261, 50027.290000),
            TradeData(15698, 50025.640000),
            TradeData(15864, 50028.140000),
            TradeData(15864, 50028.150000),
            TradeData(16033, 50032.750000),
            TradeData(16034, 50032.750000),
            TradeData(16818, 50032.760000),
            TradeData(17143, 50024.570000),
            TradeData(17187, 50030.250000),
            TradeData(17665, 50030.240000),
            TradeData(17751, 50030.230000),
            TradeData(18247, 50030.230000),
            TradeData(18375, 50030.230000),
            TradeData(18537, 50030.240000),
            TradeData(18570, 50030.240000),
            TradeData(18617, 50030.230000),
            TradeData(18868, 50030.230000),
            TradeData(18895, 50030.230000),
            TradeData(19069, 50030.230000),
            TradeData(19077, 50030.230000),
            TradeData(19109, 50030.230000),
            TradeData(19109, 50028.140000),
            TradeData(19115, 50028.140000),
            TradeData(19115, 50024.640000),
            TradeData(19115, 50024.060000),
            TradeData(19115, 50024.050000),
            TradeData(19115, 50024.000000),
            TradeData(19115, 50023.690000),
            TradeData(19115, 50019.450000),
            TradeData(19115, 50016.640000),
            TradeData(19115, 50016.020000),
            TradeData(19115, 50016.020000),
            TradeData(19606, 50014.290000),
            TradeData(19701, 50014.290000),
            TradeData(19713, 50014.290000),
            TradeData(19748, 50014.290000),
            TradeData(20165, 50014.280000),
            TradeData(20244, 50012.980000),
            TradeData(20263, 50012.980000),
            TradeData(20286, 50012.980000),
            TradeData(20315, 50012.980000),
            TradeData(20493, 50009.630000),
            TradeData(20829, 50008.010000),
            TradeData(20879, 50008.010000),
            TradeData(21170, 50008.010000),
            TradeData(21275, 50008.010000),
            TradeData(21279, 50008.000000),
            TradeData(21279, 50007.990000),
            TradeData(21364, 50007.990000),
            TradeData(21526, 50007.990000),
            TradeData(21618, 50000.990000),
            TradeData(21980, 50000.000000),
            TradeData(22008, 50000.000000),
            TradeData(22053, 50000.000000),
            TradeData(22194, 50000.010000),
            TradeData(22596, 50000.010000),
            TradeData(23259, 50000.000000),
            TradeData(23459, 50000.010000),
            TradeData(23529, 50000.000000),
            TradeData(23742, 50000.000000),
            TradeData(23768, 50000.000000),
            TradeData(23768, 49999.990000),
            TradeData(23793, 49999.990000),
            TradeData(23793, 49999.980000),
            TradeData(23800, 49999.980000),
            TradeData(23800, 49999.980000),
            TradeData(23818, 49998.000000),
            TradeData(23868, 49998.010000),
            TradeData(23868, 49998.000000),
            TradeData(23868, 49994.600000),
            TradeData(23871, 49994.600000),
            TradeData(23892, 49994.600000),
            TradeData(23892, 49994.590000),
            TradeData(23903, 49994.590000),
            TradeData(23903, 49994.100000),
            TradeData(23903, 49993.010000),
            TradeData(23903, 49990.500000),
            TradeData(23903, 49990.000000),
            TradeData(23903, 49988.150000),
            TradeData(23903, 49988.140000),
            TradeData(23903, 49987.150000),
            TradeData(23903, 49987.050000),
            TradeData(23903, 49986.410000),
            TradeData(23903, 49986.280000),
            TradeData(23903, 49985.710000),
            TradeData(23903, 49984.590000),
            TradeData(23911, 49994.600000),
            TradeData(23911, 49994.600000),
            TradeData(24801, 49992.090000),
            TradeData(24974, 49992.090000),
            TradeData(24987, 49992.080000),
            TradeData(25849, 50000.800000),
            TradeData(25851, 50000.800000),
            TradeData(26019, 50001.000000),
            TradeData(26084, 50000.990000),
            TradeData(26181, 50001.000000),
            TradeData(26181, 50001.000000),
            TradeData(26181, 50001.000000),
            TradeData(26196, 50001.000000),
            TradeData(26288, 50001.000000),
            TradeData(26681, 50003.040000),
            TradeData(26682, 50003.040000),
            TradeData(26682, 50003.040000),
            TradeData(26821, 50003.050000),
            TradeData(26821, 50008.000000),
            TradeData(26821, 50008.010000),
            TradeData(26821, 50008.710000),
            TradeData(26821, 50008.720000),
            TradeData(26821, 50012.370000),
            TradeData(26821, 50012.380000),
            TradeData(26821, 50013.790000),
            TradeData(26821, 50014.270000),
            TradeData(26821, 50014.280000),
            TradeData(26821, 50014.610000),
            TradeData(26821, 50015.720000),
            TradeData(26870, 50003.100000),
            TradeData(27493, 50004.230000),
            TradeData(27561, 50004.230000),
            TradeData(27739, 50004.230000),
            TradeData(27739, 50000.990000),
            TradeData(27739, 50000.810000),
            TradeData(27763, 50000.820000),
            TradeData(27819, 50000.820000),
            TradeData(27897, 50000.820000),
            TradeData(27998, 50000.820000),
            TradeData(28063, 50000.820000),
            TradeData(28189, 50000.820000),
            TradeData(28192, 50000.830000),
            TradeData(28218, 50000.820000),
            TradeData(28262, 50000.820000),
            TradeData(28294, 50000.830000),
            TradeData(28332, 50000.830000),
            TradeData(28365, 50000.830000),
            TradeData(28397, 50000.830000),
            TradeData(28535, 50000.830000),
            TradeData(28592, 50000.830000),
            TradeData(28702, 50000.830000),
            TradeData(28978, 50000.010000),
            TradeData(28979, 50000.010000),
            TradeData(29161, 50000.010000),
            TradeData(29161, 50000.010000),
            TradeData(29194, 50000.010000),
            TradeData(29393, 50000.020000),
            TradeData(29402, 50000.010000),
            TradeData(30177, 50000.010000),
            TradeData(30537, 50000.010000),
            TradeData(30800, 50000.020000),
            TradeData(30800, 50004.480000),
            TradeData(30824, 50000.020000),
            TradeData(30847, 50004.390000),
            TradeData(30972, 50004.390000),
            TradeData(31001, 50000.020000),
            TradeData(31569, 50000.040000),
            TradeData(31569, 50000.020000),
            TradeData(31569, 50000.010000),
            TradeData(31670, 50000.010000),
            TradeData(32207, 50003.870000),
            TradeData(32229, 50003.860000),
            TradeData(32333, 50000.030000),
            TradeData(32610, 50000.040000),
            TradeData(33044, 50000.020000),
            TradeData(33045, 50000.020000),
            TradeData(33151, 50000.010000),
            TradeData(33151, 50000.000000),
            TradeData(33151, 49996.410000),
            TradeData(33181, 49994.580000),
            TradeData(33184, 49994.580000),
            TradeData(33339, 49996.420000),
            TradeData(33424, 49996.420000),
            TradeData(33612, 50002.160000),
            TradeData(33641, 50002.160000),
            TradeData(33776, 49998.950000),
            TradeData(33987, 50002.160000),
            TradeData(34012, 50002.160000),
            TradeData(34022, 50002.160000),
            TradeData(34164, 50002.170000),
            TradeData(34424, 50002.170000),
            TradeData(34510, 50002.170000),
            TradeData(34559, 50004.790000),
            TradeData(34924, 50005.800000),
            TradeData(34954, 50005.790000),
            TradeData(35148, 50005.800000),
            TradeData(35148, 50013.730000),
            TradeData(35148, 50013.740000),
            TradeData(35307, 50005.800000),
            TradeData(35373, 50005.800000),
            TradeData(35523, 50005.790000),
            TradeData(35843, 50005.800000),
            TradeData(35941, 50005.800000),
            TradeData(36549, 50004.070000),
            TradeData(36635, 50004.080000),
            TradeData(36751, 50004.080000),
            TradeData(36791, 50004.080000),
            TradeData(36811, 50004.080000),
            TradeData(37022, 50004.070000),
            TradeData(37022, 50000.830000),
            TradeData(37022, 49997.200000),
            TradeData(37128, 49999.720000),
            TradeData(37184, 49999.720000),
            TradeData(37602, 49999.720000),
            TradeData(37866, 49999.710000),
            TradeData(37867, 49999.720000),
            TradeData(38313, 49999.720000),
            TradeData(38517, 50003.050000),
            TradeData(38834, 50003.060000),
            TradeData(39008, 50003.050000),
            TradeData(39306, 50005.790000),
            TradeData(40030, 49999.690000),
            TradeData(40157, 50001.450000),
            TradeData(40157, 49995.010000),
            TradeData(40157, 49995.000000),
            TradeData(40157, 49992.080000),
            TradeData(40157, 49989.710000),
            TradeData(40157, 49989.700000),
            TradeData(40157, 49989.320000),
            TradeData(40157, 49987.850000),
            TradeData(40165, 49987.850000),
            TradeData(40169, 49987.850000),
            TradeData(40171, 49987.850000),
            TradeData(40175, 49987.850000),
            TradeData(40176, 49987.850000),
            TradeData(40177, 49987.850000),
            TradeData(40179, 49987.850000),
            TradeData(40227, 50001.420000),
            TradeData(40227, 50003.220000),
            TradeData(40255, 50003.210000),
            TradeData(40309, 50003.210000),
            TradeData(40309, 50005.780000),
            TradeData(40322, 50005.780000),
            TradeData(40334, 50005.780000),
            TradeData(40347, 50003.200000),
            TradeData(40486, 50003.200000),
            TradeData(40486, 49992.720000),
            TradeData(40586, 50005.720000)
        )

    /**
     * Creates test LineDataSet
     */
    private fun createTestDataSet(): LineDataSet {
        val trades = testData
        val entries: MutableList<Entry> = ArrayList()
        for (trade in trades) {
            // Turning data into entry objects
            entries.add(Entry(trade.tradeTimeMillis.toFloat(), trade.price.toFloat()))
        }
        // Adding entries to dataset
        val dataSet = LineDataSet(entries, "BTC/USDT")
        dataSet.color = Color.CYAN
        dataSet.valueTextColor = Color.WHITE
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        //dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        return dataSet
    }

    /**
     * Adds dataset to the chart
     */
    private fun addDataSet(tradeInfoList: List<TradeInfo>) {
        val tradeInfoFirst = tradeInfoList[0]
        val symbolAndExchange = "${tradeInfoFirst.symbol} ${tradeInfoFirst.exchange}"

        val entries: MutableList<Entry> = ArrayList(tradeInfoList.size)
        for (tradeInfo in tradeInfoList) {
            val x = (tradeInfo.tradeTimeMillis - startTradeTime).toFloat()
            val y = tradeInfo.price.toFloat()
            entries.add(Entry(x, y))
        }

        val dataSet = LineDataSet(entries, symbolAndExchange)
        dataSet.color = Color.CYAN
        dataSet.valueTextColor = Color.WHITE
        dataSet.setDrawVerticalHighlightIndicator(false)
        dataSet.lineWidth = 1f
        dataSet.setDrawCircles(false)

        if (chart.data == null) {
            val data = LineData(dataSet)
            data.setDrawValues(false)
            chart.data = data
        } else {
            dataSet.color = Color.GREEN
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
        val rightAxis = chart.axisRight
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawLabels(false)
        rightAxis.gridColor = ContextCompat.getColor(applicationContext, R.color.gridColor)
        rightAxis.gridLineWidth = GRID_LINE_WIDTH
        rightAxis.setDrawZeroLine(false)
        rightAxis.labelCount = 6
        val leftAxis = chart.axisLeft
        leftAxis.isEnabled = false
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
        xAxis.setDrawAxisLine(false)
        xAxis.gridColor = ContextCompat.getColor(applicationContext, R.color.gridColor)
        xAxis.gridLineWidth = GRID_LINE_WIDTH
        xAxis.textColor = ContextCompat.getColor(applicationContext, R.color.gridColor)
        xAxis.textSize = 9.0f
        xAxis.valueFormatter = IAxisValueFormatter { value: Float, _: AxisBase? ->
            val tradeTime = startTradeTime + value.toLong()
            dateFormat.format(Date(tradeTime))
        }
        xAxis.setLabelCount(3, false)
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

    fun onTestAddEntryClick(view: View?) {
        val dataSet = chart.data.getDataSetByIndex(0) as LineDataSet?
        val lastTradeTime = dataSet!!.getEntryForIndex(dataSet.entryCount - 1).x
        addEntryDynamic(lastTradeTime + 500, (random.nextInt(20) + 50000).toFloat(),
            dataSet)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chart = findViewById(R.id.chart)
        adjustAxes(chart)
        chart.run {
            isScaleYEnabled = false
            isScaleXEnabled = false
            isDragEnabled = false
            description.isEnabled = false
            isKeepPositionOnRotation = true
            isAutoScaleMinMaxEnabled = true
            legend.form = Legend.LegendForm.CIRCLE
        }
        chart.setDrawBorders(false)

        //TODO: it has to be optimized
        clientEndpointHandler = ClientEndpointHandler(this)
        WSClientEndpoint.start(clientEndpointHandler)
    }

    override fun onDestroy() {
        clientEndpointHandler.removeCallbacksAndMessages(null)
        super.onDestroy()
        ChartActivity.EXCHANGE_NAME
    }

    companion object {

        /**
         * Key for exchange name passed from
         * [SelectFirstCurrencyActivity][vorotilenko.trendanalyzer.activities.select.SelectFirstCurrencyActivity]
         */
        const val EXCHANGE_NAME = "exchangeName"

        /**
         * Key for symbol passed from
         * [SelectFirstCurrencyActivity][vorotilenko.trendanalyzer.activities.select.SelectFirstCurrencyActivity]
         */
        const val SYMBOL = "symbol"

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
         * Object for generating random values
         */
        private val random = Random(System.currentTimeMillis())

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