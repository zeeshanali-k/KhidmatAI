package com.corestack.khidmatai.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdminBookingDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("provider_id") val providerId: String,
    @SerialName("service_type") val serviceType: String,
    val status: String,
    @SerialName("scheduled_at") val scheduledAt: String,
    val location: com.corestack.khidmatai.core.data.dto.ApiLocation,
    @SerialName("total_cost") val totalCost: Double? = null,
    @SerialName("created_at") val createdAt: String
)

@Serializable
data class AdminProviderDto(
    val id: String,
    val name: String,
    @SerialName("service_type") val serviceType: String,
    val rating: Float,
    val phone: String,
    @SerialName("price_per_hour") val pricePerHour: Double,
    @SerialName("experience_years") val experienceYears: Int,
    val availability: Boolean = true,
    val location: com.corestack.khidmatai.core.data.dto.ApiLocation
)

@Serializable
data class AdminProviderCreateDto(
    val name: String,
    @SerialName("service_type") val serviceType: String,
    val rating: Float,
    val phone: String,
    @SerialName("price_per_hour") val pricePerHour: Double,
    @SerialName("experience_years") val experienceYears: Int,
    val availability: Boolean = true,
    val location: com.corestack.khidmatai.core.data.dto.ApiLocation
)

@Serializable
data class AdminRequestDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("raw_query") val rawQuery: String,
    val urgency: String,
    val intent: String? = null,
    val language: String = "en",
    val status: String,
    @SerialName("booking_id") val bookingId: String? = null,
    val trace: List<com.corestack.khidmatai.core.data.dto.ApiTrace> = emptyList(),
    @SerialName("created_at") val createdAt: String
)
