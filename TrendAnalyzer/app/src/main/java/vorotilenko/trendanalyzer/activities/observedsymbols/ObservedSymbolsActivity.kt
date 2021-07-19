package vorotilenko.trendanalyzer.activities.observedsymbols

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import vorotilenko.trendanalyzer.Constants
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.activities.chooseexchange.ChooseExchangeActivity
import vorotilenko.trendanalyzer.activities.chooseexchange.Exchange

class ObservedSymbolsActivity : AppCompatActivity() {

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
        val intent = Intent(applicationContext, ChooseExchangeActivity::class.java)
        startActivity(intent)
    }
}