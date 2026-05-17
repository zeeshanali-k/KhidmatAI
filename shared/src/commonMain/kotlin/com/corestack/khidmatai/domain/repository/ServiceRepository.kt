package com.corestack.khidmatai.domain.repository

import com.corestack.khidmatai.domain.model.RequestState
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun submitRequest(query: String, location: String, urgency: String): Flow<RequestState>
}
