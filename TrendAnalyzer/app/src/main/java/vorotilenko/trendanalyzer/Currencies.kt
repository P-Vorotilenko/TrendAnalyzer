package vorotilenko.trendanalyzer

/**
 * Contains all names and tickers of any currencies used in the app, information about
 * the available currency pairs, functions that help to transform the currency names to tickers etc.
 */
object Currencies {

    const val BITCOIN = "Bitcoin"
    const val ETHEREUM = "Ethereum"
    const val CARDANO = "Cardano"
    const val XRP = "XRP"
    const val POLKADOT = "Polkadot"
    const val UNISWAP = "Uniswap"
    const val BITCOIN_CASH = "Bitcoin Cash"
    const val LITECOIN = "Litecoin"
    const val SOLANA = "Solana"
    const val CHAINLINK = "Chainlink"
    const val TETHER = "Tether"

    const val BTC = "BTC"
    const val ETH = "ETH"
    const val ADA = "ADA"
    const val DOT = "DOT"
    const val UNI = "UNI"
    const val BCH = "BCH"
    const val LTC = "LTC"
    const val SOL = "SOL"
    const val LINK = "LINK"
    const val USDT = "USDT"

    /**
     * Symbols available to observe. This structure represents which symbols are observed
     * by the server. To each exchange name there is corresponding map which contains
     * first currency as a key and a set of currencies which can create a pair with the first
     * currency.
     */
    val availableSymbols = mapOf(
        ExchangeNames.BINANCE to mapOf(
            BTC to setOf(USDT),
            ETH to setOf(BTC, USDT),
            ADA to setOf(BTC, USDT, ETH),
            XRP to setOf(BTC, USDT, ETH),
            DOT to setOf(BTC, USDT),
            UNI to setOf(BTC, USDT),
            BCH to setOf(BTC, USDT),
            LTC to setOf(BTC, USDT, ETH),
            SOL to setOf(BTC, USDT),
            LINK to setOf(BTC, USDT, ETH)
        ),
        ExchangeNames.HUOBI to mapOf(
            BTC to setOf(USDT),
            ETH to setOf(BTC, USDT),
            ADA to setOf(BTC, USDT, ETH),
            XRP to setOf(BTC, USDT),
            DOT to setOf(BTC, USDT),
            UNI to setOf(BTC, USDT, ETH),
            BCH to setOf(BTC, USDT),
            LTC to setOf(BTC, USDT),
            SOL to setOf(BTC, USDT, ETH),
            LINK to setOf(BTC, USDT, ETH)
        )
    )

    /**
     * @param currency Full name of the currency specified in [Currencies] e.g. "Bitcoin".
     * @return Ticker of the currency e.g. "BTC" or null if the currency name is not
     * specified in [Currencies].
     */
    fun getTicker(currency: String?) = when (currency) {
        BITCOIN -> BTC
        ETHEREUM -> ETH
        CARDANO -> ADA
        XRP -> XRP
        POLKADOT -> DOT
        UNISWAP -> UNI
        BITCOIN_CASH -> BCH
        LITECOIN -> LTC
        SOLANA -> SOL
        CHAINLINK -> LINK
        TETHER -> USDT
        else -> null
    }

    /**
     * @param symbol Symbol name in format "Currency1 \ Currency2" e.g. "Bitcoin \ Ethereum".
     * @return Array of two currency tickers.
     */
    fun getTickers(symbol: String?): Array<String>? {
        val currencies = symbol?.split(" \\ ")
        if (currencies?.size != 2) return null
        val ticker1 = getTicker(currencies[0]) ?: return null
        val ticker2 = getTicker(currencies[1]) ?: return null
        return arrayOf(ticker1, ticker2)
    }

    /**
     * @param currency1 Full name of first currency specified in [Currencies] e.g. "Bitcoin".
     * @param currency2 Full name of second currency specified in [Currencies] e.g. "Ethereum".
     * @return Symbol for sending to server e.g. "BTCETH" or null if one or both of the params
     * are not one of the specified [Currencies].
     */
    fun getSymbol(currency1: String?, currency2: String?): String? {
        val symbol = StringBuilder(8)

        val ticker1 = getTicker(currency1)
        if (ticker1 != null) symbol.append(ticker1)
        else return null

        val ticker2 = getTicker(currency2)
        return if (ticker2 != null) symbol.append(ticker2).toString()
        else null
    }
}