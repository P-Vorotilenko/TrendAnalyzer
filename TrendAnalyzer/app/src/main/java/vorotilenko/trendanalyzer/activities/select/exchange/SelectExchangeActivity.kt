package vorotilenko.trendanalyzer.activities.select.exchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.activities.ObservedSymbol
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity
import vorotilenko.trendanalyzer.activities.select.ExchangeToCurrencyContract
import vorotilenko.trendanalyzer.activities.select.SearchBarWatcher

class SelectExchangeActivity : AppCompatActivity() {
    /**
     * Exchanges which are shown in the list
     */
    private val shownExchanges = allExchanges.toMutableList()

    /**
     * Launcher for
     * [SelectFirstCurrencyActivity][vorotilenko.trendanalyzer.activities.select.currency.SelectFirstCurrencyActivity]
     */
    private val currencyActivityLauncher =
        registerForActivityResult(ExchangeToCurrencyContract()) { result ->
            if (result != null) {
                setResult(RESULT_OK, Intent().putExtra(ObservedSymbolsActivity.NEW_ITEM, result))
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_exchange)

        val rvExchangesList = findViewById<RecyclerView>(R.id.rvExchangesList)
        val adapter = ExchangesAdapter(applicationContext, shownExchanges) { exchange, _ ->
            currencyActivityLauncher.launch(
                ObservedSymbol().apply {
                    exchangeName = exchange.name
                    exchangeLogo = exchange.logoRes
                })
        }
        rvExchangesList.adapter = adapter

        val etExchangeName = findViewById<EditText>(R.id.etExchangeName)
        etExchangeName.addTextChangedListener(
            SearchBarWatcher(shownExchanges, allExchanges, adapter)
        )

        setSupportActionBar(findViewById(R.id.select_exchange_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val allExchanges = arrayOf(
            ExchangesListItem("Binance", R.mipmap.binance_logo),
            ExchangesListItem("Huobi", R.mipmap.huobi_logo)
        )
    }
}