package com.corestack.khidmatai.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<com.corestack.khidmatai.core.domain.model.AuthResult>
    fun register(name: String, email: String, password: String): Flow<com.corestack.khidmatai.core.domain.model.AuthResult>
    fun getLastEmail(): String
    fun isLoggedIn(): Boolean
}
