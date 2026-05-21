package com.corestack.khidmatai.core.domain.notifications

interface PushNotificationService {
    suspend fun getToken(): String?
}
