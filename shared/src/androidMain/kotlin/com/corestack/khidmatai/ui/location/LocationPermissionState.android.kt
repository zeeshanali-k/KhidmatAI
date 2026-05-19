package com.corestack.khidmatai.ui.location

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
import com.corestack.khidmatai.domain.model.LocationPermissionStatus

@Composable
actual fun rememberLocationPermissionState(
    onPermissionResult: (LocationPermissionStatus) -> Unit
): LocationPermissionState {
    val context = LocalContext.current
    var status by remember {
        mutableStateOf(
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
            ) LocationPermissionStatus.GRANTED else LocationPermissionStatus.UNKNOWN
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val newStatus = if (granted) LocationPermissionStatus.GRANTED else LocationPermissionStatus.DENIED
        status = newStatus
        onPermissionResult(newStatus)
    }
    return LocationPermissionState(
        status = status,
        launchRequest = {
            launcher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    )
}
