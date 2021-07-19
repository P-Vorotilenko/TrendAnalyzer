package vorotilenko.trendanalyzer.activities.chooseexchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R
import java.util.*
import kotlin.collections.ArrayList

class ChooseExchangeActivity : AppCompatActivity() {

    private val allExchanges: ArrayList<Exchange> = ArrayList(2)
    init {
        allExchanges.add(Exchange("Binance", R.mipmap.binance_logo))
        allExchanges.add(Exchange("Huobi", R.mipmap.huobi_logo))
    }
    private val shownExchanges = allExchanges.toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_exchange)

        val rvExchangesList = findViewById<RecyclerView>(R.id.rvExchangesList)
        val adapter = ExchangeAdapter(applicationContext, shownExchanges)
        rvExchangesList.adapter = adapter

        val etExchangeName = findViewById<EditText>(R.id.etExchangeName)
        etExchangeName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val searched = s.toString()
                shownExchanges.clear()
                if (searched == "")
                    shownExchanges.addAll(allExchanges)
                else {
                    allExchanges.forEach {
                        if (it.name.lowercase().startsWith(searched.lowercase().trim()))
                            shownExchanges.add(it)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        })
    }
}