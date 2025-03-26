package asset.ledger.asset_ledger_android.retrofit.ledger.dto

data class ResponseLedgerDto (
    val id : Long,
    val plusMinusType : String,
    val editDate : String,
    val editTime : String,
    val useCategory :  String,
    val assetType : String,
    val assetTypeDetail : String,
    val description : String,
    val amount : Int
)