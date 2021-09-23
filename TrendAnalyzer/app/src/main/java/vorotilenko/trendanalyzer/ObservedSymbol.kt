package vorotilenko.trendanalyzer

import android.graphics.drawable.Drawable

/**
 * Symbol observed by user. Includes exchange, currencies and some additional info
 */
data class ObservedSymbol(
    var exchangeName: String? = null,
    var exchangeLogo: Drawable? = null,
    var symbolName: String? = null,
    var symbolTicker: String? = null,
    var currency1Logo: Drawable? = null,
    var currency2Logo: Drawable? = null,
    var color: Int? = null
) {

    override fun equals(other: Any?): Boolean {
        return if (
            other != null &&
            this.exchangeName == (other as ObservedSymbol).exchangeName &&
            this.symbolName == other.symbolName &&
            this.symbolTicker == other.symbolTicker
        ) true
        else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = exchangeName?.hashCode() ?: 0
        result = 31 * result + (symbolName?.hashCode() ?: 0)
        result = 31 * result + (symbolTicker?.hashCode() ?: 0)
        return result
    }
}