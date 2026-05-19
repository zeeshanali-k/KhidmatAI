package com.corestack.khidmatai.ui

import kotlinx.serialization.Serializable

@Serializable
data object Splash

@Serializable
data object Login

@Serializable
data object Register

@Serializable
data object Onboarding

@Serializable
data object Home

@Serializable
data object ServiceRequestProcessing

@Serializable
data object ServiceResultSuccess

@Serializable
data object ServiceResultUnavailable

@Serializable
data class BookingDetail(val bookingId: String)

@Serializable
data object Bookings

@Serializable
data object VoiceInput

@Serializable
data object Profile

@Serializable
data object LocationPicker
