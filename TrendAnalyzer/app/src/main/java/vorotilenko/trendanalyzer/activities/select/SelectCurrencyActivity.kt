package vorotilenko.trendanalyzer.activities.select

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R

class SelectCurrencyActivity : AppCompatActivity() {

    private val allCurrencies: ArrayList<ListItem> = ArrayList(2)
    init {
        //TODO: add currencies
//        allCurrencies.add(ListItem("Binance", R.mipmap.binance_logo))
//        allCurrencies.add(ListItem("Huobi", R.mipmap.huobi_logo))
    }

    /**
     * Exchanges which are shown in the list
     */
    private val shownCurrencies = allCurrencies.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_symbol)

        val currencyNum = intent.extras?.getInt(CURRENCY_NUM, 1)
        title = when (currencyNum) {
            2 -> resources.getString(R.string.select_2nd_currency)
            else -> resources.getString(R.string.select_1st_currency)
        }

        val rvCurrenciesList = findViewById<RecyclerView>(R.id.rvCurrenciesList)
        val adapter = RVAdapter(applicationContext, shownCurrencies) { currency, _ ->
            //TODO: create intent
            //startActivity(intent)
        }
        rvCurrenciesList.adapter = adapter

        val etCurrencyName = findViewById<EditText>(R.id.etCurrencyName)
        etCurrencyName.addTextChangedListener(
            SearchBarWatcher(shownCurrencies, allCurrencies, adapter))
    }

    companion object {
        /**
         * Key for exchange name passed to activity
         */
        const val EXCHANGE_NAME = "exchangeName"

        /**
         * The value on this key indicates whether the activity is called
         * for the first currency or for the second
         */
        const val CURRENCY_NUM = "currencyNum"
    }
}