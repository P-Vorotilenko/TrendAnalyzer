package vorotilenko.trendanalyzer.activities.select

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedListItem
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity
import vorotilenko.trendanalyzer.activities.select.currency.SelectFirstCurrencyActivity

/**
 * Contract between
 * [SelectExchangeActivity][vorotilenko.trendanalyzer.activities.select.exchange.SelectExchangeActivity]
 * and [SelectFirstCurrencyActivity]
 */
class ExchangeToCurrencyContract : ActivityResultContract<ObservedListItem?, ObservedListItem?>() {
    override fun createIntent(context: Context, input: ObservedListItem?): Intent {
        return Intent(context, SelectFirstCurrencyActivity::class.java)
            .putExtra(ObservedSymbolsActivity.NEW_ITEM, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ObservedListItem? = when {
        resultCode != RESULT_OK -> null
        else -> intent?.getParcelableExtra(ObservedSymbolsActivity.NEW_ITEM)
    }

    override fun getSynchronousResult(
        context: Context,
        input: ObservedListItem?
    ): SynchronousResult<ObservedListItem?>? {
        return if (input?.exchangeName == null || input.exchangeLogo == 0)
            SynchronousResult(null)
        else null
    }
}