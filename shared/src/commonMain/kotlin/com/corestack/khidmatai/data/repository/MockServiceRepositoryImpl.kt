package com.corestack.khidmatai.data.repository

import com.corestack.khidmatai.domain.model.*
import com.corestack.khidmatai.domain.repository.ServiceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single
class MockServiceRepositoryImpl : ServiceRepository {
    override fun submitRequest(query: String, location: String, urgency: String): Flow<RequestState> = flow {
        // Initial mock trace
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
        
        // Progressively complete trace items
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
                id = "p1", name = "Kamran Khan",
                phone = "+923001234567", rating = 4.7f,
                distanceKm = 1.2f, experienceYears = 8,
                reasoning = "Kamran Khan is the top match with rating 4.7, located 1.2km from you."
            ),
            appointment = Appointment(
                bookingId = "BK-1747391234",
                timeDisplay = "10:30 AM, 17 May",
                address = location,
                costPerHour = 1500, currency = "PKR"
            ),
            nextSteps = listOf(
                NextStep(1, "Provider call karega", "Kamran Khan aapko 15 minutes ke andar call karega.", "action", "+923001234567", "Call Now"),
                NextStep(2, "Jagah saaf karein", "Service ke liye relevant area clear karein.", "info", null, null),
                NextStep(3, "Reminder mil jayega", "Appointment se 1 ghanta pehle (09:30 AM) aapko reminder milega.", "info", "09:30 AM", "Set Reminder"),
                NextStep(4, "Booking track karein", "App mein apni booking ka real-time status dekh sakte hain.", "info", "BK-1747391234", "Track")
            ),
            trace = trace.toList()
        )
        
        emit(RequestState.Success(mockSuccessResponse))
    }
}
