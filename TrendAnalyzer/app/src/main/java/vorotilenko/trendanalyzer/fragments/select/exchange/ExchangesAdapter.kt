package vorotilenko.trendanalyzer.fragments.select.exchange

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R

/**
 * Adapter for RecyclerView in [SelectExchangeFragment].
 */
class ExchangesAdapter(
    context: Context,
    private val listItems: List<ExchangesListItem>,
    private val onItemClick: (listItem: ExchangesListItem, position: Int) -> Unit
) : RecyclerView.Adapter<ExchangesAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exchanges_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exchange = listItems[position]
        holder.tvExchangeName.text = exchange.name
        holder.ivLogo.setImageResource(exchange.logoRes)
        holder.itemView.setOnClickListener {
            onItemClick(exchange, position)
        }
    }

    override fun getItemCount() = listItems.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExchangeName: TextView = itemView.findViewById(R.id.tvExchangeName)
        val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
    }
}