package asset.ledger.asset_ledger_android.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import asset.ledger.asset_ledger_android.R

class PopupRecyclerViewAdapter(
    private val items : List<PopupRecyclerViewItem>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<PopupRecyclerViewAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemTextView : TextView = view.findViewById(R.id.recycler_view_popup_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupRecyclerViewAdapter.ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_fragment_popup_item, parent, false)

        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: PopupRecyclerViewAdapter.ItemViewHolder, position: Int) {
        val item = items[position]
        val itemName = item.itemName

        holder.itemTextView.text = itemName
        holder.itemTextView.setOnClickListener {
            onItemClick(itemName)
        }
    }

}