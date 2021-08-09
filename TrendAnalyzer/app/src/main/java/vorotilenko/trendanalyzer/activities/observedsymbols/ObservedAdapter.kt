package vorotilenko.trendanalyzer.activities.observedsymbols

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import vorotilenko.trendanalyzer.R

/**
 * Adapter for RecyclerView in [ObservedSymbolsActivity]
 */
class ObservedAdapter(
    val context: Context,
    private val listItems: MutableList<ObservedListItem>,
    private val afterItemDelete: ((position: Int, item: ObservedListItem?) -> Unit)? = null,
    private val afterUndoDeletion: ((position: Int, item: ObservedListItem?) -> Unit)? = null
) : RecyclerView.Adapter<ObservedAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * Reference to item deleted by user. In case the user wants to undo deletion
     */
    private var recentlyDeletedItem: ObservedListItem? = null
    /**
     * Position of item deleted by user. In case the user wants to undo deletion
     */
    private var recentlyDeletedItemPosition = -1

    /**
     * RecyclerView which this adapter is attached to
     */
    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.observed_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        holder.ivCurrency1Logo.setImageResource(item.currency1Logo)
        holder.ivCurrency2Logo.setImageResource(item.currency2Logo)
        holder.tvSymbolName.text = item.symbolName
        holder.tvSymbolTicker.text = item.symbolTicker
        holder.tvExchangeName.text = item.exchangeName
        holder.ivExchangeLogo.setImageResource(item.exchangeLogo)
    }

    override fun getItemCount() = listItems.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    /**
     * Shows the snackbar to undo the item deletion
     */
    private fun showUndoSnackbar() {
        if (recyclerView == null) return
        Snackbar.make(recyclerView!!, R.string.symbol_removed, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) {
                recentlyDeletedItem?.let {
                    listItems.add(recentlyDeletedItemPosition, recentlyDeletedItem!!)
                    notifyItemInserted(recentlyDeletedItemPosition)
                    afterUndoDeletion?.invoke(recentlyDeletedItemPosition, recentlyDeletedItem)
                    recentlyDeletedItemPosition = -1
                    recentlyDeletedItem = null
                }
            }
            .show()
    }

    /**
     * Call to delete the item
     */
    fun deleteItem(position: Int) {
        recentlyDeletedItem = listItems[position]
        recentlyDeletedItemPosition = position
        listItems.removeAt(position)
        notifyItemRemoved(position)
        afterItemDelete?.invoke(position, recentlyDeletedItem)
        showUndoSnackbar()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCurrency1Logo: ImageView = itemView.findViewById(R.id.ivCurrency1Logo)
        val ivCurrency2Logo: ImageView = itemView.findViewById(R.id.ivCurrency2Logo)
        val tvSymbolName: TextView = itemView.findViewById(R.id.tvSymbolName)
        val tvSymbolTicker: TextView = itemView.findViewById(R.id.tvSymbolTicker)
        val tvExchangeName: TextView = itemView.findViewById(R.id.tvExchangeName)
        val ivExchangeLogo: ImageView = itemView.findViewById(R.id.ivExchangeLogo)
    }
}