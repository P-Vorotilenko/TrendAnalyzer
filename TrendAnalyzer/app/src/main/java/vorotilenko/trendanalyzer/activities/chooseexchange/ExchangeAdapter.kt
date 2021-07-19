package vorotilenko.trendanalyzer.activities.chooseexchange

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vorotilenko.trendanalyzer.R

class ExchangeAdapter(context: Context, private val exchanges: List<Exchange>) :
    RecyclerView.Adapter<ExchangeAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exchange_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exchange = exchanges[position]
        holder.tvExchangeName.text = exchange.name
        holder.ivLogo.setImageResource(exchange.logoRes)
    }

    override fun getItemCount(): Int {
        return exchanges.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExchangeName = itemView.findViewById<TextView>(R.id.tvExchangeName)
        val ivLogo = itemView.findViewById<ImageView>(R.id.ivLogo)
    }
}