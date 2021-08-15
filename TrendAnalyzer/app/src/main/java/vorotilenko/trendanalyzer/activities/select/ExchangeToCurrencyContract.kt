package vorotilenko.trendanalyzer.activities.select

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import vorotilenko.trendanalyzer.activities.ObservedSymbol
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity
import vorotilenko.trendanalyzer.activities.select.currency.SelectFirstCurrencyActivity
import vorotilenko.trendanalyzer.activities.select.exchange.SelectExchangeActivity

/**
 * Contract between [SelectExchangeActivity] and [SelectFirstCurrencyActivity]
 */
class ExchangeToCurrencyContract : ActivityResultContract<ObservedSymbol?, ObservedSymbol?>() {
    override fun createIntent(context: Context, input: ObservedSymbol?): Intent {
        return Intent(context, SelectFirstCurrencyActivity::class.java)
            .putExtra(ObservedSymbolsActivity.NEW_ITEM, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ObservedSymbol? = when {
        resultCode != RESULT_OK -> null
        else -> intent?.getParcelableExtra(ObservedSymbolsActivity.NEW_ITEM)
    }

    override fun getSynchronousResult(
        context: Context,
        input: ObservedSymbol?
    ): SynchronousResult<ObservedSymbol?>? {
        return if (input?.exchangeName == null || input.exchangeLogo == 0)
            SynchronousResult(null)
        else null
    }
}