package com.corestack.khidmatai.ui.location

import androidx.compose.runtime.Immutable

val defaultPopularAreas = listOf(
    "G-13, Islamabad",
    "G-9, Islamabad",
    "G-11, Islamabad",
    "F-6/1, Islamabad",
    "F-7/1, Islamabad",
    "F-8, Islamabad",
    "F-10/1, Islamabad",
    "I-8, Islamabad",
    "I-10, Islamabad",
    "E-7, Islamabad",
    "DHA Phase 1, Islamabad",
    "Bahria Town, Islamabad",
)

val defaultMajorCities = listOf(
    "Rawalpindi",
    "Lahore",
    "Karachi",
    "Peshawar",
    "Quetta",
    "Multan",
    "Faisalabad",
    "Sialkot",
)

@Immutable
data class LocationPickerState(
    val searchQuery: String = "",
    val filteredPopular: List<String> = defaultPopularAreas,
    val filteredCities: List<String> = defaultMajorCities,
    val isDetecting: Boolean = false,
    val detectionError: String? = null,
)

sealed interface LocationPickerIntent {
    data class UpdateSearch(val query: String) : LocationPickerIntent
    data object DetectLocation : LocationPickerIntent
    data object ClearError : LocationPickerIntent
}
