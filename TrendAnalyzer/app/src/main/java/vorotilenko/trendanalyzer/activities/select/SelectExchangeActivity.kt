package vorotilenko.trendanalyzer.activities.select

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R
import kotlin.collections.ArrayList

class SelectExchangeActivity : AppCompatActivity() {

    private val allExchanges: ArrayList<ListItem> = ArrayList(2)
    init {
        allExchanges.add(ListItem("Binance", R.mipmap.binance_logo))
        allExchanges.add(ListItem("Huobi", R.mipmap.huobi_logo))
    }

    /**
     * Exchanges which are shown in the list
     */
    private val shownExchanges = allExchanges.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_exchange)

        title = resources.getString(R.string.select_exchange)

        val rvExchangesList = findViewById<RecyclerView>(R.id.rvExchangesList)
        val adapter = RVAdapter(applicationContext, shownExchanges) { exchange, _ ->
            val intent = Intent(applicationContext, SelectCurrencyActivity::class.java)
                .putExtra(SelectCurrencyActivity.EXCHANGE_NAME, exchange.name)
                .putExtra(SelectCurrencyActivity.CURRENCY_NUM, 1)
            startActivity(intent)
        }
        rvExchangesList.adapter = adapter

        val etExchangeName = findViewById<EditText>(R.id.etExchangeName)
        etExchangeName.addTextChangedListener(
            SearchBarWatcher(shownExchanges, allExchanges, adapter))
    }
}