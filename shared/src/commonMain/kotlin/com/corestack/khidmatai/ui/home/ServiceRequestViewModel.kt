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
    locationPreferences: LocationPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ServiceRequestState(location = locationPreferences.detectedLocation)
    )
    val uiState: StateFlow<ServiceRequestState> = _uiState.asStateFlow()

    private var requestJob: kotlinx.coroutines.Job? = null

    fun onAction(action: ServiceRequestIntent) {
        when (action) {
            is ServiceRequestIntent.UpdateQuery -> _uiState.update { it.copy(query = action.query) }
            is ServiceRequestIntent.UpdateLocation -> _uiState.update { it.copy(location = action.location) }
            is ServiceRequestIntent.UpdateUrgency -> _uiState.update { it.copy(urgency = action.urgency) }
            is ServiceRequestIntent.UpdateLanguage -> _uiState.update { it.copy(selectedLanguage = action.language) }
            ServiceRequestIntent.SubmitRequest -> submitRequest()
            ServiceRequestIntent.CancelRequest -> cancelActiveRequest()
            ServiceRequestIntent.Reset -> _uiState.update { it.copy(requestState = RequestState.Idle, activeRequestId = null) }
            is ServiceRequestIntent.SelectProvider -> selectProvider(action.providerId)
        }
    }

    private fun selectProvider(providerId: String) {
        val current = _uiState.value.requestState
        if (current !is RequestState.AwaitingProviderSelection) return
        val traces = current.traces
        val requestId = current.requestId
        _uiState.update { it.copy(requestState = RequestState.Processing(traces)) }
        viewModelScope.launch {
            repository.selectProvider(requestId, providerId)
        }
    }

    private fun submitRequest() {
        val currentState = _uiState.value
        if (currentState.query.isBlank()) return

        requestJob?.cancel()
        requestJob = viewModelScope.launch {
            repository.submitRequestStream(
                query = currentState.query,
                location = currentState.location,
                urgency = currentState.urgency
            ).collect { state ->
                var reqId: String? = null
                if (state is RequestState.Processing) {
                    reqId = state.traces.firstOrNull { it.requestId != null }?.requestId
                }
                _uiState.update { 
                    it.copy(
                        requestState = state,
                        activeRequestId = reqId ?: it.activeRequestId
                    )
                }
            }
        }
    }

    private fun cancelActiveRequest() {
        requestJob?.cancel()
        requestJob = null
        val activeId = _uiState.value.activeRequestId
        if (activeId != null) {
            viewModelScope.launch {
                repository.cancelRequest(activeId)
            }
        }
        _uiState.update { 
            it.copy(
                requestState = RequestState.Idle,
                activeRequestId = null
            )
        }
    }
}
