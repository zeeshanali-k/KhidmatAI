package com.corestack.khidmatai.core.domain.repository

import com.corestack.khidmatai.core.domain.model.Booking
import com.corestack.khidmatai.core.domain.model.Provider
import com.corestack.khidmatai.core.domain.model.RequestState
import com.corestack.khidmatai.core.domain.model.ServiceCategory
import com.corestack.khidmatai.core.domain.model.TraceItem
import kotlinx.coroutines.flow.Flow

interface ServiceRepository {
    fun submitRequest(query: String, location: String, urgency: String): Flow<RequestState>
    
    fun submitRequestStream(query: String, location: String, urgency: String): Flow<RequestState>
    
    suspend fun getServiceCategories(): List<ServiceCategory>
    
    suspend fun getAvailableProviders(): List<Provider>
    
    suspend fun getBookingHistory(userId: String): List<Booking>
    
    suspend fun getBookingDetails(bookingId: String): Booking
    
    suspend fun cancelBooking(bookingId: String): Booking
    
    suspend fun completeBooking(bookingId: String): List<TraceItem>

    suspend fun cancelRequest(requestId: String): Boolean
}
