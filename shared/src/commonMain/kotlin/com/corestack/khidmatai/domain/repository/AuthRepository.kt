package com.corestack.khidmatai.domain.repository

import com.corestack.khidmatai.domain.model.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<AuthResult>
    fun register(name: String, email: String, password: String): Flow<AuthResult>
}
