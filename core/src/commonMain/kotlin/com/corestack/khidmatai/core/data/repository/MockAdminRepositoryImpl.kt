package com.corestack.khidmatai.core.data.repository

import com.corestack.khidmatai.core.domain.model.AdminBooking
import com.corestack.khidmatai.core.domain.model.AdminProvider
import com.corestack.khidmatai.core.domain.model.AdminRequest
import com.corestack.khidmatai.core.domain.model.TraceItem
import com.corestack.khidmatai.core.domain.repository.AdminRepository
import kotlinx.coroutines.delay

class MockAdminRepositoryImpl : AdminRepository {
    
    private val mockBookings = mutableListOf(
        AdminBooking("b1", "u1", "p1", "Plumbing", "pending", "2026-05-20T10:00:00Z", "123 Main St", 50.0, "2026-05-19T10:00:00Z"),
        AdminBooking("b2", "u2", "p2", "Electrical", "completed", "2026-05-18T14:00:00Z", "456 Oak St", 75.0, "2026-05-17T10:00:00Z")
    )
    
    private val mockProviders = mutableListOf(
        AdminProvider("p1", "John Doe", "Plumbing", 4.5f, "+1234567890", 25.0, 5, true, "Downtown"),
        AdminProvider("p2", "Jane Smith", "Electrical", 4.8f, "+0987654321", 30.0, 8, false, "Uptown")
    )
    
    private val mockRequests = mutableListOf(
        AdminRequest("r1", "u1", "Need a plumber ASAP", "high", "Plumbing", "en", "pending", null, listOf(TraceItem("init", "Request received", "success")), "2026-05-19T10:00:00Z"),
        AdminRequest("r2", "u2", "Light bulb is broken", "low", "Electrical", "en", "processed", "b2", listOf(TraceItem("init", "Request received", "success"), TraceItem("process", "Booking created", "success")), "2026-05-17T09:00:00Z")
    )

    override suspend fun getAllBookings(): List<AdminBooking> {
        delay(1000)
        return mockBookings.toList()
    }

    override suspend fun getBookingById(bookingId: String): AdminBooking {
        delay(500)
        return mockBookings.first { it.id == bookingId }
    }

    override suspend fun cancelBooking(bookingId: String): AdminBooking {
        delay(500)
        val index = mockBookings.indexOfFirst { it.id == bookingId }
        val updated = mockBookings[index].copy(status = "cancelled")
        mockBookings[index] = updated
        return updated
    }

    override suspend fun completeBooking(bookingId: String): AdminBooking {
        delay(500)
        val index = mockBookings.indexOfFirst { it.id == bookingId }
        val updated = mockBookings[index].copy(status = "completed")
        mockBookings[index] = updated
        return updated
    }

    override suspend fun getAllProviders(): List<AdminProvider> {
        delay(1000)
        return mockProviders.toList()
    }

    override suspend fun createProvider(provider: AdminProvider): AdminProvider {
        delay(500)
        val newProvider = provider.copy(id = "p${mockProviders.size + 1}")
        mockProviders.add(newProvider)
        return newProvider
    }

    override suspend fun updateProvider(providerId: String, provider: AdminProvider): AdminProvider {
        delay(500)
        val index = mockProviders.indexOfFirst { it.id == providerId }
        val updated = provider.copy(id = providerId)
        mockProviders[index] = updated
        return updated
    }

    override suspend fun deleteProvider(providerId: String) {
        delay(500)
        mockProviders.removeAll { it.id == providerId }
    }

    override suspend fun toggleProviderAvailability(providerId: String): AdminProvider {
        delay(500)
        val index = mockProviders.indexOfFirst { it.id == providerId }
        val updated = mockProviders[index].copy(availability = !mockProviders[index].availability)
        mockProviders[index] = updated
        return updated
    }

    override suspend fun getAllRequests(): List<AdminRequest> {
        delay(1000)
        return mockRequests.toList()
    }

    override suspend fun getRequestById(requestId: String): AdminRequest {
        delay(500)
        return mockRequests.first { it.id == requestId }
    }
}
