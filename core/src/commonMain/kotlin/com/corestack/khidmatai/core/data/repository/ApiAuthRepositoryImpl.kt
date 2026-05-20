package com.corestack.khidmatai.core.data.repository

import com.corestack.khidmatai.core.data.dto.AuthErrorResponse
import com.corestack.khidmatai.core.data.dto.RegisterRequest
import com.corestack.khidmatai.core.data.dto.TokenResponseDto
import com.corestack.khidmatai.core.domain.model.AuthResult
import com.corestack.khidmatai.core.domain.model.AuthUser
import com.corestack.khidmatai.core.domain.preferences.AppPreferences
import com.corestack.khidmatai.core.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ApiAuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val appPreferences: AppPreferences
) : AuthRepository {

    override fun getLastEmail(): String = appPreferences.lastEmail
    override fun isLoggedIn(): Boolean = appPreferences.isLoggedIn

    override fun logout() {
        appPreferences.clearAuth()
    }

    override fun getUserName(): String = appPreferences.userName

    override fun getUserEmail(): String = appPreferences.lastEmail

    override fun login(email: String, password: String): Flow<AuthResult> = flow {
        try {
            val response: HttpResponse = httpClient.submitForm(
                url = "$BASE_URL/token",
                formParameters = parameters {
                    append("username", email)
                    append("password", password)
                }
            )

            if (response.status.value in 200..299) {
                val tokenResponse = response.body<TokenResponseDto>()
                val parsedName = email.substringBefore("@")
                    .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                
                val user = AuthUser(
                    id = email,
                    name = parsedName,
                    email = email,
                    token = tokenResponse.accessToken
                )
                appPreferences.authToken = user.token
                appPreferences.lastEmail = user.email
                appPreferences.userName = user.name
                emit(AuthResult.Success(user))
            } else {
                val errorMsg = runCatching {
                    response.body<AuthErrorResponse>().detail
                }.getOrElse { "Invalid credentials" }
                emit(AuthResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Network error. Please check your connection."))
        }
    }

    override fun register(name: String, email: String, password: String): Flow<AuthResult> = flow {
        try {
            val response: HttpResponse = httpClient.post("$BASE_URL/register") {
                contentType(ContentType.Application.Json)
                setBody(RegisterRequest(name = name, email = email, password = password))
            }

            if (response.status.value in 200..299) {
                // Sequentially trigger login (fetch JWT token)
                val loginResponse: HttpResponse = httpClient.submitForm(
                    url = "$BASE_URL/token",
                    formParameters = parameters {
                        append("username", email)
                        append("password", password)
                    }
                )

                if (loginResponse.status.value in 200..299) {
                    val tokenResponse = loginResponse.body<TokenResponseDto>()
                    val user = AuthUser(
                        id = email,
                        name = name,
                        email = email,
                        token = tokenResponse.accessToken
                    )
                    appPreferences.authToken = user.token
                    appPreferences.lastEmail = user.email
                    appPreferences.userName = user.name
                    emit(AuthResult.Success(user))
                } else {
                    val errorMsg = runCatching {
                        loginResponse.body<AuthErrorResponse>().detail
                    }.getOrElse { "Registration succeeded, but login failed. Please login manually." }
                    emit(AuthResult.Error(errorMsg))
                }
            } else {
                val errorMsg = runCatching {
                    response.body<AuthErrorResponse>().detail
                }.getOrElse { "Email already exists" }
                emit(AuthResult.Error(errorMsg))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Network error. Please check your connection."))
        }
    }
}
