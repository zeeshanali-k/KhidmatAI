package com.corestack.khidmatai.domain.model

data class ServiceResult(
    val success: Boolean,
    val status: String,
    val message: String,
    val bookingId: String?,
    val detectedService: String?,
    val detectedLanguage: String?,
    val urgency: String?,
    val provider: Provider?,
    val appointment: Appointment?,
    val nextSteps: List<NextStep> = emptyList(),
    val trace: List<TraceItem> = emptyList(),
    val followup: Followup? = null,
    val error: String? = null
)

data class Provider(
    val id: String,
    val name: String,
    val phone: String,
    val rating: Float,
    val distanceKm: Float,
    val experienceYears: Int,
    val reasoning: String
)

data class Appointment(
    val bookingId: String,
    val timeDisplay: String,
    val address: String,
    val costPerHour: Int,
    val currency: String
)

data class TraceItem(
    val stage: String,
    val message: String,
    val status: String // completed, pending, waiting, failed
)

data class NextStep(
    val id: Int,
    val title: String,
    val description: String,
    val type: String, // action, info, warning
    val actionValue: String?,
    val actionLabel: String?
)

data class Followup(
    val reminderScheduled: Boolean,
    val reminderTimeDisplay: String?,
    val statusUpdate: String?,
    val completionConfirmation: Boolean
)

enum class AiOrbState {
    IDLE, THINKING, DONE, ERROR
}

sealed class RequestState {
    data object Idle : RequestState()
    data class Processing(val traces: List<TraceItem>) : RequestState()
    data class Success(val result: ServiceResult) : RequestState()
    data class Unavailable(val result: ServiceResult) : RequestState()
    data class Error(val message: String) : RequestState()
}
