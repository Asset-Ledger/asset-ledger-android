package asset.ledger.asset_ledger_android.retrofit.ledger

import asset.ledger.asset_ledger_android.retrofit.ledger.dto.ResponseLedgerListDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface LedgerApiService {
    @GET("/ledger")
    suspend fun getLedgers(
        @Header("user-id") userId : String,
        @Query("searchYearMonth") searchYearMonth : String,
        @Query("startDate") startDate : String,
        @Query("plusMinusType") plusMinusType : String,
        @Query("useCategory") useCategory : String,
        @Query("assetType") assetType : String,
        @Query("assetTypeDetail") assetTypeDetail : String
    ) : Response<ResponseLedgerListDto>
}