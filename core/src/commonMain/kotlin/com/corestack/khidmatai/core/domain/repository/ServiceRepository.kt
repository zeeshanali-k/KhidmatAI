package com.corestack.khidmatai.core.domain.repository

import com.corestack.khidmatai.core.domain.model.RequestState
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun submitRequest(query: String, location: String, urgency: String): Flow<com.corestack.khidmatai.core.domain.model.RequestState>
}
