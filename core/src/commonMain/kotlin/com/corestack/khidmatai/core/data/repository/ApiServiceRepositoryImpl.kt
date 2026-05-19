package com.corestack.khidmatai.core.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

// 10.0.2.2 maps to host machine's localhost when running on an Android emulator.
// For a physical device on the same LAN, replace with your machine's local IP (e.g. 192.168.1.x).
private const val BASE_URL = "http://10.0.2.2:8000"

// Default coordinates for G-13 Islamabad; used when no GPS is available.
private const val DEFAULT_LAT = 33.6333
private const val DEFAULT_LNG = 72.9667

class ApiServiceRepositoryImpl(
    private val httpClient: HttpClient
) : com.corestack.khidmatai.core.domain.repository.ServiceRepository {

    override fun submitRequest(query: String, location: String, urgency: String): Flow<com.corestack.khidmatai.core.domain.model.RequestState> = flow {
        emit(_root_ide_package_.com.corestack.khidmatai.core.domain.model.RequestState.Processing(emptyList()))

        try {
            val body = _root_ide_package_.com.corestack.khidmatai.core.data.dto.ServiceRequestBody(
                userId = "user_001",
                rawQuery = query,
                urgency = urgency,
                location = _root_ide_package_.com.corestack.khidmatai.core.data.dto.LocationBody(
                    address = location,
                    lat = _root_ide_package_.com.corestack.khidmatai.core.data.repository.DEFAULT_LAT,
                    lng = _root_ide_package_.com.corestack.khidmatai.core.data.repository.DEFAULT_LNG
                )
            )

            val response: com.corestack.khidmatai.core.data.dto.ApiResponse = httpClient.post("${_root_ide_package_.com.corestack.khidmatai.core.data.repository.BASE_URL}/requests/") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body()

            // Replay trace items with staggered delays to drive the ProcessingScreen animation
            val stageDelayMs = if (urgency == "emergency") 150L else 350L
            val accumulatedTraces = mutableListOf<com.corestack.khidmatai.core.domain.model.TraceItem>()
            for (t in response.data.trace) {
                accumulatedTraces.add(
                    _root_ide_package_.com.corestack.khidmatai.core.domain.model.TraceItem(
                        t.stage,
                        t.message,
                        t.status
                    )
                )
                emit(_root_ide_package_.com.corestack.khidmatai.core.domain.model.RequestState.Processing(accumulatedTraces.toList()))
                delay(stageDelayMs)
            }

            val result = mapToServiceResult(response)
            if (response.success) {
                emit(_root_ide_package_.com.corestack.khidmatai.core.domain.model.RequestState.Success(result))
            } else {
                emit(_root_ide_package_.com.corestack.khidmatai.core.domain.model.RequestState.Unavailable(result))
            }
        } catch (e: Exception) {
            emit(_root_ide_package_.com.corestack.khidmatai.core.domain.model.RequestState.Error(e.message ?: "Network error. Please check your connection."))
        }
    }

    private fun mapToServiceResult(response: com.corestack.khidmatai.core.data.dto.ApiResponse): com.corestack.khidmatai.core.domain.model.ServiceResult {
        val data = response.data
        val meta = response.meta
        return _root_ide_package_.com.corestack.khidmatai.core.domain.model.ServiceResult(
            success = response.success,
            status = data.status,
            message = data.message,
            bookingId = meta.bookingId,
            detectedService = meta.detectedIntent,
            detectedLanguage = meta.detectedLanguage,
            urgency = meta.urgency,
            provider = data.provider?.let { p ->
                _root_ide_package_.com.corestack.khidmatai.core.domain.model.Provider(
                    id = p.id,
                    name = p.name,
                    phone = p.phone,
                    rating = p.rating,
                    distanceKm = p.distanceKm,
                    experienceYears = p.experienceYears,
                    reasoning = p.reasoning
                )
            },
            appointment = data.appointment?.let { a ->
                _root_ide_package_.com.corestack.khidmatai.core.domain.model.Appointment(
                    bookingId = a.bookingId,
                    timeDisplay = a.scheduledTimeDisplay,
                    address = a.location.address,
                    costPerHour = a.costPerHour,
                    currency = a.currency
                )
            },
            nextSteps = data.nextSteps.map { s ->
                _root_ide_package_.com.corestack.khidmatai.core.domain.model.NextStep(
                    id = s.step,
                    title = s.title,
                    description = s.description,
                    type = s.type,
                    actionValue = s.actionValue,
                    actionLabel = s.actionLabel
                )
            },
            trace = data.trace.map { t ->
                _root_ide_package_.com.corestack.khidmatai.core.domain.model.TraceItem(
                    t.stage,
                    t.message,
                    t.status
                )
            },
            followup = data.followup?.let { f ->
                _root_ide_package_.com.corestack.khidmatai.core.domain.model.Followup(
                    f.reminderScheduled,
                    f.reminderTimeDisplay,
                    f.statusUpdate,
                    f.completionConfirmation
                )
            },
            error = data.error?.message
        )
    }
}
