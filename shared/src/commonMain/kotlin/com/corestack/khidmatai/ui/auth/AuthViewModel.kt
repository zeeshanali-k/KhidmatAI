package com.corestack.khidmatai.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corestack.khidmatai.domain.model.AuthResult
import com.corestack.khidmatai.domain.model.AuthState
import com.corestack.khidmatai.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onAction(action: AuthIntent) {
        when (action) {
            is AuthIntent.Login -> login(action.email, action.password)
            is AuthIntent.Register -> register(action.name, action.email, action.password)
            AuthIntent.Reset -> _uiState.update { it.copy(authState = AuthState.Idle) }
        }
    }

    private fun login(email: String, password: String) {
        _uiState.update { it.copy(authState = AuthState.Loading) }
        viewModelScope.launch {
            repository.login(email, password).collect { result ->
                when (result) {
                    is AuthResult.Success -> _uiState.update { it.copy(authState = AuthState.Success(result.user)) }
                    is AuthResult.Error -> _uiState.update { it.copy(authState = AuthState.Error(result.message)) }
                }
            }
        }
    }

    private fun register(name: String, email: String, password: String) {
        _uiState.update { it.copy(authState = AuthState.Loading) }
        viewModelScope.launch {
            repository.register(name, email, password).collect { result ->
                when (result) {
                    is AuthResult.Success -> _uiState.update { it.copy(authState = AuthState.Success(result.user)) }
                    is AuthResult.Error -> _uiState.update { it.copy(authState = AuthState.Error(result.message)) }
                }
            }
        }
    }
}
