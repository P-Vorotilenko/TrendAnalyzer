package vorotilenko.trendanalyzer.activities.chart

import android.os.Parcel
import android.os.Parcelable

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(tradeTimeMillis)
        parcel.writeDouble(price)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<TradeData> {
        override fun createFromParcel(parcel: Parcel): TradeData {
            return TradeData(parcel)
        }

        override fun newArray(size: Int): Array<TradeData?> {
            return arrayOfNulls(size)
        }
    }
}
