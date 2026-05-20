package com.corestack.khidmatai.ui.voice

import android.Manifest
import android.content.pm.PackageManager
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
actual fun rememberRecordAudioPermissionState(
    onPermissionResult: (RecordAudioPermissionStatus) -> Unit
): RecordAudioPermissionState {
    val context = LocalContext.current
    var status by remember {
        mutableStateOf(
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED
            ) RecordAudioPermissionStatus.GRANTED else RecordAudioPermissionStatus.UNKNOWN
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val newStatus = if (granted) RecordAudioPermissionStatus.GRANTED else RecordAudioPermissionStatus.DENIED
        status = newStatus
        onPermissionResult(newStatus)
    }
    return RecordAudioPermissionState(
        status = status,
        launchRequest = {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    )
}
