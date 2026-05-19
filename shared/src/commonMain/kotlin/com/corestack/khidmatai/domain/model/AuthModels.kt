package com.corestack.khidmatai.domain.model

data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val token: String
)

sealed class AuthResult {
    data class Success(val user: AuthUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: AuthUser) : AuthState()
    data class Error(val message: String) : AuthState()
}
