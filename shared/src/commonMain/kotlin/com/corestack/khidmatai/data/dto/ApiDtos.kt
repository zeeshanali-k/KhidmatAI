package com.corestack.khidmatai.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceRequestBody(
    @SerialName("user_id") val userId: String,
    @SerialName("raw_query") val rawQuery: String,
    val urgency: String,
    val location: LocationBody
)

@Serializable
data class LocationBody(
    val address: String,
    val lat: Double,
    val lng: Double
)

@Serializable
data class ApiResponse(
    val success: Boolean,
    val data: ApiData,
    val meta: ApiMeta
)

@Serializable
data class ApiData(
    val status: String,
    val message: String,
    val provider: ApiProvider? = null,
    val appointment: ApiAppointment? = null,
    @SerialName("next_steps") val nextSteps: List<ApiNextStep> = emptyList(),
    val trace: List<ApiTrace> = emptyList(),
    val followup: ApiFollowup? = null,
    val error: ApiError? = null
)

@Serializable
data class ApiProvider(
    val id: String,
    val name: String,
    val phone: String,
    val rating: Float,
    @SerialName("distance_km") val distanceKm: Float,
    @SerialName("experience_years") val experienceYears: Int,
    val reasoning: String = ""
)

@Serializable
data class ApiAppointment(
    @SerialName("booking_id") val bookingId: String,
    @SerialName("scheduled_time_display") val scheduledTimeDisplay: String,
    val location: ApiLocation,
    @SerialName("cost_per_hour") val costPerHour: Int,
    val currency: String
)

@Serializable
data class ApiLocation(
    val address: String,
    val lat: Double,
    val lng: Double
)

@Serializable
data class ApiNextStep(
    val step: Int,
    val title: String,
    val description: String,
    val type: String,
    @SerialName("action_value") val actionValue: String? = null,
    @SerialName("action_label") val actionLabel: String? = null
)

@Serializable
data class ApiTrace(
    val stage: String,
    val message: String,
    val status: String
)

@Serializable
data class ApiFollowup(
    @SerialName("reminder_scheduled") val reminderScheduled: Boolean = false,
    @SerialName("reminder_time_display") val reminderTimeDisplay: String? = null,
    @SerialName("status_update") val statusUpdate: String? = null,
    @SerialName("completion_confirmation") val completionConfirmation: Boolean = false
)

@Serializable
data class ApiMeta(
    @SerialName("booking_id") val bookingId: String? = null,
    @SerialName("detected_intent") val detectedIntent: String? = null,
    @SerialName("detected_language") val detectedLanguage: String? = null,
    val urgency: String? = null
)

@Serializable
data class ApiError(
    val code: String? = null,
    val message: String? = null,
    val suggestion: String? = null
)
