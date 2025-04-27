package asset.ledger.asset_ledger_android.retrofit.asset

import asset.ledger.asset_ledger_android.retrofit.asset.dto.RequestAssetDto
import asset.ledger.asset_ledger_android.retrofit.asset.dto.ResponseAssetListDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AssetApiService {
    @GET("/asset")
    suspend fun getAssets(@Header("user-id") userId : String) : Response<ResponseAssetListDto>

    @POST("/asset")
    suspend fun createAsset(
        @Header("user-id") userId : String,
        @Body requestAssetDto: RequestAssetDto
    ) : Response<Void>
}