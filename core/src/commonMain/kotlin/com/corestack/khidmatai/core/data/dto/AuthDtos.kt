package com.corestack.khidmatai.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val data: com.corestack.khidmatai.core.data.dto.AuthData?,
    val error: String? = null
)

@Serializable
data class AuthData(
    val id: String,
    val name: String,
    val email: String,
    val token: String
)
