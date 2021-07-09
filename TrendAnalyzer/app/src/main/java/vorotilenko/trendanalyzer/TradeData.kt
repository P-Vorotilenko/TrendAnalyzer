package vorotilenko.trendanalyzer

/**
 * Storing trade data for separate trades
 */
data class TradeData(
        /**
         * Trade time in milliseconds
         */
        val tradeTimeMillis: Long,
        /**
         * Trade price
         */
        val price: Double
)