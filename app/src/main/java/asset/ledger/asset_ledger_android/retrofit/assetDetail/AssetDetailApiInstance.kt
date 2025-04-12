package asset.ledger.asset_ledger_android.retrofit.assetDetail

import asset.ledger.asset_ledger_android.BuildConfig
import asset.ledger.asset_ledger_android.retrofit.asset.AssetApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AssetDetailApiInstance {

    // LOCAL
//    private const val BASE_URL = "http://10.0.2.2:8080"
    // AWS EC2
    private val BASE_URL = BuildConfig.SERVER_IP

    // Retrofit 객체를 생성하여 반환
    val assetDetailApiService : AssetDetailApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 기본 URL 설정
            .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 추가
            .build()
            .create(AssetDetailApiService::class.java) // ApiService 인터페이스 생성
    }

}