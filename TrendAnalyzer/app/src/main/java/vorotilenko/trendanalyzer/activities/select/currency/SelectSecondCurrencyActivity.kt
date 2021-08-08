package vorotilenko.trendanalyzer.activities.select.currency

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedListItem
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity.Companion.NEW_ITEM
import vorotilenko.trendanalyzer.activities.select.SearchBarWatcher
import vorotilenko.trendanalyzer.serverinteraction.Currencies

class SelectSecondCurrencyActivity : AppCompatActivity() {

    private lateinit var allCurrencies: Array<CurrenciesListItem>

    /**
     * Currencies which are shown in the list
     */
    private lateinit var shownCurrencies: MutableList<CurrenciesListItem>

    /**
     * Sets adapter to the RecyclerView
     * @return Created adapter
     */
    private fun setRVAdapter(extras: Bundle?): CurrenciesAdapter {
        val rvCurrenciesList = findViewById<RecyclerView>(R.id.rvCurrenciesList)
        val observedItem = extras?.getParcelable<ObservedListItem>(NEW_ITEM)
        val adapter = CurrenciesAdapter(applicationContext, shownCurrencies) { currency, _ ->
            observedItem?.apply {
                symbol += Currencies.getTicker(currency.name)
                currency2Logo = currency.logoRes
            }
            setResult(RESULT_OK, Intent().putExtra(NEW_ITEM, observedItem))
            finish()
        }
        rvCurrenciesList.adapter = adapter
        return adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_first_currency)

        val extras = intent.extras
        val firstCurrencyTicker = extras?.getParcelable<ObservedListItem?>(NEW_ITEM)?.symbol

        title = resources.getString(R.string.select_2nd_currency)
        allCurrencies =
            SelectFirstCurrencyActivity.allCurrencies.filter {
                Currencies.getTicker(it.name) != firstCurrencyTicker
            }.toTypedArray()
        shownCurrencies = allCurrencies.toMutableList()

        val adapter = setRVAdapter(extras)

        val etCurrencyName = findViewById<EditText>(R.id.etCurrencyName)
        val textChangedListener = SearchBarWatcher(shownCurrencies, allCurrencies, adapter)
        etCurrencyName.addTextChangedListener(textChangedListener)
    }
}