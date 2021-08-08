package vorotilenko.trendanalyzer.activities.observedsymbols

import android.os.Parcel
import android.os.Parcelable

class ObservedListItem() : Parcelable {

    var exchangeName: String? = null

    var exchangeLogo: Int = 0

    var symbol: String? = null

    var currency1Logo: Int = 0

    var currency2Logo: Int = 0

    constructor(parcel: Parcel) : this() {
        exchangeName = parcel.readString()
        exchangeLogo = parcel.readInt()
        symbol = parcel.readString()
        currency1Logo = parcel.readInt()
        currency2Logo = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(exchangeName)
        parcel.writeInt(exchangeLogo)
        parcel.writeString(symbol)
        parcel.writeInt(currency1Logo)
        parcel.writeInt(currency2Logo)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ObservedListItem> {
        override fun createFromParcel(parcel: Parcel): ObservedListItem {
            return ObservedListItem(parcel)
        }

        override fun newArray(size: Int): Array<ObservedListItem?> {
            return arrayOfNulls(size)
        }
    }
}