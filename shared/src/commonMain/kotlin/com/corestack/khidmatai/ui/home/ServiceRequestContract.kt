package com.corestack.khidmatai.ui.home

import com.corestack.khidmatai.domain.model.RequestState

data class ServiceRequestState(
    val query: String = "",
    val location: String = "G-13, Islamabad",
    val urgency: String = "medium",
    val selectedLanguage: String = "EN",
    val requestState: RequestState = RequestState.Idle
)

sealed class ServiceRequestIntent {
    data class UpdateQuery(val query: String) : ServiceRequestIntent()
    data class UpdateLocation(val location: String) : ServiceRequestIntent()
    data class UpdateUrgency(val urgency: String) : ServiceRequestIntent()
    data class UpdateLanguage(val language: String) : ServiceRequestIntent()
    data object SubmitRequest : ServiceRequestIntent()
    data object Reset : ServiceRequestIntent()
}
