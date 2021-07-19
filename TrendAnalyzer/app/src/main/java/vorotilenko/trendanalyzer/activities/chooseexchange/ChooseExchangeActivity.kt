package vorotilenko.trendanalyzer.activities.chooseexchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R

class ChooseExchangeActivity : AppCompatActivity() {

    private val allExchanges: ArrayList<Exchange> = ArrayList(2)
    init {
        allExchanges.add(Exchange("Binance", R.mipmap.binance_logo))
        allExchanges.add(Exchange("Huobi", R.mipmap.huobi_logo))
    }
    private val shownExchanges = allExchanges.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_exchange)

        val rvExchangesList = findViewById<RecyclerView>(R.id.rvExchangesList)
        val adapter = ExchangeAdapter(applicationContext, shownExchanges)
        rvExchangesList.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}