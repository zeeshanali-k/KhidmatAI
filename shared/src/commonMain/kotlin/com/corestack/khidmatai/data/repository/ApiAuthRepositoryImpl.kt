package com.corestack.khidmatai.data.repository

import com.corestack.khidmatai.data.dto.AuthResponse
import com.corestack.khidmatai.data.dto.LoginRequest
import com.corestack.khidmatai.data.dto.RegisterRequest
import com.corestack.khidmatai.domain.model.AuthResult
import com.corestack.khidmatai.domain.model.AuthUser
import com.corestack.khidmatai.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val BASE_URL = "http://10.0.2.2:8000"

class ApiAuthRepositoryImpl(
    private val httpClient: HttpClient
) : AuthRepository {

    override fun login(email: String, password: String): Flow<AuthResult> = flow {
        try {
            val body = LoginRequest(email, password)
            val response: AuthResponse = httpClient.post("$BASE_URL/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body()

            if (response.success && response.data != null) {
                emit(AuthResult.Success(AuthUser(response.data.id, response.data.name, response.data.email, response.data.token)))
            } else {
                emit(AuthResult.Error(response.error ?: "Login failed"))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Network error. Please check your connection."))
        }
    }

    override fun register(name: String, email: String, password: String): Flow<AuthResult> = flow {
        try {
            val body = RegisterRequest(name, email, password)
            val response: AuthResponse = httpClient.post("$BASE_URL/auth/register") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body()

            if (response.success && response.data != null) {
                emit(AuthResult.Success(AuthUser(response.data.id, response.data.name, response.data.email, response.data.token)))
            } else {
                emit(AuthResult.Error(response.error ?: "Registration failed"))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Network error. Please check your connection."))
        }
    }
}
