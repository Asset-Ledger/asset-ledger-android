package asset.ledger.asset_ledger_android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import asset.ledger.asset_ledger_android.R
import asset.ledger.asset_ledger_android.retrofit.asset.AssetApiInstance
import asset.ledger.asset_ledger_android.retrofit.asset.dto.RequestAssetDto
import asset.ledger.asset_ledger_android.retrofit.assetDetail.AssetDetailApiInstance
import asset.ledger.asset_ledger_android.retrofit.assetDetail.dto.RequestAssetDetailDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class AssetDetailTypeAddPopupFragment : DialogFragment() {

    private lateinit var assetDetailTypeNameEditText: EditText
    private lateinit var assetDetailTypeAmountEditText: EditText
    private lateinit var assetDetailTypeConfirmButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_asset_detail_popup, container, false)

        assetDetailTypeNameEditText = view.findViewById(R.id.fragment_add_asset_detail_popup_name_edit_text)
        assetDetailTypeAmountEditText = view.findViewById(R.id.fragment_add_asset_detail_popup_amount_edit_text)
        assetDetailTypeConfirmButton = view.findViewById(R.id.fragment_add_asset_detail_popup_confirm_button)

        var userId : String = "user1"
        var assetType : String = arguments?.getString("assetType", "") ?: ""
        var connectedAccount : String = ""

        setAssetDetailTypeConfirmButtonClickListener(userId, assetType, connectedAccount)

        return view
    }

    private fun setAssetDetailTypeConfirmButtonClickListener(userId: String, assetType: String, connectedAccount: String) {
        assetDetailTypeConfirmButton.setOnClickListener {
            fetchCreateAssetDetail(userId, assetType, connectedAccount)
        }
    }

    private fun fetchCreateAssetDetail(userId: String, assetType: String, connectedAccount: String) {
        if (assetDetailTypeNameEditText == null || assetDetailTypeNameEditText.text.toString() == "") {
            Toast.makeText(requireContext(), "자산 세부를 입력해주세요.", Toast.LENGTH_LONG).show()
            return
        }

        if (assetDetailTypeAmountEditText == null || assetDetailTypeAmountEditText.text.toString() == "") {
            Toast.makeText(requireContext(), "현재 금액을 입력해주세요.", Toast.LENGTH_LONG).show()
            return
        }

        if (assetDetailTypeAmountEditText.text.toString().toIntOrNull() == null) {
            Toast.makeText(requireContext(), "금액은 숫자만 입력가능합니다.", Toast.LENGTH_LONG).show()
            return
        }

        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleCreateAssetDetailApiResponse(
                userId,
                assetType,
                assetDetailTypeNameEditText.text.toString(),
                connectedAccount,
                assetDetailTypeAmountEditText.text.toString().toInt()
            )
        }
    }

    private suspend fun handleCreateAssetDetailApiResponse(
        userId : String,
        assetType: String,
        assetDetailType: String,
        connectedAccount: String,
        currentAmount: Int
    ) {
        try {
            val requestAssetDetailDto = RequestAssetDetailDto(
                assetType,
                assetDetailType,
                connectedAccount,
                currentAmount
            )
            val response = createAssetDetail(userId, requestAssetDetailDto)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {
                val statusCode = response.code() // 상태 코드
                val headers = response.headers() // 헤더

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    dismiss()
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "자산 세부 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "자산 세부 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun createAssetDetail(userId: String, requestAssetDetailDto: RequestAssetDetailDto): Response<Void> {
        return withContext(Dispatchers.IO) {
            AssetDetailApiInstance.assetDetailApiService.createAssetDetail(userId, requestAssetDetailDto)
        }
    }
}