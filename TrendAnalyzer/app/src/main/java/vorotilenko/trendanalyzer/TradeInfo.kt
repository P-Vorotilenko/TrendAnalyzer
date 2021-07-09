package vorotilenko.trendanalyzer

/**
 * Stores data about trades
 */
data class TradeInfo(
    /**
     * Symbol
     */
    val symbol: String,
    /**
     * Trade time in milliseconds
     */
    val tradeTimeMillis: Long,
    /**
     * Price of the trade
     */
    val price: Double,
    /**
     * Exchange name
     */
    val exchange: String
)
