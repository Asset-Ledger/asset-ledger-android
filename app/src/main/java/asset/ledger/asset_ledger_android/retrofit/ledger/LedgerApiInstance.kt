package asset.ledger.asset_ledger_android.retrofit.ledger

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LedgerApiInstance {

    private const val BASE_URL = "http://10.0.2.2:8080"

    // Retrofit 객체를 생성하여 반환
    val ledgerApiService : LedgerApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // 기본 URL 설정
            .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 추가
            .build()
            .create(LedgerApiService::class.java) // ApiService 인터페이스 생성
    }

}