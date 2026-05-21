package com.corestack.khidmatai.android.notifications

import com.corestack.khidmatai.core.domain.notifications.PushNotificationService
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import org.koin.core.annotation.Single

@Single(binds = [PushNotificationService::class])
class AndroidPushNotificationService : PushNotificationService {
    override suspend fun getToken(): String? {
        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            null
        }
    }
}
