package vorotilenko.trendanalyzer.activities.chart

import vorotilenko.trendanalyzer.TradeInfo

/**
 * Stores data about trades. Key - label as it is presented in datasets of [ChartActivity.chart].
 * Value - list of [TradeData]
 */
class TradesMap(private val startTradeTime: Long) : HashMap<String, ArrayList<TradeData>>() {
    fun add(tradeInfoList: List<TradeInfo>) {
        if (tradeInfoList.isEmpty())
            return
        val firstItem = tradeInfoList[0]
        val label = "${firstItem.symbol} ${firstItem.exchange}"
        this[label] = ArrayList(tradeInfoList.map {
            TradeData(it.tradeTimeMillis - startTradeTime, it.price)
        })
    }

    fun add(tradeInfo: TradeInfo) {
        val label = "${tradeInfo.symbol} ${tradeInfo.exchange}"
        val tradeData =
            TradeData(tradeInfo.tradeTimeMillis - startTradeTime, tradeInfo.price)
        val list = this[label]
        if (list != null) list.add(tradeData)
        else this[label] = ArrayList<TradeData>().apply{ add(tradeData) }
    }
}