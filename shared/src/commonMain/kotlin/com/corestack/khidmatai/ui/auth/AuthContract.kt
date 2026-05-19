package com.corestack.khidmatai.ui.auth

import com.corestack.khidmatai.core.domain.model.AuthState

sealed class AuthIntent {
    data class Login(val email: String, val password: String) : AuthIntent()
    data class Register(val name: String, val email: String, val password: String) : AuthIntent()
    data object Reset : AuthIntent()
}

data class AuthUiState(
    val authState: AuthState = AuthState.Idle
)
