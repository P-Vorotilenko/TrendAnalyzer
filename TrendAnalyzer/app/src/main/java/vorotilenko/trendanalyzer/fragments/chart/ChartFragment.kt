package vorotilenko.trendanalyzer.fragments.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.TradeInfo
import vorotilenko.trendanalyzer.serverinteraction.ClientEndpointStates.CONNECTION_ERROR
import vorotilenko.trendanalyzer.serverinteraction.ClientEndpointStates.LOADING
import vorotilenko.trendanalyzer.serverinteraction.ClientEndpointStates.NORMAL
import vorotilenko.trendanalyzer.viewmodel.AppViewModel
import java.util.*

/**
 * Displays the chart with the trade data.
 */
class ChartFragment : Fragment() {

    /**
     * Chart.
     */
    private lateinit var chart: TrendAnalyzerLineChart

    /**
     * ViewModel of the application.
     */
    private val appViewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_chart, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chart = view.findViewById(R.id.chart)
        view.findViewById<ImageButton>(R.id.listBtn)?.setOnClickListener { button ->
            val action = ChartFragmentDirections.actionToObservedSymbols()
            button.findNavController().navigate(action)
        }
    }

    /**
     * Sets views visibility in case of connection error.
     */
    private fun setViewsVisibilityConnectionError() {
        chart.visibility = View.INVISIBLE
        view?.findViewById<ImageButton>(R.id.listBtn)?.visibility = View.INVISIBLE
        view?.findViewById<ProgressBar>(R.id.chartProgressBar)?.visibility = View.INVISIBLE
        view?.findViewById<TextView>(R.id.tvServerUnavailable)?.visibility = View.VISIBLE
    }

    /**
     * Sets views visibility in case of loading.
     */
    private fun setViewsVisibilityLoading() {
        chart.visibility = View.INVISIBLE
        view?.findViewById<ImageButton>(R.id.listBtn)?.visibility = View.INVISIBLE
        view?.findViewById<ProgressBar>(R.id.chartProgressBar)?.visibility = View.VISIBLE
        view?.findViewById<TextView>(R.id.tvServerUnavailable)?.visibility = View.INVISIBLE
    }

    /**
     * Sets views visibility in case of normal work.
     */
    private fun setViewsVisibilityNormal() {
        chart.visibility = View.VISIBLE
        view?.findViewById<ImageButton>(R.id.listBtn)?.visibility = View.VISIBLE
        view?.findViewById<ProgressBar>(R.id.chartProgressBar)?.visibility = View.INVISIBLE
        view?.findViewById<TextView>(R.id.tvServerUnavailable)?.visibility = View.INVISIBLE
    }

    /**
     * Subscribes to the updates of [appViewModel].
     */
    private fun subscribeToViewModelUpdates() {
        appViewModel.tradeInfoQueueData.observe(this) { queue: Queue<TradeInfo> ->
            var tradeInfo = queue.poll()
            while (tradeInfo != null) {
                chart.addEntry(tradeInfo)
                tradeInfo = queue.poll()
            }
        }
        appViewModel.tradeInfoListsQueueData.observe(this) { queue: Queue<List<TradeInfo>> ->
            do {
                val tradeInfoList = queue.poll()
                if (tradeInfoList.isNullOrEmpty()) continue
                val exchange = tradeInfoList[0].exchange
                val symbol = tradeInfoList[0].symbol
                chart.removeDataSet(exchange, symbol)
                val color = appViewModel.getDatasetColor(exchange, symbol) ?: continue
                chart.addDataSet(tradeInfoList, color)
            } while (tradeInfoList != null)
        }
        appViewModel.serverEndpointStateData.observe(this) { state: Int ->
            when (state) {
                CONNECTION_ERROR -> setViewsVisibilityConnectionError()
                LOADING -> setViewsVisibilityLoading()
                NORMAL -> setViewsVisibilityNormal()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        chart.restore()
        chart.removeRedundantDataSets(appViewModel.observedSymbols)
        subscribeToViewModelUpdates()
    }

    override fun onStop() {
        super.onStop()
        chart.save()
    }
}