package asset.ledger.asset_ledger_android.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import asset.ledger.asset_ledger_android.R
import asset.ledger.asset_ledger_android.retrofit.fcm.FCMApiInstance
import asset.ledger.asset_ledger_android.retrofit.ledger.LedgerApiInstance
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class MainActivitySettingFragment : Fragment() {

    private lateinit var saveFCMDeviceTokenButton : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_activity_setting, container, false)

        saveFCMDeviceTokenButton = view.findViewById(R.id.fragment_setting_save_fcm_device_token_button)

        val userId: String = "user1"

        setSaveFCMDeviceTokenButton(userId)

        return view
    }

    private fun setSaveFCMDeviceTokenButton(userId: String) {
        saveFCMDeviceTokenButton.setOnClickListener {
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(requireContext(), "FCM 토큰 호출에 실패했습니다.", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }
                    val token = task.result
                    Toast.makeText(requireContext(), "FCM 토큰 호출에 성공했습니다. Token = $token", Toast.LENGTH_LONG).show()
                    fetchSaveFCMDeviceToken(userId, token)
                }
        }
    }

    private fun fetchSaveFCMDeviceToken(userId: String, fcmDeviceToken: String) {
        // 비동기 작업을 위한 launch 호출
        lifecycleScope.launch {
            // 비동기 API 호출을 처리
            handleSaveFCMDeviceTokenResponse(userId, fcmDeviceToken)
        }
    }

    private suspend fun handleSaveFCMDeviceTokenResponse(userId: String, fcmDeviceToken: String) {
        try {
            val response = saveFCMDeviceToken(userId, fcmDeviceToken)

            // 응답 본문, 상태 코드, 헤더 처리
            if (response.isSuccessful) {

                // UI 업데이트 (Main 스레드에서 처리)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "FCM Device Token 저장에 성공했습니다.", Toast.LENGTH_LONG).show()
                }
            } else {
                // 오류 처리
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "FCM Device Token 저장 과정에서 서버 오류가 발생했습니다.", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            // 예외 처리
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "FCM Device Token 저장 과정에서 네트워크 오류가 발생했습니다. error=${e.message}", Toast.LENGTH_LONG).show()
                println("123123123 " + e.message)
            }
        }
    }

    private suspend fun saveFCMDeviceToken(userId: String, fcmDeviceToken: String): Response<Void> {
        return withContext(Dispatchers.IO) {
            FCMApiInstance.fcmApiService.saveFCMDeviceToken(userId, fcmDeviceToken)
        }
    }
}