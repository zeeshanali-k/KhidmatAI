package com.corestack.khidmatai.ui.notifications

import androidx.compose.runtime.Composable

enum class NotificationPermissionStatus { GRANTED, DENIED, UNKNOWN }

data class NotificationPermissionState(
    val status: NotificationPermissionStatus,
    val launchRequest: () -> Unit
)

@Composable
expect fun rememberNotificationPermissionState(
    onPermissionResult: (NotificationPermissionStatus) -> Unit = {}
): NotificationPermissionState
