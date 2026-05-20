package com.corestack.khidmatai.core.data.repository

import com.corestack.khidmatai.core.domain.model.Appointment
import com.corestack.khidmatai.core.domain.model.Booking
import com.corestack.khidmatai.core.domain.model.Followup
import com.corestack.khidmatai.core.domain.model.NextStep
import com.corestack.khidmatai.core.domain.model.Provider
import com.corestack.khidmatai.core.domain.model.RequestState
import com.corestack.khidmatai.core.domain.model.ServiceCategory
import com.corestack.khidmatai.core.domain.model.ServiceResult
import com.corestack.khidmatai.core.domain.model.TraceItem
import com.corestack.khidmatai.core.domain.repository.ServiceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockServiceRepositoryImpl : ServiceRepository {

    override fun submitRequest(query: String, location: String, urgency: String): Flow<RequestState> = flow {
        val trace = mutableListOf(
            TraceItem("intent_detection", "Input Query received and parsed", "waiting"),
            TraceItem("llm_analysis", "intent=ac_technician language=roman_urdu", "waiting"),
            TraceItem("service_classification", "Service Detected: AC_TECHNICIAN", "waiting"),
            TraceItem("provider_discovery", "Found 3 verified providers near $location", "waiting"),
            TraceItem("provider_ranking", "Kamran Khan selected with score 12.16", "waiting"),
            TraceItem("booking_execution", "Booking BK-1747391234 confirmed in DB", "waiting"),
            TraceItem("followup", "Reminder scheduled for 09:30 AM", "waiting")
        )
        
        emit(RequestState.Processing(trace.toList()))
        
        val delayTime = if (urgency == "emergency") 150L else 350L
        
        for (i in trace.indices) {
            delay(delayTime)
            trace[i] = trace[i].copy(status = "completed")
            if (i < trace.size - 1) {
                trace[i + 1] = trace[i + 1].copy(status = "pending")
            }
            emit(RequestState.Processing(trace.toList()))
        }
        
        delay(delayTime)
        
        val mockSuccessResponse = ServiceResult(
            success = true,
            status = "success",
            message = "Booking confirmed. Kamran Khan will contact you before 10:30 AM, 17 May.",
            bookingId = "BK-1747391234",
            detectedService = "ac_technician",
            detectedLanguage = "roman_urdu",
            urgency = urgency,
            provider = Provider(
                id = "p1",
                name = "Kamran Khan",
                phone = "+923001234567",
                rating = 4.7f,
                distanceKm = 1.2f,
                experienceYears = 8,
                reasoning = "Kamran Khan is the top match with rating 4.7, located 1.2km from you."
            ),
            appointment = Appointment(
                bookingId = "BK-1747391234",
                timeDisplay = "10:30 AM, 17 May",
                address = location,
                costPerHour = 1500,
                currency = "PKR"
            ),
            nextSteps = listOf(
                NextStep(1, "Provider will call", "Kamran Khan will call you within 15 minutes.", "action", "+923001234567", "Call Now"),
                NextStep(2, "Clear the area", "Clear the relevant area for the service.", "info", null, null),
                NextStep(3, "Reminder scheduled", "You will receive a reminder 1 hour before your appointment (09:30 AM).", "info", "09:30 AM", "Set Reminder"),
                NextStep(4, "Track booking", "View your booking status in real-time in the app.", "info", "BK-1747391234", "Track")
            ),
            trace = trace.toList(),
            followup = Followup(
                reminderScheduled = true,
                reminderTimeDisplay = "09:30 AM",
                statusUpdate = "Booking Confirmed",
                completionConfirmation = false
            )
        )
        
        emit(RequestState.Success(mockSuccessResponse))
    }

    override fun submitRequestStream(query: String, location: String, urgency: String): Flow<RequestState> =
        submitRequest(query, location, urgency)

    override suspend fun getServiceCategories(): List<ServiceCategory> {
        return listOf(
            ServiceCategory("AC_TECHNICIAN", "ac_technician", "Ac Technician"),
            ServiceCategory("PLUMBER", "plumber", "Plumber"),
            ServiceCategory("ELECTRICIAN", "electrician", "Electrician"),
            ServiceCategory("TUTOR", "tutor", "Tutor"),
            ServiceCategory("CLEANER", "cleaner", "Cleaner"),
            ServiceCategory("PAINTER", "painter", "Painter"),
            ServiceCategory("BEAUTICIAN", "beautician", "Beautician")
        )
    }

    override suspend fun getAvailableProviders(): List<Provider> {
        return listOf(
            Provider(
                id = "p1",
                name = "Kamran Khan",
                phone = "+923001234567",
                rating = 4.7f,
                distanceKm = 1.2f,
                experienceYears = 8,
                reasoning = "Kamran Khan is the top match with rating 4.7, located 1.2km from you."
            )
        )
    }

    override suspend fun getBookingHistory(userId: String): List<Booking> {
        return listOf(
            Booking(
                id = "BK-1747391234",
                userId = userId,
                providerId = "p1",
                serviceType = "ac_technician",
                status = "confirmed",
                scheduledAt = "2026-05-21T10:30:00",
                address = "G-13, Islamabad",
                lat = 33.6333,
                lng = 72.9667,
                totalCost = 1500.0,
                createdAt = "2026-05-20T10:15:30",
                updatedAt = "2026-05-20T10:15:30"
            )
        )
    }

    override suspend fun getBookingDetails(bookingId: String): Booking {
        return Booking(
            id = bookingId,
            userId = "user_001",
            providerId = "p1",
            serviceType = "ac_technician",
            status = "confirmed",
            scheduledAt = "2026-05-21T10:30:00",
            address = "G-13, Islamabad",
            lat = 33.6333,
            lng = 72.9667,
            totalCost = 1500.0,
            createdAt = "2026-05-20T10:15:30",
            updatedAt = "2026-05-20T10:15:30"
        )
    }

    override suspend fun cancelBooking(bookingId: String): Booking {
        return getBookingDetails(bookingId).copy(status = "cancelled")
    }

    override suspend fun completeBooking(bookingId: String): List<TraceItem> {
        return listOf(
            TraceItem(
                "booking_completed",
                "Booking marked as completed successfully",
                "completed"
            )
        )
    }

    override suspend fun cancelRequest(requestId: String): Boolean {
        return true
    }

    override suspend fun selectProvider(requestId: String, providerId: String): Boolean {
        return true
    }
}
