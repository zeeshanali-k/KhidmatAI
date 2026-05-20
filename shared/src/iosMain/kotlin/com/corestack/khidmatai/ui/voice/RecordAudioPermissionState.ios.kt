package com.corestack.khidmatai.ui.voice

import androidx.compose.runtime.Composable

@Composable
actual fun rememberRecordAudioPermissionState(
    onPermissionResult: (RecordAudioPermissionStatus) -> Unit
): RecordAudioPermissionState {
    return RecordAudioPermissionState(
        status = RecordAudioPermissionStatus.GRANTED,
        launchRequest = {}
    )
}
