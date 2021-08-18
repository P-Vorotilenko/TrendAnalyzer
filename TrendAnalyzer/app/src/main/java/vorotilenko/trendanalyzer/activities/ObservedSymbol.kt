package vorotilenko.trendanalyzer.activities

import android.os.Parcel
import android.os.Parcelable

/**
 * Symbol observed by user. Includes exchange, currencies and some additional info
 */
class ObservedSymbol(
    var exchangeName: String? = null,
    var exchangeLogo: Int = 0,
    var symbolName: String? = null,
    var symbolTicker: String? = null,
    var currency1Logo: Int = 0,
    var currency2Logo: Int = 0,
    var colorOnChart: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this() {
        exchangeName = parcel.readString()
        exchangeLogo = parcel.readInt()
        symbolName = parcel.readString()
        symbolTicker = parcel.readString()
        currency1Logo = parcel.readInt()
        currency2Logo = parcel.readInt()
        colorOnChart = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(exchangeName)
        parcel.writeInt(exchangeLogo)
        parcel.writeString(symbolName)
        parcel.writeString(symbolTicker)
        parcel.writeInt(currency1Logo)
        parcel.writeInt(currency2Logo)
        parcel.writeInt(colorOnChart)
    }

    override fun describeContents() = 0

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

    companion object CREATOR : Parcelable.Creator<ObservedSymbol> {
        override fun createFromParcel(parcel: Parcel): ObservedSymbol {
            return ObservedSymbol(parcel)
        }

        override fun newArray(size: Int): Array<ObservedSymbol?> {
            return arrayOfNulls(size)
        }
    }
}