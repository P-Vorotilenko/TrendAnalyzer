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

        val prefs = getSharedPreferences(Constants.LISTENED_SYMBOLS, MODE_PRIVATE)
        val listened = prefs.getString(Constants.LISTENED_SYMBOLS, "")
        val intent = if (listened == "")
            Intent(applicationContext, ObservedSymbolsActivity::class.java)
        else {
            Intent(applicationContext, ChartActivity::class.java)
                .putExtra(Constants.LISTENED_SYMBOLS, listened)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}