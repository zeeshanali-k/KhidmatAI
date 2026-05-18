package com.corestack.khidmatai.data.repository

import com.corestack.khidmatai.data.dto.ApiResponse
import com.corestack.khidmatai.data.dto.LocationBody
import com.corestack.khidmatai.data.dto.ServiceRequestBody
import com.corestack.khidmatai.domain.model.*
import com.corestack.khidmatai.domain.repository.ServiceRepository
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
) : ServiceRepository {

    override fun submitRequest(query: String, location: String, urgency: String): Flow<RequestState> = flow {
        emit(RequestState.Processing(emptyList()))

        try {
            val body = ServiceRequestBody(
                userId = "user_001",
                rawQuery = query,
                urgency = urgency,
                location = LocationBody(
                    address = location,
                    lat = DEFAULT_LAT,
                    lng = DEFAULT_LNG
                )
            )

            val response: ApiResponse = httpClient.post("$BASE_URL/requests/") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body()

            // Replay trace items with staggered delays to drive the ProcessingScreen animation
            val stageDelayMs = if (urgency == "emergency") 150L else 350L
            val accumulatedTraces = mutableListOf<TraceItem>()
            for (t in response.data.trace) {
                accumulatedTraces.add(TraceItem(t.stage, t.message, t.status))
                emit(RequestState.Processing(accumulatedTraces.toList()))
                delay(stageDelayMs)
            }

            val result = mapToServiceResult(response)
            if (response.success) {
                emit(RequestState.Success(result))
            } else {
                emit(RequestState.Unavailable(result))
            }
        } catch (e: Exception) {
            emit(RequestState.Error(e.message ?: "Network error. Please check your connection."))
        }
    }

    private fun mapToServiceResult(response: ApiResponse): ServiceResult {
        val data = response.data
        val meta = response.meta
        return ServiceResult(
            success = response.success,
            status = data.status,
            message = data.message,
            bookingId = meta.bookingId,
            detectedService = meta.detectedIntent,
            detectedLanguage = meta.detectedLanguage,
            urgency = meta.urgency,
            provider = data.provider?.let { p ->
                Provider(
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
                Appointment(
                    bookingId = a.bookingId,
                    timeDisplay = a.scheduledTimeDisplay,
                    address = a.location.address,
                    costPerHour = a.costPerHour,
                    currency = a.currency
                )
            },
            nextSteps = data.nextSteps.map { s ->
                NextStep(
                    id = s.step,
                    title = s.title,
                    description = s.description,
                    type = s.type,
                    actionValue = s.actionValue,
                    actionLabel = s.actionLabel
                )
            },
            trace = data.trace.map { t ->
                TraceItem(t.stage, t.message, t.status)
            },
            error = data.error?.message
        )
    }
}
