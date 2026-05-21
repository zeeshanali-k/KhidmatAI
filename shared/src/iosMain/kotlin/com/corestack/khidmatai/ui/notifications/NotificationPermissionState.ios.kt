package com.corestack.khidmatai.ui.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter

@Composable
actual fun rememberNotificationPermissionState(
    onPermissionResult: (NotificationPermissionStatus) -> Unit
): NotificationPermissionState {
    var status by remember { mutableStateOf(NotificationPermissionStatus.UNKNOWN) }

    // Check current status on first composition
    remember {
        UNUserNotificationCenter.currentNotificationCenter()
            .getNotificationSettingsWithCompletionHandler { settings ->
                if (settings != null) {
                    val newStatus = when (settings.authorizationStatus) {
                        UNAuthorizationStatusAuthorized,
                        UNAuthorizationStatusProvisional -> NotificationPermissionStatus.GRANTED
                        UNAuthorizationStatusDenied -> NotificationPermissionStatus.DENIED
                        else -> NotificationPermissionStatus.UNKNOWN
                    }
                    status = newStatus
                }
            }
        true
    }

    return NotificationPermissionState(
        status = status,
        launchRequest = {
            UNUserNotificationCenter.currentNotificationCenter()
                .requestAuthorizationWithOptions(
                    UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
                ) { granted, _ ->
                    val newStatus = if (granted) {
                        NotificationPermissionStatus.GRANTED
                    } else {
                        NotificationPermissionStatus.DENIED
                    }
                    status = newStatus
                    onPermissionResult(newStatus)
                }
        }
    )
}
