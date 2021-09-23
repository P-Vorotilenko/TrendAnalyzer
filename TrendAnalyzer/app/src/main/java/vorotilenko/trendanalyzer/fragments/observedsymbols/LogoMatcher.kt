package vorotilenko.trendanalyzer.fragments.observedsymbols

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import vorotilenko.trendanalyzer.Currencies
import vorotilenko.trendanalyzer.ExchangeNames
import vorotilenko.trendanalyzer.R

/**
 * Returns bitmaps with logos for currencies and exchanges.
 */
class LogoMatcher(private val context: Context) {

    /**
     * Returns logo for the exchange.
     * @param name Full name of the exchange.
     */
    fun getLogoForExchange(name: String?): Drawable? = when (name) {
        ExchangeNames.BINANCE -> ContextCompat.getDrawable(context, R.mipmap.binance_logo)
        ExchangeNames.HUOBI -> ContextCompat.getDrawable(context, R.mipmap.huobi_logo)
        else -> null
    }

    /**
     * Returns logo for the currency.
     * @param name Full name of the currency.
     */
    fun getLogoForCurrency(name: String?): Drawable? = when (name) {
        Currencies.BITCOIN -> ContextCompat.getDrawable(context, R.mipmap.bitcoin_logo)
        Currencies.ETHEREUM -> ContextCompat.getDrawable(context, R.mipmap.ethereum_logo)
        Currencies.CARDANO -> ContextCompat.getDrawable(context, R.mipmap.cardano_logo)
        Currencies.XRP -> ContextCompat.getDrawable(context, R.mipmap.xrp_logo)
        Currencies.POLKADOT -> ContextCompat.getDrawable(context, R.mipmap.polkadot_logo)
        Currencies.UNISWAP -> ContextCompat.getDrawable(context, R.mipmap.uniswap_logo)
        Currencies.BITCOIN_CASH -> ContextCompat.getDrawable(context, R.mipmap.bitcoin_cash_logo)
        Currencies.LITECOIN -> ContextCompat.getDrawable(context, R.mipmap.litecoin_logo)
        Currencies.SOLANA -> ContextCompat.getDrawable(context, R.mipmap.solana_logo)
        Currencies.CHAINLINK -> ContextCompat.getDrawable(context, R.mipmap.chainlink_logo)
        Currencies.TETHER -> ContextCompat.getDrawable(context, R.mipmap.tether_logo)
        else -> null
    }

    /**
     * Returns array of two drawables with logos for the currency pair.
     * @param pair Name of the currency pair in format "Currency1 \ Currency2".
     */
    fun getLogosForCurrencyPair(pair: String?): Array<Drawable>? {
        val currencyNames = pair?.split(" \\ ")
        if (currencyNames?.size != 2) return null
        val logo1 = getLogoForCurrency(currencyNames[0]) ?: return null
        val logo2 = getLogoForCurrency(currencyNames[1]) ?: return null
        return arrayOf(logo1, logo2)
    }
}