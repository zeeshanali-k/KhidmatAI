package com.corestack.khidmatai.admin.ui.providers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.core.domain.model.AdminProvider
import com.corestack.khidmatai.core.domain.model.AdminState
import com.corestack.khidmatai.core.domain.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ProvidersViewModel(private val adminRepository: com.corestack.khidmatai.core.domain.repository.AdminRepository) : ViewModel() {

    private val _listState = MutableStateFlow<com.corestack.khidmatai.core.domain.model.AdminState<List<com.corestack.khidmatai.core.domain.model.AdminProvider>>>(
        _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading)
    val listState: StateFlow<com.corestack.khidmatai.core.domain.model.AdminState<List<com.corestack.khidmatai.core.domain.model.AdminProvider>>> = _listState.asStateFlow()

    private val _formState = MutableStateFlow<com.corestack.khidmatai.core.domain.model.AdminState<com.corestack.khidmatai.core.domain.model.AdminProvider?>>(
        _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Success(null))
    val formState: StateFlow<com.corestack.khidmatai.core.domain.model.AdminState<com.corestack.khidmatai.core.domain.model.AdminProvider?>> = _formState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading
        viewModelScope.launch {
            try {
                _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Success(adminRepository.getAllProviders())
            } catch (e: Exception) {
                _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to load providers")
            }
        }
    }

    fun loadForEdit(providerId: String) {
        _formState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Loading
        viewModelScope.launch {
            try {
                val all = adminRepository.getAllProviders()
                _formState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Success(all.find { it.id == providerId })
            } catch (e: Exception) {
                _formState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to load provider")
            }
        }
    }

    fun create(provider: com.corestack.khidmatai.core.domain.model.AdminProvider, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                adminRepository.createProvider(provider)
                loadAll()
                onSuccess()
            } catch (e: Exception) {
                _formState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to create provider")
            }
        }
    }

    fun update(providerId: String, provider: com.corestack.khidmatai.core.domain.model.AdminProvider, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                adminRepository.updateProvider(providerId, provider)
                loadAll()
                onSuccess()
            } catch (e: Exception) {
                _formState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to update provider")
            }
        }
    }

    fun delete(providerId: String) {
        viewModelScope.launch {
            try {
                adminRepository.deleteProvider(providerId)
                loadAll()
            } catch (e: Exception) {
                _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to delete provider")
            }
        }
    }

    fun toggleAvailability(providerId: String) {
        viewModelScope.launch {
            try {
                adminRepository.toggleProviderAvailability(providerId)
                loadAll()
            } catch (e: Exception) {
                _listState.value = _root_ide_package_.com.corestack.khidmatai.core.domain.model.AdminState.Error(e.message ?: "Failed to toggle availability")
            }
        }
    }
}
