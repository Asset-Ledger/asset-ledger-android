package asset.ledger.asset_ledger_android.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toLowerCase
import androidx.recyclerview.widget.RecyclerView
import asset.ledger.asset_ledger_android.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LedgerRecyclerViewAdapter(
    private val items : List<LedgerRecyclerViewItem>,
    private val onItemLongClick: (LedgerRecyclerViewItem, View) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val TYPE_LEDGER_ITEM = 0
        const val TYPE_DATE_ITEM = 1
    }

    // ViewHolder for item
    class LedgerRecyclerViewLedgerItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val plusMinusTextView : TextView = view.findViewById(R.id.recycler_view_fragment_ledger_ledger_item_plus_minus_text_view)
        val useCategoryTextView : TextView = view.findViewById(R.id.recycler_view_fragment_ledger_ledger_item_content_use_category_text_view)
        val assetAndDetailTypeTextView : TextView = view.findViewById(R.id.recycler_view_fragment_ledger_ledger_item_content_asset_type_text_view)
        val amountTextView : TextView = view.findViewById(R.id.recycler_view_fragment_ledger_ledger_item_content_amount_text_view)
        val createTimeTextView : TextView = view.findViewById(R.id.recycler_view_fragment_ledger_ledger_item_content_create_time_text_view)
    }

    // ViewHolder for date
    class LedgerRecyclerViewDateItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val createDateTextView : TextView = view.findViewById(R.id.recycler_view_fragment_ledger_date_item_date_text_view)
        val createDateKoreanTextView : TextView = view.findViewById(R.id.recycler_view_fragment_ledger_date_item_date_korean_text_view)
        val totalPlusTextView : TextView = view.findViewById(R.id.recycler_view_fragment_ledger_date_item_total_plus_text_view)
        val totalMinusTextView : TextView = view.findViewById(R.id.recycler_view_fragment_ledger_date_item_total_minus_text_view)
    }

    // 아이템 타입에 따라 다른 뷰를 반환
    override fun getItemViewType(position: Int): Int {
        return items[position].itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_LEDGER_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_fragment_ledger_ledger_item, parent, false)
                LedgerRecyclerViewLedgerItemHolder(view)

            }
            TYPE_DATE_ITEM -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_fragment_ledger_date_item, parent, false)
                LedgerRecyclerViewDateItemViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LedgerRecyclerViewLedgerItemHolder -> {
                val ledgerItem = items[position]
                if (ledgerItem.plusMinus.isNotEmpty()) {
                    if (ledgerItem.plusMinus.equals("PLUS") or ledgerItem.plusMinus.equals("plus")) {
                        holder.plusMinusTextView.text = "+"
                        holder.itemView.setBackgroundColor(0xFF1E90FF.toInt())
                    }
                    else if(ledgerItem.plusMinus.equals("MINUS") or ledgerItem.plusMinus.equals("minus")) {
                        holder.plusMinusTextView.text = "-"
                        holder.itemView.setBackgroundColor(0xFFFF5A5A.hashCode())
                    }
                }
                holder.useCategoryTextView.text = ledgerItem.useCategory
                if (ledgerItem.assetDetailType.isNotEmpty()) {
                    holder.assetAndDetailTypeTextView.text = "${ledgerItem.assetType} / ${ledgerItem.assetDetailType}"
                }
                else {
                    holder.assetAndDetailTypeTextView.text = ledgerItem.assetType
                }
                val createdTimeSplit = ledgerItem.createdTime.split(":")
                holder.createTimeTextView.text = "${createdTimeSplit[0]}:${createdTimeSplit[1]}"
                holder.amountTextView.text = "${ledgerItem.amount}원"

                holder.itemView.setOnLongClickListener {
                    onItemLongClick(ledgerItem, holder.itemView)
                    true
                }
            }
            is LedgerRecyclerViewDateItemViewHolder -> {
                val dateItem = items[position]
                holder.createDateTextView.text = getDayFromCreateDate(dateItem.createDate)
                holder.createDateKoreanTextView.text = getDayKoreanFromCreateDate(dateItem.createDate)
                holder.totalPlusTextView.text = "${dateItem.totalPlusAmount}원"
                holder.totalMinusTextView.text = "${dateItem.totalMinusAmount}원"
            }
        }
    }

    private fun getDayFromCreateDate(createDate : String) : String {
        val createDateSplit : List<String> = createDate.split("/")

        if (createDateSplit.size != 3) {
            throw IllegalArgumentException("잘못된 날짜 형식 입니다.")
        }

        return "${createDateSplit[2]}일"
    }

    private fun getDayKoreanFromCreateDate(createDate: String) : String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.KOREA)
        val date: Date = dateFormat.parse(createDate) // 문자열을 Date 객체로 변환

        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        val dayOfWeekKorean = getKoreanDayOfWeek(dayOfWeek)

        return "(${dayOfWeekKorean})"
    }

    private fun getKoreanDayOfWeek(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            Calendar.MONDAY -> "월요일"
            Calendar.TUESDAY -> "화요일"
            Calendar.WEDNESDAY -> "수요일"
            Calendar.THURSDAY -> "목요일"
            Calendar.FRIDAY -> "금요일"
            Calendar.SATURDAY -> "토요일"
            Calendar.SUNDAY -> "일요일"
            else -> "알 수 없는 요일"
        }
    }
}