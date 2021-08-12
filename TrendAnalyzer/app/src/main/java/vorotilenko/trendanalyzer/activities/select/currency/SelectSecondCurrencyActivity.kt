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
    /**
     * Currencies which are available for this exchange and first currency
     */
    private lateinit var availableCurrencies: Array<CurrenciesListItem>
    /**
     * Currencies which are shown in the list
     */
    private lateinit var shownCurrencies: MutableList<CurrenciesListItem>

    /**
     * Sets adapter to the RecyclerView
     * @return Created adapter
     */
    private fun setRVAdapter(observedItem: ObservedListItem?): CurrenciesAdapter {
        val rvCurrenciesList = findViewById<RecyclerView>(R.id.rvCurrenciesList)
        val adapter = CurrenciesAdapter(applicationContext, shownCurrencies) { currency, _ ->
            observedItem?.apply {
                symbolName += " \\ ${currency.name}"
                symbolTicker += Currencies.getTicker(currency.name)
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

        val observedItem = intent.extras?.getParcelable<ObservedListItem>(NEW_ITEM)
        val exchangeName = observedItem?.exchangeName

        title = resources.getString(R.string.select_2nd_currency)

        availableCurrencies =
            SelectFirstCurrencyActivity.allCurrencies.filter {
                val availableSymbols =
                    Currencies.availableSymbols[exchangeName]?.get(observedItem?.symbolTicker)
                availableSymbols != null && availableSymbols.contains(it.ticker)
            }.toTypedArray()
        shownCurrencies = availableCurrencies.toMutableList()

        val adapter = setRVAdapter(observedItem)

        val etCurrencyName = findViewById<EditText>(R.id.etCurrencyName)
        val textChangedListener = SearchBarWatcher(shownCurrencies, availableCurrencies, adapter)
        etCurrencyName.addTextChangedListener(textChangedListener)
    }
}