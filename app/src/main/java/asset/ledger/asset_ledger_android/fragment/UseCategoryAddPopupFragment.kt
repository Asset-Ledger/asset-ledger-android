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
import asset.ledger.asset_ledger_android.retrofit.category.UseCategoryApiInstance
import asset.ledger.asset_ledger_android.retrofit.category.dto.RequestUseCategoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class UseCategoryAddPopupFragment : DialogFragment() {

    private lateinit var useCategoryNameEditText: EditText
    private lateinit var useCategoryConfirmButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_use_category_popup, container, false)

        useCategoryNameEditText = view.findViewById(R.id.fragment_add_use_category_popup_name_edit_text)
        useCategoryConfirmButton = view.findViewById(R.id.fragment_add_use_category_popup_confirm_button)

        var userId : String = "user1"

        setUseCategoryConfirmButtonClickListener(userId)

        return view
    }

    private fun setUseCategoryConfirmButtonClickListener(userId: String) {
        useCategoryConfirmButton.setOnClickListener {
            fetchCreateAsset(userId)
        }
    }

    private fun fetchCreateAsset(userId: String) {
        if (useCategoryNameEditText == null || useCategoryNameEditText.text.toString() == "") {
            Toast.makeText(requireContext(), "분를 입력해주세요.", Toast.LENGTH_LONG).show()
            return
        }

        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleCreateUseCategoryApiResponse(
                userId,
                useCategoryNameEditText.text.toString()
            )
        }
    }

    private suspend fun handleCreateUseCategoryApiResponse(userId : String, useCategory: String) {
        try {
            val requestUseCategoryDto = RequestUseCategoryDto(useCategory)
            val response = createUseCategory(userId, requestUseCategoryDto)

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
                    Toast.makeText(requireContext(), "분류 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "분류 추가 과정에서 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun createUseCategory(userId: String, requestUseCategoryDto: RequestUseCategoryDto): Response<Void> {
        return withContext(Dispatchers.IO) {
            UseCategoryApiInstance.useCategoryApiService.createUseCategory(userId, requestUseCategoryDto)
        }
    }
}