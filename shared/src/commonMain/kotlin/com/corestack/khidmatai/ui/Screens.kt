package com.corestack.khidmatai.ui

import kotlinx.serialization.Serializable

@Serializable
data object Onboarding

@Serializable
data object Home

@Serializable
data object Processing

@Serializable
data object ResultSuccess

@Serializable
data object ResultUnavailable

@Serializable
data class BookingDetail(val bookingId: String)

@Serializable
data object Bookings

@Serializable
data object VoiceInput
