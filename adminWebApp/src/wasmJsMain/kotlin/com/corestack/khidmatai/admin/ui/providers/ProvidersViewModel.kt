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
class ProvidersViewModel(private val adminRepository: AdminRepository) :
    ViewModel() {

    private val _listState =
        MutableStateFlow<AdminState<List<AdminProvider>>>(
            AdminState.Loading
        )
    val listState: StateFlow<AdminState<List<AdminProvider>>> = _listState.asStateFlow()

    private val _formState =
        MutableStateFlow<AdminState<AdminProvider?>>(
            AdminState.Success(null)
        )
    val formState: StateFlow<AdminState<AdminProvider?>> =
        _formState.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        _listState.value =
            AdminState.Loading
        viewModelScope.launch {
            try {
                _listState.value =
                    AdminState.Success(
                        adminRepository.getAllProviders()
                    )
            } catch (e: Exception) {
                _listState.value =
                    AdminState.Error(
                        e.message ?: "Failed to load providers"
                    )
            }
        }
    }

    fun loadForEdit(providerId: String) {
        _formState.value =
            AdminState.Loading
        viewModelScope.launch {
            try {
                val all = adminRepository.getAllProviders()
                _formState.value =
                    AdminState.Success(
                        all.find { it.id == providerId })
            } catch (e: Exception) {
                _formState.value =
                    AdminState.Error(
                        e.message ?: "Failed to load provider"
                    )
            }
        }
    }

    fun create(
        provider: AdminProvider,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                adminRepository.createProvider(provider)
                loadAll()
                onSuccess()
            } catch (e: Exception) {
                _formState.value =
                    AdminState.Error(
                        e.message ?: "Failed to create provider"
                    )
            }
        }
    }

    fun update(
        providerId: String,
        provider: AdminProvider,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                adminRepository.updateProvider(providerId, provider)
                loadAll()
                onSuccess()
            } catch (e: Exception) {
                _formState.value =
                    AdminState.Error(
                        e.message ?: "Failed to update provider"
                    )
            }
        }
    }

    fun delete(providerId: String) {
        viewModelScope.launch {
            try {
                adminRepository.deleteProvider(providerId)
                loadAll()
            } catch (e: Exception) {
                _listState.value =
                    AdminState.Error(
                        e.message ?: "Failed to delete provider"
                    )
            }
        }
    }

    fun toggleAvailability(providerId: String) {
        viewModelScope.launch {
            try {
                adminRepository.toggleProviderAvailability(providerId)
                loadAll()
            } catch (e: Exception) {
                _listState.value =
                    AdminState.Error(
                        e.message ?: "Failed to toggle availability"
                    )
            }
        }
    }
}
