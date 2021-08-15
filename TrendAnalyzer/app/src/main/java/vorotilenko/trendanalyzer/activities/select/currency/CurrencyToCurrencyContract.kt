package vorotilenko.trendanalyzer.activities.select.currency

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import vorotilenko.trendanalyzer.activities.ObservedSymbol
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity

/**
 * Contract used when [SelectFirstCurrencyActivity] starts [SelectSecondCurrencyActivity]
 */
class CurrencyToCurrencyContract : ActivityResultContract<ObservedSymbol?, ObservedSymbol?>() {
    override fun createIntent(context: Context, input: ObservedSymbol?): Intent {
        return Intent(context, SelectSecondCurrencyActivity::class.java)
            .putExtra(ObservedSymbolsActivity.NEW_ITEM, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): ObservedSymbol? = when {
        resultCode != Activity.RESULT_OK -> null
        else -> intent?.getParcelableExtra(ObservedSymbolsActivity.NEW_ITEM)
    }

    override fun getSynchronousResult(
        context: Context,
        input: ObservedSymbol?
    ): SynchronousResult<ObservedSymbol?>? {
        return if (input?.exchangeName == null || input.exchangeLogo == 0 ||
            input.symbolTicker == null || input.symbolName == null || input.currency1Logo == 0
        ) SynchronousResult(null)
        else null
    }
}