package asset.ledger.asset_ledger_android.retrofit.fcm

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface FCMApiService {
    @POST("/fcm")
    suspend fun saveFCMDeviceToken(
        @Header("user-id") userId : String,
        @Body fcmDeviceToken: String
    ) : Response<Void>
}