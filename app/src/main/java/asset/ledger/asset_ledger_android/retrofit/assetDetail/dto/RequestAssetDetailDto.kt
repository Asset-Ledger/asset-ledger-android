package asset.ledger.asset_ledger_android.retrofit.assetDetail.dto

data class RequestAssetDetailDto(
    val assetType : String,
    val assetDetailType : String,
    val connectedAccount : String,
    val totalAmount : Int
)
