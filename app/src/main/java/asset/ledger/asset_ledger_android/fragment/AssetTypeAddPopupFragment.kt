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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class AssetTypeAddPopupFragment : DialogFragment() {

    private lateinit var assetTypeNameEditText: EditText
    private lateinit var assetTypeAmountEditText: EditText
    private lateinit var assetTypeConfirmButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_popup, container, false)

        assetTypeNameEditText = view.findViewById(R.id.fragment_add_popup_name_edit_text)
        assetTypeAmountEditText = view.findViewById(R.id.fragment_add_popup_amount_edit_text)
        assetTypeConfirmButton = view.findViewById(R.id.fragment_add_popup_confirm_button)

        var userId : String = "user1"

        setAssetTypeConfirmButtonClickListener(userId)

        return view
    }

    private fun setAssetTypeConfirmButtonClickListener(userId: String) {
        assetTypeConfirmButton.setOnClickListener {
            fetchCreateAsset(userId)
        }
    }

    private fun fetchCreateAsset(userId: String) {
        if (assetTypeNameEditText == null || assetTypeNameEditText.text.toString() == "") {
            Toast.makeText(requireContext(), "자산 종류를 입력해주세요.", Toast.LENGTH_LONG).show()
            return
        }

        if (assetTypeAmountEditText == null || assetTypeAmountEditText.text.toString() == "") {
            Toast.makeText(requireContext(), "현재 금액을 입력해주세요.", Toast.LENGTH_LONG).show()
            return
        }

        if (assetTypeAmountEditText.text.toString().toIntOrNull() == null) {
            Toast.makeText(requireContext(), "금액은 숫자만 입력가능합니다.", Toast.LENGTH_LONG).show()
            return
        }

        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleCreateAssetApiResponse(
                userId,
                assetTypeNameEditText.text.toString(),
                assetTypeAmountEditText.text.toString().toInt()
            )
        }
    }

    private suspend fun handleCreateAssetApiResponse(userId : String, assetType: String, currentAmount: Int) {
        try {
            val requestAssetDto = RequestAssetDto(assetType, currentAmount)
            val response = createAsset(userId, requestAssetDto)

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
                    Toast.makeText(requireContext(), "자산 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "자산 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun createAsset(userId: String, requestAssetDto: RequestAssetDto): Response<Void> {
        return withContext(Dispatchers.IO) {
            AssetApiInstance.assetApiService.createAsset(userId, requestAssetDto)
        }
    }
}