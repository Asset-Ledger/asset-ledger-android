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
import asset.ledger.asset_ledger_android.retrofit.asset.AssetApiInstance
import asset.ledger.asset_ledger_android.retrofit.asset.dto.ResponseAssetDto
import asset.ledger.asset_ledger_android.retrofit.asset.dto.ResponseAssetListDto
import asset.ledger.asset_ledger_android.retrofit.category.UseCategoryApiInstance
import asset.ledger.asset_ledger_android.retrofit.category.dto.ResponseUseCategoryDto
import asset.ledger.asset_ledger_android.retrofit.category.dto.ResponseUseCategoryListDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class AssetTypePopupFragment : DialogFragment() {

    private lateinit var assetTypeRecyclerView : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_popup, container, false)

        assetTypeRecyclerView = view.findViewById(R.id.fragment_popup_recycler_view)

        var userId : String = "user1"

        fetchGetAssets(userId)

        return view
    }

    private fun setAssetTypeRecyclerView(recyclerViewItems : List<PopupRecyclerViewItem>) {
        assetTypeRecyclerView.layoutManager = GridLayoutManager(context, 4)
        var popupRecyclerViewAdapter : PopupRecyclerViewAdapter = PopupRecyclerViewAdapter(recyclerViewItems) { item ->
            (activity as? AddLedgerActivity)?.updateAssetTypeEditText(item)
            dismiss()
        }

        assetTypeRecyclerView.adapter = popupRecyclerViewAdapter
    }

    private fun fetchGetAssets(userId: String) {
        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleGetAssetApiResponse(userId)
        }
    }

    private suspend fun handleGetAssetApiResponse(userId : String) {
        try {
            val response = getAssets(userId)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                val responseAssetListDto = response.body() // 응답 본문
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    val responseAssetDtos : List<ResponseAssetDto>? = responseAssetListDto?.assetDtos
                    val assetTypes : MutableList<PopupRecyclerViewItem> = mutableListOf()

                    if (responseAssetDtos != null){
                        responseAssetDtos.forEach { responseAssetDto ->
                            assetTypes.add(PopupRecyclerViewItem(responseAssetDto.assetType))
                        }
                    }

                    setAssetTypeRecyclerView(assetTypes)
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "자산 종류를 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }

                setAssetTypeRecyclerView(listOf())
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "자산 종류를 불러오는 과정에서 네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }

            setAssetTypeRecyclerView(listOf())
        }
    }

    private suspend fun getAssets(userId: String): Response<ResponseAssetListDto> {
        return withContext(Dispatchers.IO) {
            AssetApiInstance.assetApiService.getAssets(userId)
        }
    }
}