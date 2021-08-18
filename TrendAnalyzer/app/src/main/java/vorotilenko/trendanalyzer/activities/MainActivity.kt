package vorotilenko.trendanalyzer.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vorotilenko.trendanalyzer.Constants
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.activities.chart.ChartActivity
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val observedSymbols = getSharedPreferences(Constants.LISTENED_SYMBOLS, MODE_PRIVATE)
            .getString(Constants.LISTENED_SYMBOLS, "[]")
        val intent = if (observedSymbols != "[]")
            Intent(applicationContext, ChartActivity::class.java)
        else
            Intent(applicationContext, ObservedSymbolsActivity::class.java)
        startActivity(intent)
    }
}