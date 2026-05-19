package com.corestack.khidmatai.core.domain.model

data class LocationAddress(
    val latitude: Double,
    val longitude: Double,
    val displayName: String
)

enum class LocationPermissionStatus { GRANTED, DENIED, UNKNOWN }

sealed interface LocationFetchResult {
    data class Success(val address: com.corestack.khidmatai.core.domain.model.LocationAddress) : LocationFetchResult
    data object PermissionDenied : LocationFetchResult
    data class Error(val message: String) : LocationFetchResult
}
