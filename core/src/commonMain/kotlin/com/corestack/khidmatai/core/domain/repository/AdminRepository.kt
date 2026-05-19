package com.corestack.khidmatai.core.domain.repository

import com.corestack.khidmatai.core.domain.model.AdminBooking
import com.corestack.khidmatai.core.domain.model.AdminProvider
import com.corestack.khidmatai.core.domain.model.AdminRequest

interface AdminRepository {
    // Bookings
    suspend fun getAllBookings(): List<com.corestack.khidmatai.core.domain.model.AdminBooking>
    suspend fun getBookingById(bookingId: String): com.corestack.khidmatai.core.domain.model.AdminBooking
    suspend fun cancelBooking(bookingId: String): com.corestack.khidmatai.core.domain.model.AdminBooking
    suspend fun completeBooking(bookingId: String): com.corestack.khidmatai.core.domain.model.AdminBooking

    // Providers
    suspend fun getAllProviders(): List<com.corestack.khidmatai.core.domain.model.AdminProvider>
    suspend fun createProvider(provider: com.corestack.khidmatai.core.domain.model.AdminProvider): com.corestack.khidmatai.core.domain.model.AdminProvider
    suspend fun updateProvider(providerId: String, provider: com.corestack.khidmatai.core.domain.model.AdminProvider): com.corestack.khidmatai.core.domain.model.AdminProvider
    suspend fun deleteProvider(providerId: String)
    suspend fun toggleProviderAvailability(providerId: String): com.corestack.khidmatai.core.domain.model.AdminProvider

    // Requests
    suspend fun getAllRequests(): List<com.corestack.khidmatai.core.domain.model.AdminRequest>
    suspend fun getRequestById(requestId: String): com.corestack.khidmatai.core.domain.model.AdminRequest
}
