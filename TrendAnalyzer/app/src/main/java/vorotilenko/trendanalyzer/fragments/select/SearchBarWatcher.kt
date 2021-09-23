package vorotilenko.trendanalyzer.fragments.select

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.fragments.select.exchange.SelectExchangeFragment
import vorotilenko.trendanalyzer.fragments.select.currency.first.SelectFirstCurrencyFragment
import vorotilenko.trendanalyzer.fragments.select.currency.second.SelectSecondCurrencyFragment

/**
 * Listener for search bar in [SelectExchangeFragment], [SelectFirstCurrencyFragment] and
 * [SelectSecondCurrencyFragment].
 */
class SearchBarWatcher<T : ListItem>(
    private val shownListItems: MutableList<T>,
    private val allListItems: Array<T>,
    private val adapter: RecyclerView.Adapter<*>
) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    @SuppressLint("NotifyDataSetChanged")
    override fun afterTextChanged(s: Editable?) {
        val searched = s.toString()
        shownListItems.clear()
        if (searched == "")
            shownListItems.addAll(allListItems)
        else {
            allListItems.forEach {
                if (it.name.lowercase().startsWith(searched.lowercase().trim()))
                    shownListItems.add(it)
            }
        }
        adapter.notifyDataSetChanged()
    }
}