package com.corestack.khidmatai.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceRequestBody(
    @SerialName("user_id") val userId: String,
    @SerialName("raw_query") val rawQuery: String,
    val urgency: String,
    val location: com.corestack.khidmatai.core.data.dto.LocationBody,
    @SerialName("language_detected") val languageDetected: String? = null
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
    val data: com.corestack.khidmatai.core.data.dto.ApiData,
    val meta: com.corestack.khidmatai.core.data.dto.ApiMeta
)

@Serializable
data class ApiData(
    val status: String,
    val message: String,
    val provider: com.corestack.khidmatai.core.data.dto.ApiProvider? = null,
    val appointment: com.corestack.khidmatai.core.data.dto.ApiAppointment? = null,
    @SerialName("next_steps") val nextSteps: List<com.corestack.khidmatai.core.data.dto.ApiNextStep> = emptyList(),
    val trace: List<com.corestack.khidmatai.core.data.dto.ApiTrace> = emptyList(),
    val followup: com.corestack.khidmatai.core.data.dto.ApiFollowup? = null,
    val error: com.corestack.khidmatai.core.data.dto.ApiError? = null
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
    val location: com.corestack.khidmatai.core.data.dto.ApiLocation,
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
    val stage: String = "",
    val message: String = "",
    val status: String = "unknown"
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

@Serializable
data class UserApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val error: UserErrorDetail? = null
)

@Serializable
data class UserErrorDetail(
    val type: String,
    val details: kotlinx.serialization.json.JsonElement? = null
)

@Serializable
data class ServiceCategoryDto(
    val id: String,
    val value: String,
    val label: String
)

@Serializable
data class UserProviderDto(
    val id: String,
    val name: String,
    @SerialName("service_type") val serviceType: String,
    val rating: Float,
    val location: ApiLocation,
    val phone: String,
    @SerialName("price_per_hour") val pricePerHour: Double,
    @SerialName("experience_years") val experienceYears: Int,
    val availability: Boolean = true,
    @SerialName("range_km") val rangeKm: Double = 10.0
)

@Serializable
data class ApiProviderOption(
    val id: String,
    val name: String,
    @SerialName("service_type") val serviceType: String,
    val rating: Float,
    @SerialName("distance_km") val distanceKm: Float,
    @SerialName("price_per_hour") val pricePerHour: Double,
    @SerialName("experience_years") val experienceYears: Int,
    val score: Double,
    val reasoning: String = ""
)

@Serializable
data class UserBookingDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("provider_id") val providerId: String,
    @SerialName("service_type") val serviceType: String,
    val status: String,
    @SerialName("scheduled_at") val scheduledAt: String,
    val location: ApiLocation,
    @SerialName("total_cost") val totalCost: Double? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)
