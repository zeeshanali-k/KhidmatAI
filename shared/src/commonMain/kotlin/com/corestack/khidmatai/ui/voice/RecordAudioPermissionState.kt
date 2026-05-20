package com.corestack.khidmatai.ui.voice

import androidx.compose.runtime.Composable

enum class RecordAudioPermissionStatus { GRANTED, DENIED, UNKNOWN }

data class RecordAudioPermissionState(
    val status: RecordAudioPermissionStatus,
    val launchRequest: () -> Unit
)

@Composable
expect fun rememberRecordAudioPermissionState(
    onPermissionResult: (RecordAudioPermissionStatus) -> Unit = {}
): RecordAudioPermissionState
