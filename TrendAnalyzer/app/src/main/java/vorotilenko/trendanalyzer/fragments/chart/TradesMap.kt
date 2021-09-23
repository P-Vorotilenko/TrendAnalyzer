package vorotilenko.trendanalyzer.fragments.chart

import android.os.Parcel
import android.os.Parcelable
import com.github.mikephil.charting.data.Entry
import vorotilenko.trendanalyzer.TradeInfo

/**
 * Stores data about trades. Key - label as it is presented in datasets of [ChartFragment.chart].
 * Value - list of [TradeData].
 */
class TradesMap(private val startTradeTime: Long) : HashMap<String, ArrayList<TradeData>>(),
    Parcelable {

    constructor(parcel: Parcel) : this(parcel.readLong()) {
        val size = parcel.readInt()
        for (i in 1..size) {
            val label = parcel.readString()
            val listSize = parcel.readInt()
            val list = ArrayList<TradeData>(listSize)
            parcel.readTypedList(list, TradeData.CREATOR)
            label?.let {
                this[label] = list
            }
        }
    }

    fun add(tradeInfoList: List<TradeInfo>, entries: List<Entry>, label: String) {
        val size = tradeInfoList.size
        if (!containsKey(label)) {
            val dataList = ArrayList<TradeData>(size)
            for (i in 0 until size)
                dataList.add(TradeData(entries[i].x.toLong(), tradeInfoList[i].price))
            this[label] = dataList
        } else {
            for (i in 0 until size)
                add(label, entries[i].x.toLong(), tradeInfoList[i].price)
        }
    }

    fun add(label: String, relativeTradeTime: Long, price: Double) {
        val tradeData = TradeData(relativeTradeTime, price)
        val list = this[label]
        if (list != null) {
            if (list.isEmpty() || relativeTradeTime > list.last().tradeTimeMillis)
                list.add(tradeData)
        } else
            this[label] = ArrayList<TradeData>().apply { add(tradeData) }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(startTradeTime)
        parcel.writeInt(size)
        this.forEach {
            parcel.writeString(it.key)
            val list = it.value
            parcel.writeInt(list.size)
            parcel.writeTypedList(list)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<TradesMap> {
        override fun createFromParcel(parcel: Parcel) = TradesMap(parcel)
        override fun newArray(size: Int): Array<TradesMap?> = arrayOfNulls(size)
    }
}