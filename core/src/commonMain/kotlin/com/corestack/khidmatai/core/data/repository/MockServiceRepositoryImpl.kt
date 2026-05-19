package com.corestack.khidmatai.core.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockServiceRepositoryImpl : com.corestack.khidmatai.core.domain.repository.ServiceRepository {
    override fun submitRequest(query: String, location: String, urgency: String): Flow<com.corestack.khidmatai.core.domain.model.RequestState> = flow {
        // Initial mock trace
        val trace = mutableListOf(
            _root_ide_package_.com.corestack.khidmatai.core.domain.model.TraceItem(
                "intent_detection",
                "Input Query received and parsed",
                "waiting"
            ),
            _root_ide_package_.com.corestack.khidmatai.core.domain.model.TraceItem(
                "llm_analysis",
                "intent=ac_technician language=roman_urdu",
                "waiting"
            ),
            _root_ide_package_.com.corestack.khidmatai.core.domain.model.TraceItem(
                "service_classification",
                "Service Detected: AC_TECHNICIAN",
                "waiting"
            ),
            _root_ide_package_.com.corestack.khidmatai.core.domain.model.TraceItem(
                "provider_discovery",
                "Found 3 verified providers near $location",
                "waiting"
            ),
            _root_ide_package_.com.corestack.khidmatai.core.domain.model.TraceItem(
                "provider_ranking",
                "Kamran Khan selected with score 12.16",
                "waiting"
            ),
            _root_ide_package_.com.corestack.khidmatai.core.domain.model.TraceItem(
                "booking_execution",
                "Booking BK-1747391234 confirmed in DB",
                "waiting"
            ),
            _root_ide_package_.com.corestack.khidmatai.core.domain.model.TraceItem(
                "followup",
                "Reminder scheduled for 09:30 AM",
                "waiting"
            )
        )
        
        emit(_root_ide_package_.com.corestack.khidmatai.core.domain.model.RequestState.Processing(trace.toList()))
        
        val delayTime = if (urgency == "emergency") 150L else 350L
        
        // Progressively complete trace items
        for (i in trace.indices) {
            delay(delayTime)
            trace[i] = trace[i].copy(status = "completed")
            if (i < trace.size - 1) {
                trace[i + 1] = trace[i + 1].copy(status = "pending")
            }
            emit(_root_ide_package_.com.corestack.khidmatai.core.domain.model.RequestState.Processing(trace.toList()))
        }
        
        delay(delayTime)
        
        val mockSuccessResponse =
            _root_ide_package_.com.corestack.khidmatai.core.domain.model.ServiceResult(
                success = true,
                status = "success",
                message = "Booking confirmed. Kamran Khan will contact you before 10:30 AM, 17 May.",
                bookingId = "BK-1747391234",
                detectedService = "ac_technician",
                detectedLanguage = "roman_urdu",
                urgency = urgency,
                provider = _root_ide_package_.com.corestack.khidmatai.core.domain.model.Provider(
                    id = "p1", name = "Kamran Khan",
                    phone = "+923001234567", rating = 4.7f,
                    distanceKm = 1.2f, experienceYears = 8,
                    reasoning = "Kamran Khan is the top match with rating 4.7, located 1.2km from you."
                ),
                appointment = _root_ide_package_.com.corestack.khidmatai.core.domain.model.Appointment(
                    bookingId = "BK-1747391234",
                    timeDisplay = "10:30 AM, 17 May",
                    address = location,
                    costPerHour = 1500, currency = "PKR"
                ),
                nextSteps = listOf(
                    _root_ide_package_.com.corestack.khidmatai.core.domain.model.NextStep(
                        1,
                        "Provider will call",
                        "Kamran Khan will call you within 15 minutes.",
                        "action",
                        "+923001234567",
                        "Call Now"
                    ),
                    _root_ide_package_.com.corestack.khidmatai.core.domain.model.NextStep(
                        2,
                        "Clear the area",
                        "Clear the relevant area for the service.",
                        "info",
                        null,
                        null
                    ),
                    _root_ide_package_.com.corestack.khidmatai.core.domain.model.NextStep(
                        3,
                        "Reminder scheduled",
                        "You will receive a reminder 1 hour before your appointment (09:30 AM).",
                        "info",
                        "09:30 AM",
                        "Set Reminder"
                    ),
                    _root_ide_package_.com.corestack.khidmatai.core.domain.model.NextStep(
                        4,
                        "Track booking",
                        "View your booking status in real-time in the app.",
                        "info",
                        "BK-1747391234",
                        "Track"
                    )
                ),
                trace = trace.toList(),
                followup = _root_ide_package_.com.corestack.khidmatai.core.domain.model.Followup(
                    reminderScheduled = true,
                    reminderTimeDisplay = "09:30 AM",
                    statusUpdate = "Booking Confirmed",
                    completionConfirmation = false
                )
            )
        
        emit(_root_ide_package_.com.corestack.khidmatai.core.domain.model.RequestState.Success(mockSuccessResponse))
    }
}
