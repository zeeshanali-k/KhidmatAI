package com.corestack.khidmatai.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.domain.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ServiceRequestViewModel(
    private val repository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceRequestState())
    val uiState: StateFlow<ServiceRequestState> = _uiState.asStateFlow()

    fun handleIntent(intent: ServiceRequestIntent) {
        when (intent) {
            is ServiceRequestIntent.UpdateQuery -> _uiState.update { it.copy(query = intent.query) }
            is ServiceRequestIntent.UpdateLocation -> _uiState.update { it.copy(location = intent.location) }
            is ServiceRequestIntent.UpdateUrgency -> _uiState.update { it.copy(urgency = intent.urgency) }
            is ServiceRequestIntent.UpdateLanguage -> _uiState.update { it.copy(selectedLanguage = intent.language) }
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
