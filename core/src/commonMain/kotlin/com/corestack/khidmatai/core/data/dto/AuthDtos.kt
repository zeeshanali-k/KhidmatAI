package com.corestack.khidmatai.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class RegisterResponseDto(
    val message: String? = null,
    val name: String,
    val email: String,
    @SerialName("hashed_password") val hashedPassword: String
)

@Serializable
data class TokenResponseDto(
    val message: String? = null,
    val email: String,
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String
)

@Serializable
data class AuthErrorResponse(
    val detail: String
)

// Retained legacy DTOs for safety
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val data: AuthData?,
    val error: String? = null
)

@Serializable
data class AuthData(
    val id: String,
    val name: String,
    val email: String,
    val token: String
)
