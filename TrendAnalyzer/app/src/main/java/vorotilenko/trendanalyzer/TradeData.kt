package vorotilenko.trendanalyzer

/**
 * Stores data about trades including only time and price
 */
data class TradeData(
        /**
         * Trade time in milliseconds
         */
        val tradeTimeMillis: Long,
        /**
         * Price of the trade
         */
        val price: Double
)