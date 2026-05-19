package com.corestack.khidmatai.data.repository

import com.corestack.khidmatai.domain.model.AuthResult
import com.corestack.khidmatai.domain.model.AuthUser
import com.corestack.khidmatai.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockAuthRepositoryImpl : AuthRepository {
    override fun login(email: String, password: String): Flow<AuthResult> = flow {
        delay(1500)
        if (email.isNotBlank() && password.isNotBlank()) {
            emit(AuthResult.Success(AuthUser("u1", "Test User", email, "mock_token_123")))
        } else {
            emit(AuthResult.Error("Invalid credentials"))
        }
    }

    override fun register(name: String, email: String, password: String): Flow<AuthResult> = flow {
        delay(1500)
        if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
            emit(AuthResult.Success(AuthUser("u1", name, email, "mock_token_123")))
        } else {
            emit(AuthResult.Error("Please fill all fields"))
        }
    }
}
