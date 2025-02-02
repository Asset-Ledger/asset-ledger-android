package asset.ledger.asset_ledger_android.recyclerview

data class LedgerRecyclerViewItem(
    // ledger 내용
    val plusMinus : String,
    val useCategory : String,
    val assetType : String,
    val assetDetailType : String,
    val amount : Int,
    val createdTime : String,
    // date 내용
    val createDate : String,
    val totalPlusAmount : Int,
    val totalMinusAmount : Int,
    // 공통
    val itemType : Int
)
