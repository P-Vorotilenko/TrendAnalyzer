package vorotilenko.trendanalyzer.activities.observedsymbols

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import vorotilenko.trendanalyzer.Constants
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.activities.select.exchange.SelectExchangeActivity

class ObservedSymbolsActivity : AppCompatActivity() {

    private val selectActivitiesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val newItem: ObservedListItem? = result.data?.extras?.getParcelable(NEW_ITEM)
                if (newItem != null) {
                    TODO()
                } else {
                    Toast.makeText(
                        applicationContext,
                        R.string.err_selecting_symbol,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observed_symbols)

        val observedSymbols = intent.extras?.getString(Constants.LISTENED_SYMBOLS)
        if (observedSymbols == null)
            findViewById<TextView>(R.id.tvNothingObserved).visibility = View.VISIBLE
        else {
            //TODO: filling the RecyclerView
        }
    }

    fun onAddSymbolBtnClick(view: View?) {
        val intent = Intent(applicationContext, SelectExchangeActivity::class.java)
        selectActivitiesLauncher.launch(intent)
    }

    companion object {

        /**
         * Key for activity result passed from
         * [SelectExchangeActivity]
         */
        const val NEW_ITEM = "newItem"
    }
}