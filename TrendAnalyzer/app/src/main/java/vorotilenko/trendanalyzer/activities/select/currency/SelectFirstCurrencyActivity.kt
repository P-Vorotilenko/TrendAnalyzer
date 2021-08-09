package vorotilenko.trendanalyzer.activities.select.currency

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedListItem
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity.Companion.NEW_ITEM
import vorotilenko.trendanalyzer.activities.select.SearchBarWatcher
import vorotilenko.trendanalyzer.serverinteraction.Currencies

class SelectFirstCurrencyActivity : AppCompatActivity() {
    /**
     * Currencies which are shown in the list
     */
    private val shownCurrencies = allCurrencies.toMutableList()

    private val secondCurrencyLauncher =
        registerForActivityResult(CurrencyToCurrencyContract()) { result ->
            if (result != null) {
                setResult(RESULT_OK, Intent().putExtra(NEW_ITEM, result))
                finish()
            }
        }

    /**
     * Sets adapter to the RecyclerView
     * @return Created adapter
     */
    private fun setRVAdapter(extras: Bundle?): CurrenciesAdapter {
        val rvCurrenciesList = findViewById<RecyclerView>(R.id.rvCurrenciesList)
        val observedItem = extras?.getParcelable<ObservedListItem>(NEW_ITEM)
        val adapter = CurrenciesAdapter(applicationContext, shownCurrencies) { currency, _ ->
            observedItem?.apply {
                symbolName = currency.name
                symbolTicker = Currencies.getTicker(currency.name)
                currency1Logo = currency.logoRes
            }
            secondCurrencyLauncher.launch(observedItem)
        }
        rvCurrenciesList.adapter = adapter
        return adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_first_currency)

        val extras = intent.extras

        title = resources.getString(R.string.select_1st_currency)

        val adapter = setRVAdapter(extras)

        val etCurrencyName = findViewById<EditText>(R.id.etCurrencyName)
        val textChangedListener = SearchBarWatcher(shownCurrencies, allCurrencies, adapter)
        etCurrencyName.addTextChangedListener(textChangedListener)
    }

    companion object {
        val allCurrencies = arrayOf(
            CurrenciesListItem(Currencies.BITCOIN, Currencies.BTC, R.mipmap.bitcoin_logo),
            CurrenciesListItem(Currencies.ETHEREUM, Currencies.ETH, R.mipmap.ethereum_logo),
            CurrenciesListItem(Currencies.CARDANO, Currencies.ADA, R.mipmap.cardano_logo),
            CurrenciesListItem(Currencies.XRP, Currencies.XRP, R.mipmap.xrp_logo),
            CurrenciesListItem(Currencies.POLKADOT, Currencies.DOT, R.mipmap.polkadot_logo),
            CurrenciesListItem(Currencies.UNISWAP, Currencies.UNI, R.mipmap.uniswap_logo),
            CurrenciesListItem(Currencies.BITCOIN_CASH, Currencies.BCH, R.mipmap.bitcoin_cash_logo),
            CurrenciesListItem(Currencies.LITECOIN, Currencies.LTC, R.mipmap.litecoin_logo),
            CurrenciesListItem(Currencies.SOLANA, Currencies.SOL, R.mipmap.solana_logo),
            CurrenciesListItem(Currencies.CHAINLINK, Currencies.LINK, R.mipmap.chainlink_logo),
            CurrenciesListItem(Currencies.TETHER, Currencies.USDT, R.mipmap.tether_logo)
        )
    }
}