package vorotilenko.trendanalyzer.activities.chart

import vorotilenko.trendanalyzer.TradeData
import vorotilenko.trendanalyzer.TradeInfo

/**
 * Stores data about trades. Key - label as it is presented in datasets of [ChartActivity.chart].
 * Value - list of [TradeData]
 */
class TradesMap : HashMap<String, ArrayList<TradeData>>() {
    fun add(tradeInfoList: List<TradeInfo>) {
        if (tradeInfoList.isEmpty())
            return
        val firstItem = tradeInfoList[0]
        val label = "${firstItem.symbol} ${firstItem.exchange}"
        this[label] = ArrayList(tradeInfoList.map { TradeData(it.tradeTimeMillis, it.price) })
    }

    fun add(tradeInfo: TradeInfo) {
        val label = "${tradeInfo.symbol} ${tradeInfo.exchange}"
        this[label]?.add(TradeData(tradeInfo.tradeTimeMillis, tradeInfo.price))
            ?: ArrayList<TradeData>().apply {
                add(TradeData(tradeInfo.tradeTimeMillis, tradeInfo.price))
            }
    }
}