package com.corestack.khidmatai.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.domain.location.LocationService
import com.corestack.khidmatai.domain.model.LocationFetchResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class LocationPickerViewModel(
    private val locationService: LocationService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationPickerState())
    val uiState: StateFlow<LocationPickerState> = _uiState.asStateFlow()

    private val _detectedLocation = MutableSharedFlow<String>(replay = 0)
    val detectedLocation: SharedFlow<String> = _detectedLocation.asSharedFlow()

    fun onAction(action: LocationPickerIntent) {
        when (action) {
            is LocationPickerIntent.UpdateSearch -> applySearch(action.query)
            LocationPickerIntent.DetectLocation -> detectLocation()
            LocationPickerIntent.ClearError -> _uiState.update { it.copy(detectionError = null) }
        }
    }

    private fun applySearch(query: String) {
        val trimmed = query.trim()
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredPopular = if (trimmed.isEmpty()) defaultPopularAreas
                    else defaultPopularAreas.filter { loc -> loc.contains(trimmed, ignoreCase = true) },
                filteredCities = if (trimmed.isEmpty()) defaultMajorCities
                    else defaultMajorCities.filter { loc -> loc.contains(trimmed, ignoreCase = true) },
            )
        }
    }

    private fun detectLocation() {
        if (_uiState.value.isDetecting) return
        _uiState.update { it.copy(isDetecting = true, detectionError = null) }
        viewModelScope.launch {
            when (val result = locationService.fetchCurrentLocation()) {
                is LocationFetchResult.Success -> {
                    _uiState.update { it.copy(isDetecting = false) }
                    _detectedLocation.emit(result.address.displayName)
                }
                is LocationFetchResult.PermissionDenied -> _uiState.update {
                    it.copy(isDetecting = false, detectionError = "Location permission required")
                }
                is LocationFetchResult.Error -> _uiState.update {
                    it.copy(isDetecting = false, detectionError = result.message)
                }
            }
        }
    }
}
