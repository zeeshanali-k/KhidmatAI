package com.corestack.khidmatai.data.repository

import com.corestack.khidmatai.domain.model.RequestState
import com.corestack.khidmatai.domain.repository.ServiceRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

class ApiServiceRepositoryImpl(
    private val httpClient: HttpClient
) : ServiceRepository {
    override fun submitRequest(query: String, location: String, urgency: String): Flow<RequestState> = flow {
        // TODO: Implement actual Ktor API call
        emit(RequestState.Error("Prod API not implemented yet"))
    }
}
