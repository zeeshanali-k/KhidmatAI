package com.corestack.khidmatai.ui.location

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.corestack.khidmatai.domain.model.LocationPermissionStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.darwin.NSObject

@Composable
actual fun rememberLocationPermissionState(
    onPermissionResult: (LocationPermissionStatus) -> Unit
): LocationPermissionState {
    var status by remember {
        mutableStateOf(
            when (CLLocationManager.authorizationStatus()) {
                kCLAuthorizationStatusAuthorizedWhenInUse,
                kCLAuthorizationStatusAuthorizedAlways -> LocationPermissionStatus.GRANTED
                kCLAuthorizationStatusDenied,
                kCLAuthorizationStatusRestricted -> LocationPermissionStatus.DENIED
                else -> LocationPermissionStatus.UNKNOWN
            }
        )
    }
    val manager = remember { CLLocationManager() }

    DisposableEffect(Unit) {
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                val newStatus = when (CLLocationManager.authorizationStatus()) {
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> LocationPermissionStatus.GRANTED
                    kCLAuthorizationStatusDenied,
                    kCLAuthorizationStatusRestricted -> LocationPermissionStatus.DENIED
                    else -> LocationPermissionStatus.UNKNOWN
                }
                status = newStatus
                onPermissionResult(newStatus)
            }
        }
        manager.delegate = delegate
        onDispose { manager.delegate = null }
    }

    return LocationPermissionState(
        status = status,
        launchRequest = { manager.requestWhenInUseAuthorization() }
    )
}
