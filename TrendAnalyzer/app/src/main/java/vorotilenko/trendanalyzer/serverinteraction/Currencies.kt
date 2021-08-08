package vorotilenko.trendanalyzer.serverinteraction

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
     * @param currency Full name of the currency specified in [Currencies] e.g. "Bitcoin"
     * @return Ticker of the currency e.g. "BTC" or null if the currency name is not
     * specified in [Currencies]
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
     * @param currency1 Full name of first currency specified in [Currencies] e.g. "Bitcoin"
     * @param currency2 Full name of second currency specified in [Currencies] e.g. "Ethereum"
     * @return Symbol for sending to server e.g. "BTCETH" or null if one or both of the params
     * are not one of the specified [Currencies]
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