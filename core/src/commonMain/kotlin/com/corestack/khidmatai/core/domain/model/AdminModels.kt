package com.corestack.khidmatai.core.domain.model

data class AdminBooking(
    val id: String,
    val userId: String,
    val providerId: String,
    val serviceType: String,
    val status: String,
    val scheduledAt: String,
    val address: String,
    val totalCost: Double?,
    val createdAt: String
)

data class AdminProvider(
    val id: String,
    val name: String,
    val serviceType: String,
    val rating: Float,
    val phone: String,
    val pricePerHour: Double,
    val experienceYears: Int,
    val availability: Boolean,
    val locationAddress: String
)

data class AdminRequest(
    val id: String,
    val userId: String,
    val rawQuery: String,
    val urgency: String,
    val intent: String?,
    val language: String,
    val status: String,
    val bookingId: String?,
    val trace: List<com.corestack.khidmatai.core.domain.model.TraceItem>,
    val createdAt: String
)

sealed class AdminState<out T> {
    data object Loading : AdminState<Nothing>()
    data class Success<T>(val data: T) : AdminState<T>()
    data class Error(val message: String) : AdminState<Nothing>()
}
