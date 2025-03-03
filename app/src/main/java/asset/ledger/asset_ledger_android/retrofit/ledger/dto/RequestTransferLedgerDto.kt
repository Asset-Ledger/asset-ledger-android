package asset.ledger.asset_ledger_android.retrofit.ledger.dto

data class RequestTransferLedgerDto(
    val requestOutLedgerDto: RequestLedgerDto,
    val requestInLedgerDto: RequestLedgerDto
)
