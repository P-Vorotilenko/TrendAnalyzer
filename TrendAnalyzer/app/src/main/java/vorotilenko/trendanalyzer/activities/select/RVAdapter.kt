package vorotilenko.trendanalyzer.activities.select

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R

/**
 * Adapter for RecyclerViews in [SelectExchangeActivity] and [SelectCurrencyActivity]
 */
class RVAdapter(
    context: Context,
    private val listItems: List<ListItem>,
    private val onItemClick: (listItem: ListItem, position: Int) -> Unit
) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.select_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvItemName.text = item.name
        holder.ivLogo.setImageResource(item.logoRes)
        holder.itemView.setOnClickListener {
            onItemClick(item, position)
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
    }
}