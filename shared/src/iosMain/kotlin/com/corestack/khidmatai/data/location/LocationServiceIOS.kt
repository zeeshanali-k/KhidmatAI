@file:OptIn(ExperimentalForeignApi::class)

package com.corestack.khidmatai.data.location

import com.corestack.khidmatai.domain.location.LocationService
import com.corestack.khidmatai.core.domain.model.LocationAddress
import com.corestack.khidmatai.core.domain.model.LocationFetchResult
import com.corestack.khidmatai.core.domain.model.LocationPermissionStatus
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.annotation.Single
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.CoreLocation.kCLLocationAccuracyHundredMeters
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

@Single(binds = [LocationService::class])
class LocationServiceIOS(
    private val geocoder: NominatimGeocoder
) : LocationService {

    override suspend fun checkPermission(): LocationPermissionStatus =
        when (CLLocationManager.authorizationStatus()) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> LocationPermissionStatus.GRANTED
            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> LocationPermissionStatus.DENIED
            else -> LocationPermissionStatus.UNKNOWN
        }

    override suspend fun fetchCurrentLocation(): LocationFetchResult {
        if (checkPermission() != LocationPermissionStatus.GRANTED)
            return LocationFetchResult.PermissionDenied

        val coords = suspendCancellableCoroutine<Pair<Double, Double>?> { cont ->
            val manager = CLLocationManager()
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                    val loc = didUpdateLocations.lastOrNull() as? CLLocation
                    manager.delegate = null
                    cont.resume(loc?.coordinate?.let { Pair(it.useContents { latitude }, it.useContents { longitude }) })
                }

                override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                    manager.delegate = null
                    cont.resume(null)
                }
            }
            manager.delegate = delegate
            manager.desiredAccuracy = kCLLocationAccuracyHundredMeters
            manager.requestLocation()
            cont.invokeOnCancellation { manager.delegate = null }
        } ?: return LocationFetchResult.Error("Location unavailable")

        return try {
            val name = geocoder.reverseGeocode(coords.first, coords.second)
            LocationFetchResult.Success(LocationAddress(coords.first, coords.second, name))
        } catch (e: Exception) {
            LocationFetchResult.Error(e.message ?: "Geocoding failed")
        }
    }
}
