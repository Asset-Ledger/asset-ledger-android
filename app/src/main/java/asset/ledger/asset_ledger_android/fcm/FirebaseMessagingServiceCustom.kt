package asset.ledger.asset_ledger_android.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import asset.ledger.asset_ledger_android.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessagingServiceCustom : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        message.notification?.let {
            val title = it.title
            val message = it.body
            sendNotification(title, message)
        }
    }

    private fun sendNotification(title: String?, message: String?) {
        // 알림 채널 생성
        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.mipmap.piggy_bank_round) // 아이콘 리소스는 프로젝트에 따라 설정 필요
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat.from(this).notify(123, builder.build())
        }
    }

    private fun createNotificationChannel() {
        // Android 8.0 이상에서 알림 채널을 생성해야 합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Default Channel"
            val descriptionText = "Channel for default notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("default", name, importance).apply {
                description = descriptionText
            }
            // 채널을 시스템에 등록
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}