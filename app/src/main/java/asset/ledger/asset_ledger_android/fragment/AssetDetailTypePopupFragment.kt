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
import asset.ledger.asset_ledger_android.retrofit.assetDetail.AssetDetailApiInstance
import asset.ledger.asset_ledger_android.retrofit.assetDetail.dto.ResponseAssetDetailDto
import asset.ledger.asset_ledger_android.retrofit.assetDetail.dto.ResponseAssetDetailListDto
import asset.ledger.asset_ledger_android.retrofit.category.UseCategoryApiInstance
import asset.ledger.asset_ledger_android.retrofit.category.dto.ResponseUseCategoryDto
import asset.ledger.asset_ledger_android.retrofit.category.dto.ResponseUseCategoryListDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class AssetDetailTypePopupFragment(editTextId: Int) : DialogFragment() {

    private lateinit var assetDetailTypeRecyclerView : RecyclerView
    private val editTextId = editTextId

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_popup, container, false)

        assetDetailTypeRecyclerView = view.findViewById(R.id.fragment_popup_recycler_view)

        var userId : String = "user1"
        var assetType : String = arguments?.getString("assetType", "") ?: ""

        fetchGetAssetDetailTypes(userId, assetType)

        return view
    }

    private fun setAssetTypeRecyclerView(recyclerViewItems : List<PopupRecyclerViewItem>) {
        assetDetailTypeRecyclerView.layoutManager = GridLayoutManager(context, 4)
        var popupRecyclerViewAdapter : PopupRecyclerViewAdapter = PopupRecyclerViewAdapter(recyclerViewItems) { item ->
            if (editTextId == R.id.activity_add_ledger_asset_detail_type_edit_text) {
                (activity as? AddLedgerActivity)?.updateAssetDetailTypeEditText(item)
            }
            else if (editTextId == R.id.activity_add_ledger_out_account_edit_text) {
                (activity as? AddLedgerActivity)?.updateOutAccountEditText(item)
            }
            else if (editTextId == R.id.activity_add_ledger_in_account_edit_text) {
                (activity as? AddLedgerActivity)?.updateInAccountEditText(item)
            }
            dismiss()
        }

        assetDetailTypeRecyclerView.adapter = popupRecyclerViewAdapter
    }

    private fun fetchGetAssetDetailTypes(userId: String, assetType: String) {
        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleGetAssetDetailApiResponse(userId, assetType)
        }
    }

    private suspend fun handleGetAssetDetailApiResponse(userId : String, assetType: String) {
        try {
            val response = getAssetDetails(userId, assetType)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                val responseAssetDetailListDto = response.body() // 응답 본문
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    val responseAssetDetailDtos : List<ResponseAssetDetailDto>? =
                        responseAssetDetailListDto?.assetDetailDtos
                    val assetDetailTypes : MutableList<PopupRecyclerViewItem> = mutableListOf()

                    if (responseAssetDetailDtos != null){
                        responseAssetDetailDtos.forEach { responseAssetDetail ->
                            assetDetailTypes.add(PopupRecyclerViewItem(responseAssetDetail.assetDetailType))
                        }
                    }

                    setAssetTypeRecyclerView(assetDetailTypes)
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "사용 분류를 불러오는 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }

                setAssetTypeRecyclerView(listOf())
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "사용 분류를 불러오는 과정에서 네트워크 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }

            setAssetTypeRecyclerView(listOf())
        }
    }

    private suspend fun getAssetDetails(userId: String, assetType: String): Response<ResponseAssetDetailListDto> {
        return withContext(Dispatchers.IO) {
            AssetDetailApiInstance.assetDetailApiService.getAssetDetails(userId, assetType)
        }
    }

}