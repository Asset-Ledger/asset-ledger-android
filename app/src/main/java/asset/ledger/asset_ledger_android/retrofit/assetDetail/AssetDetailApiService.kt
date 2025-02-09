package asset.ledger.asset_ledger_android.retrofit.assetDetail

import asset.ledger.asset_ledger_android.retrofit.asset.dto.RequestAssetDto
import asset.ledger.asset_ledger_android.retrofit.asset.dto.ResponseAssetListDto
import asset.ledger.asset_ledger_android.retrofit.assetDetail.dto.RequestAssetDetailDto
import asset.ledger.asset_ledger_android.retrofit.assetDetail.dto.ResponseAssetDetailListDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AssetDetailApiService {
    @GET("/asset-detail")
    suspend fun getAssetDetails(
        @Header("user-id") userId : String,
        @Query("assetType") assetType : String
    ) : Response<ResponseAssetDetailListDto>

    @POST("/asset-detail")
    suspend fun createAssetDetail(
        @Header("user-id") userId : String,
        @Body requestAssetDetailDto: RequestAssetDetailDto
    ) : Response<Void>

}