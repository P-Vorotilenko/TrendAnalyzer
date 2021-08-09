package vorotilenko.trendanalyzer.activities.observedsymbols

import android.os.Parcel
import android.os.Parcelable

class ObservedListItem(
    var exchangeName: String? = null,
    var exchangeLogo: Int = 0,
    var symbolName: String? = null,
    var symbolTicker: String? = null,
    var currency1Logo: Int = 0,
    var currency2Logo: Int = 0
) : Parcelable {

    constructor(parcel: Parcel) : this() {
        exchangeName = parcel.readString()
        exchangeLogo = parcel.readInt()
        symbolName = parcel.readString()
        symbolTicker = parcel.readString()
        currency1Logo = parcel.readInt()
        currency2Logo = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(exchangeName)
        parcel.writeInt(exchangeLogo)
        parcel.writeString(symbolName)
        parcel.writeString(symbolTicker)
        parcel.writeInt(currency1Logo)
        parcel.writeInt(currency2Logo)
    }

    override fun describeContents() = 0

    override fun equals(other: Any?): Boolean {
        return if (
            other != null &&
            this.exchangeName == (other as ObservedListItem).exchangeName &&
            this.exchangeLogo == other.exchangeLogo &&
            this.symbolName == other.symbolName &&
            this.symbolTicker == other.symbolTicker &&
            this.currency1Logo == other.currency1Logo &&
            this.currency2Logo == other.currency2Logo
        ) true
        else super.equals(other)
    }

    override fun hashCode(): Int {
        var result = exchangeName?.hashCode() ?: 0
        result = 31 * result + exchangeLogo
        result = 31 * result + (symbolName?.hashCode() ?: 0)
        result = 31 * result + (symbolTicker?.hashCode() ?: 0)
        result = 31 * result + currency1Logo
        result = 31 * result + currency2Logo
        return result
    }

    companion object CREATOR : Parcelable.Creator<ObservedListItem> {
        override fun createFromParcel(parcel: Parcel): ObservedListItem {
            return ObservedListItem(parcel)
        }

        override fun newArray(size: Int): Array<ObservedListItem?> {
            return arrayOfNulls(size)
        }
    }
}