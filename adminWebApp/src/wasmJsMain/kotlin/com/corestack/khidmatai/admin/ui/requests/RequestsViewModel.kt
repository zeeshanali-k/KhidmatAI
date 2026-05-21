package com.corestack.khidmatai.admin.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.core.domain.model.AdminRequest
import com.corestack.khidmatai.core.domain.model.AdminState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class RequestsViewModel(private val adminRepository: com.corestack.khidmatai.core.domain.repository.AdminRepository) : ViewModel() {

    private val _listState = MutableStateFlow<AdminState<List<AdminRequest>>>(
        AdminState.Loading)
    val listState: StateFlow<AdminState<List<AdminRequest>>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<AdminState<AdminRequest>>(
        AdminState.Loading)
    val detailState: StateFlow<AdminState<AdminRequest>> = _detailState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        _listState.value = AdminState.Loading
        viewModelScope.launch {
            try {
                _listState.value = AdminState.Success(adminRepository.getAllRequests())
            } catch (e: Exception) {
                _listState.value = AdminState.Error(e.message ?: "Failed to load requests")
            }
        }
    }

    fun loadDetail(requestId: String) {
        _detailState.value = AdminState.Loading
        viewModelScope.launch {
            try {
                _detailState.value = AdminState.Success(adminRepository.getRequestById(requestId))
            } catch (e: Exception) {
                _detailState.value = AdminState.Error(e.message ?: "Failed to load request")
            }
        }
    }
}
