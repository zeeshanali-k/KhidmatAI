package com.corestack.khidmatai.domain.location

import com.corestack.khidmatai.domain.model.LocationFetchResult
import com.corestack.khidmatai.domain.model.LocationPermissionStatus

interface LocationService {
    suspend fun checkPermission(): LocationPermissionStatus
    suspend fun fetchCurrentLocation(): LocationFetchResult
}
