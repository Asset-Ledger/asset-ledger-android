package asset.ledger.asset_ledger_android.retrofit.category

import asset.ledger.asset_ledger_android.retrofit.category.dto.RequestUseCategoryDto
import asset.ledger.asset_ledger_android.retrofit.category.dto.ResponseUseCategoryListDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UseCategoryApiService {
    @GET("/use-category")
    suspend fun getUseCategories(
        @Header("user-id") userId : String,
    ) : Response<ResponseUseCategoryListDto>

    @POST("/use-category")
    suspend fun createUseCategory(
        @Header("user-id") userId : String,
        @Body requestUseCategoryDto: RequestUseCategoryDto,
    ) : Response<Void>
}