package vorotilenko.trendanalyzer.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vorotilenko.trendanalyzer.Constants
import vorotilenko.trendanalyzer.R
import vorotilenko.trendanalyzer.activities.observedsymbols.ObservedSymbolsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val prefs = getSharedPreferences(Constants.LISTENED_SYMBOLS, MODE_PRIVATE)
//        val intent = if (prefs.contains(Constants.LISTENED_SYMBOLS))
//            Intent(applicationContext, ObservedSymbolsActivity::class.java)
//        else
//            Intent(applicationContext, ChartActivity::class.java)
        val intent = Intent(applicationContext, ObservedSymbolsActivity::class.java)
        startActivity(intent)
    }
}