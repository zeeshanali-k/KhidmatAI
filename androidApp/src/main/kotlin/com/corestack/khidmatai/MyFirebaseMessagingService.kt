package com.corestack.khidmatai

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        try {
            val koin = org.koin.core.context.GlobalContext.get()
            val authRepo = koin.get<com.corestack.khidmatai.core.domain.repository.AuthRepository>()
            if (authRepo.isLoggedIn()) {
                val userId = authRepo.getUserEmail()
                if (userId.isNotBlank()) {
                    kotlinx.coroutines.GlobalScope.launch {
                        authRepo.registerFcmToken(userId, token)
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore if Koin not yet initialized
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        if (data.containsKey("followup_actions")) {
            val followupJson = data["followup_actions"]
            sendNotification(message.notification?.title ?: "Update", message.notification?.body ?: "You have a new update", followupJson)
        } else {
            sendNotification(message.notification?.title ?: "Notification", message.notification?.body ?: "")
        }
    }

    private fun sendNotification(title: String, body: String, followupJson: String? = null) {
        val intent = android.content.Intent(this, MainActivity::class.java).apply {
            addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
            followupJson?.let { putExtra("followup_actions", it) }
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent,
            android.app.PendingIntent.FLAG_ONE_SHOT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val channelId = "KhidmatAI_Channel"
        val builder = androidx.core.app.NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        
        val notificationManager = getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(channelId, "KhidmatAI Updates", android.app.NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, builder.build())
    }
}