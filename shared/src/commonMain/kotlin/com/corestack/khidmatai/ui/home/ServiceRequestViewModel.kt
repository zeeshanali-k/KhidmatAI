package com.corestack.khidmatai.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.core.domain.model.RequestState
import com.corestack.khidmatai.core.domain.repository.ServiceRepository
import com.corestack.khidmatai.data.location.LocationPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ServiceRequestViewModel(
    private val repository: ServiceRepository,
    private val locationPreferences: LocationPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ServiceRequestState(location = locationPreferences.detectedLocation)
    )
    val uiState: StateFlow<ServiceRequestState> = _uiState.asStateFlow()

    fun onAction(action: ServiceRequestIntent) {
        when (action) {
            is ServiceRequestIntent.UpdateQuery -> _uiState.update { it.copy(query = action.query) }
            is ServiceRequestIntent.UpdateLocation -> _uiState.update { it.copy(location = action.location) }
            is ServiceRequestIntent.UpdateUrgency -> _uiState.update { it.copy(urgency = action.urgency) }
            is ServiceRequestIntent.UpdateLanguage -> _uiState.update { it.copy(selectedLanguage = action.language) }
            ServiceRequestIntent.SubmitRequest -> submitRequest()
            ServiceRequestIntent.Reset -> _uiState.update { it.copy(requestState = RequestState.Idle) }
        }
    }

    private fun submitRequest() {
        val currentState = _uiState.value
        if (currentState.query.isBlank()) return

        viewModelScope.launch {
            repository.submitRequest(
                query = currentState.query,
                location = currentState.location,
                urgency = currentState.urgency
            ).collect { state ->
                _uiState.update { it.copy(requestState = state) }
            }
        }
    }
}
