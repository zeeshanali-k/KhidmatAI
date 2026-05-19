package com.corestack.khidmatai.admin.ui.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.core.domain.model.AdminRequest
import com.corestack.khidmatai.core.domain.model.AdminState
import com.corestack.khidmatai.core.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class RequestsViewModel(private val adminRepository: com.corestack.khidmatai.core.domain.repository.AdminRepository) : ViewModel() {

    private val _listState = MutableStateFlow<com.corestack.khidmatai.core.domain.model.AdminState<List<com.corestack.khidmatai.core.domain.model.AdminRequest>>>(
        _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading)
    val listState: StateFlow<com.corestack.khidmatai.core.domain.model.AdminState<List<com.corestack.khidmatai.core.domain.model.AdminRequest>>> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<com.corestack.khidmatai.core.domain.model.AdminState<com.corestack.khidmatai.core.domain.model.AdminRequest>>(
        _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading)
    val detailState: StateFlow<com.corestack.khidmatai.core.domain.model.AdminState<com.corestack.khidmatai.core.domain.model.AdminRequest>> = _detailState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading
        viewModelScope.launch {
            try {
                _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Success(adminRepository.getAllRequests())
            } catch (e: Exception) {
                _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to load requests")
            }
        }
    }

    fun loadDetail(requestId: String) {
        _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading
        viewModelScope.launch {
            try {
                _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Success(adminRepository.getRequestById(requestId))
            } catch (e: Exception) {
                _detailState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to load request")
            }
        }
    }
}
