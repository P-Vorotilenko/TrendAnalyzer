package vorotilenko.trendanalyzer

/**
 * Contains the full names of the exchanges.
 */
object ExchangeNames {

    const val BINANCE = "Binance"
    const val HUOBI = "Huobi"

    /**
     * @return if passed param is one of the exchanges defined in the
     * [ExchangeNames] returns true. Else returns false
     */
    fun isValid(exchangeName: String?) = when (exchangeName) {
        BINANCE, HUOBI -> true
        else -> false
    }
}