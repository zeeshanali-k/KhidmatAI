package com.corestack.khidmatai.ui.notifications

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberNotificationPermissionState(
    onPermissionResult: (NotificationPermissionStatus) -> Unit
): NotificationPermissionState {
    val context = LocalContext.current

    // On Android < 13 (API 33), notification permission is granted at install time
    val initialStatus = remember {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            NotificationPermissionStatus.GRANTED
        } else if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationPermissionStatus.GRANTED
        } else {
            NotificationPermissionStatus.UNKNOWN
        }
    }

    var status by remember { mutableStateOf(initialStatus) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val newStatus = if (granted) NotificationPermissionStatus.GRANTED else NotificationPermissionStatus.DENIED
        status = newStatus
        onPermissionResult(newStatus)
    }

    return NotificationPermissionState(
        status = status,
        launchRequest = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                // Pre-13: permission is implicit, just report granted
                status = NotificationPermissionStatus.GRANTED
                onPermissionResult(NotificationPermissionStatus.GRANTED)
            }
        }
    )
}
