package vorotilenko.trendanalyzer.activities.select.currency

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R

/**
 * Adapter for RecyclerViews in [SelectFirstCurrencyActivity] and [SelectSecondCurrencyActivity]
 */
class CurrenciesAdapter(
    context: Context,
    private val listItems: List<CurrenciesListItem>,
    private val onItemClick: (listItem: CurrenciesListItem, position: Int) -> Unit
) : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.currencies_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currency = listItems[position]
        holder.tvCurrencyName.text = currency.name
        holder.tvCurrencyTicker.text = currency.ticker
        holder.ivLogo.setImageResource(currency.logoRes)
        holder.itemView.setOnClickListener {
            onItemClick(currency, position)
        }
    }

    override fun getItemCount() = listItems.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCurrencyName: TextView = itemView.findViewById(R.id.tvCurrencyName)
        val tvCurrencyTicker: TextView = itemView.findViewById(R.id.tvCurrencyTicker)
        val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
    }
}