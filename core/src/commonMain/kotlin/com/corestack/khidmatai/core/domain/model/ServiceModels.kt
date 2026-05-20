package com.corestack.khidmatai.core.domain.model

data class ServiceResult(
    val success: Boolean,
    val status: String,
    val message: String,
    val bookingId: String?,
    val detectedService: String?,
    val detectedLanguage: String?,
    val urgency: String?,
    val provider: com.corestack.khidmatai.core.domain.model.Provider?,
    val appointment: com.corestack.khidmatai.core.domain.model.Appointment?,
    val nextSteps: List<com.corestack.khidmatai.core.domain.model.NextStep> = emptyList(),
    val trace: List<com.corestack.khidmatai.core.domain.model.TraceItem> = emptyList(),
    val followup: com.corestack.khidmatai.core.domain.model.Followup? = null,
    val error: String? = null
)

data class Provider(
    val id: String,
    val name: String,
    val phone: String,
    val rating: Float,
    val distanceKm: Float,
    val experienceYears: Int,
    val reasoning: String,
    val rangeKm: Double = 10.0
)

data class ProviderOption(
    val id: String,
    val name: String,
    val serviceType: String,
    val rating: Float,
    val distanceKm: Float,
    val pricePerHour: Double,
    val experienceYears: Int,
    val score: Double,
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
    val status: String, // completed, pending, waiting, failed
    val requestId: String? = null
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
    data class Processing(
        val traces: List<com.corestack.khidmatai.core.domain.model.TraceItem>,
        val planMessage: String? = null
    ) : RequestState()
    data class AwaitingProviderSelection(
        val providers: List<com.corestack.khidmatai.core.domain.model.ProviderOption>,
        val requestId: String,
        val traces: List<com.corestack.khidmatai.core.domain.model.TraceItem>
    ) : RequestState()
    data class Success(val result: com.corestack.khidmatai.core.domain.model.ServiceResult) : RequestState()
    data class Unavailable(val result: com.corestack.khidmatai.core.domain.model.ServiceResult) : RequestState()
    data class Error(val message: String) : RequestState()
}

data class ServiceCategory(
    val id: String,
    val value: String,
    val label: String
)

data class Booking(
    val id: String,
    val userId: String,
    val providerId: String,
    val serviceType: String,
    val status: String,
    val scheduledAt: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val totalCost: Double?,
    val createdAt: String,
    val updatedAt: String
)

