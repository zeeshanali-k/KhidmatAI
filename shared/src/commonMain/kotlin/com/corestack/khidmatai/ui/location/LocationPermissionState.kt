package com.corestack.khidmatai.ui.location

import androidx.compose.runtime.Composable
import com.corestack.khidmatai.domain.model.LocationPermissionStatus

data class LocationPermissionState(
    val status: LocationPermissionStatus,
    val launchRequest: () -> Unit
)

@Composable
expect fun rememberLocationPermissionState(
    onPermissionResult: (LocationPermissionStatus) -> Unit = {}
): LocationPermissionState
