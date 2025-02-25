package asset.ledger.asset_ledger_android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import asset.ledger.asset_ledger_android.AddLedgerActivity
import asset.ledger.asset_ledger_android.R
import asset.ledger.asset_ledger_android.recyclerview.PopupRecyclerViewAdapter
import asset.ledger.asset_ledger_android.recyclerview.PopupRecyclerViewItem
import asset.ledger.asset_ledger_android.retrofit.category.UseCategoryApiInstance
import asset.ledger.asset_ledger_android.retrofit.category.dto.ResponseUseCategoryDto
import asset.ledger.asset_ledger_android.retrofit.category.dto.ResponseUseCategoryListDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class UseCategoryPopupFragment : DialogFragment() {

    private lateinit var useCategoryRecyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_popup, container, false)

        useCategoryRecyclerView = view.findViewById(R.id.fragment_popup_recycler_view)

        var userId : String = "user1"

        fetchGetUseCategories(userId)

        return view
    }

    private fun setUseCategoryRecyclerView(recyclerViewItems : List<PopupRecyclerViewItem>) {
        useCategoryRecyclerView.layoutManager = GridLayoutManager(context, 4)
        var popupRecyclerViewAdapter : PopupRecyclerViewAdapter = PopupRecyclerViewAdapter(recyclerViewItems) { item ->
            (activity as? AddLedgerActivity)?.updateUseCategoryEditText(item)
            dismiss()
        }

        useCategoryRecyclerView.adapter = popupRecyclerViewAdapter
    }

    private fun fetchGetUseCategories(userId: String) {
        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleGetUseCategoryApiResponse(userId)
        }
    }

    private suspend fun handleGetUseCategoryApiResponse(userId : String) {
        try {
            val response = getUseCategories(userId)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                val responseUseCategoryListDto = response.body() // 응답 본문
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    val responseUseCategoryDtos : List<ResponseUseCategoryDto>? = responseUseCategoryListDto?.useCategoryDtos
                    val useCategories : MutableList<PopupRecyclerViewItem> = mutableListOf()

                    println(responseUseCategoryDtos?.get(0))
                    if (responseUseCategoryDtos != null){
                        responseUseCategoryDtos.forEach { responseUseCategory ->
                            useCategories.add(PopupRecyclerViewItem(responseUseCategory.useCategory))
                        }
                    }

                    setUseCategoryRecyclerView(useCategories)
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "사용 분류를 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }

                setUseCategoryRecyclerView(listOf())
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "사용 분류를 불러오는 과정에서 네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }

            setUseCategoryRecyclerView(listOf())
        }
    }

    private suspend fun getUseCategories(userId: String): Response<ResponseUseCategoryListDto> {
        return withContext(Dispatchers.IO) {
            UseCategoryApiInstance.useCategoryApiService.getUseCategories(userId)
        }
    }
}