package vorotilenko.trendanalyzer.activities.select

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher

/**
 * Listener for search bars in [SelectExchangeActivity] and [SelectCurrencyActivity]
 */
class SearchBarWatcher(
    private val shownListItems: MutableList<ListItem>,
    private val allListItems: List<ListItem>,
    private val adapter: RVAdapter
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