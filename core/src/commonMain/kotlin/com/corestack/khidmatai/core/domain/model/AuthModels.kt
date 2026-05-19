package com.corestack.khidmatai.core.domain.model

data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val token: String
)

sealed class AuthResult {
    data class Success(val user: com.corestack.khidmatai.core.domain.model.AuthUser) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: com.corestack.khidmatai.core.domain.model.AuthUser) : AuthState()
    data class Error(val message: String) : AuthState()
}
