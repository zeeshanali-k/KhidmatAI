package com.corestack.khidmatai.core.data.repository

import com.corestack.khidmatai.core.domain.preferences.AppPreferences
import com.corestack.khidmatai.core.domain.model.AuthResult
import com.corestack.khidmatai.core.domain.model.AuthUser
import com.corestack.khidmatai.core.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockAuthRepositoryImpl(
    private val appPreferences: AppPreferences
) : AuthRepository {

    override fun getLastEmail(): String = appPreferences.lastEmail
    override fun isLoggedIn(): Boolean = appPreferences.isLoggedIn

    override fun logout() {
        appPreferences.clearAuth()
    }

    override fun getUserName(): String = appPreferences.userName

    override fun getUserEmail(): String = appPreferences.lastEmail

    override suspend fun registerFcmToken(userId: String, fcmToken: String): Boolean {
        return true
    }

    override fun login(email: String, password: String): Flow<AuthResult> = flow {
        delay(1500)
        if (email.isNotBlank() && password.isNotBlank()) {
            val user = AuthUser(
                "u1",
                "Test User",
                email,
                "mock_token_123"
            )
            appPreferences.authToken = user.token
            appPreferences.lastEmail = user.email
            appPreferences.userName = user.name
            emit(AuthResult.Success(user))
        } else {
            emit(AuthResult.Error("Invalid credentials"))
        }
    }

    override fun register(name: String, email: String, password: String): Flow<AuthResult> = flow {
        delay(1500)
        if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            val user = AuthUser(
                "u1",
                name,
                email,
                "mock_token_123"
            )
            appPreferences.authToken = user.token
            appPreferences.lastEmail = user.email
            appPreferences.userName = user.name
            emit(AuthResult.Success(user))
        } else {
            emit(AuthResult.Error("Please fill all fields"))
        }
    }
}
