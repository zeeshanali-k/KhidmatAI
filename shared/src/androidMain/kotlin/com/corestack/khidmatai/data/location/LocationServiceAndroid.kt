package com.corestack.khidmatai.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.corestack.khidmatai.domain.location.LocationService
import com.corestack.khidmatai.domain.model.LocationAddress
import com.corestack.khidmatai.domain.model.LocationFetchResult
import com.corestack.khidmatai.domain.model.LocationPermissionStatus
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import kotlin.coroutines.resume

@Single
class LocationServiceAndroid(
    private val context: Context,
    private val geocoder: NominatimGeocoder
) : LocationService {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun checkPermission(): LocationPermissionStatus =
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) LocationPermissionStatus.GRANTED else LocationPermissionStatus.UNKNOWN

    override suspend fun fetchCurrentLocation(): LocationFetchResult {
        if (checkPermission() != LocationPermissionStatus.GRANTED)
            return LocationFetchResult.PermissionDenied

        val coords = suspendCancellableCoroutine<Pair<Double, Double>?> { cont ->
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    cont.resume(location?.let { Pair(it.latitude, it.longitude) })
                }
                .addOnFailureListener { cont.resume(null) }
        } ?: return LocationFetchResult.Error("Location unavailable")

        return try {
            val name = geocoder.reverseGeocode(coords.first, coords.second)
            LocationFetchResult.Success(LocationAddress(coords.first, coords.second, name))
        } catch (e: Exception) {
            LocationFetchResult.Error(e.message ?: "Geocoding failed")
        }
    }
}
