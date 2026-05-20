package com.corestack.khidmatai.core.data.repository

import com.corestack.khidmatai.core.data.dto.ApiLocation
import com.corestack.khidmatai.core.data.dto.LocationBody
import com.corestack.khidmatai.core.data.dto.ServiceCategoryDto
import com.corestack.khidmatai.core.data.dto.ServiceRequestBody
import com.corestack.khidmatai.core.data.dto.UserApiResponse
import com.corestack.khidmatai.core.data.dto.UserBookingDto
import com.corestack.khidmatai.core.data.dto.UserProviderDto
import com.corestack.khidmatai.core.data.dto.ApiTrace
import com.corestack.khidmatai.core.data.dto.ApiResponse
import com.corestack.khidmatai.core.domain.model.Appointment
import com.corestack.khidmatai.core.domain.model.Booking
import com.corestack.khidmatai.core.domain.model.Provider
import com.corestack.khidmatai.core.domain.model.RequestState
import com.corestack.khidmatai.core.domain.model.ServiceCategory
import com.corestack.khidmatai.core.domain.model.ServiceResult
import com.corestack.khidmatai.core.domain.model.TraceItem
import com.corestack.khidmatai.core.domain.repository.ServiceRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.corestack.khidmatai.core.domain.preferences.AppPreferences
import com.corestack.khidmatai.core.util.getApiBaseUrl

private val BASE_URL get() = getApiBaseUrl()

private const val DEFAULT_LAT = 33.6333
private const val DEFAULT_LNG = 72.9667

class ApiServiceRepositoryImpl(
    private val httpClient: HttpClient,
    private val appPreferences: AppPreferences
) : ServiceRepository {

    private fun getUserId(): String {
        val email = appPreferences.lastEmail
        return email.ifBlank { "user_001" }
    }

    private fun <T> UserApiResponse<T>.unwrap(): T {
        if (!success) {
            val errType = error?.type ?: "UnknownError"
            throw Exception("$errType: $message")
        }
        return data ?: throw Exception("Response data is null")
    }

    override fun submitRequest(query: String, location: String, urgency: String): Flow<RequestState> = flow {
        emit(RequestState.Processing(emptyList()))

        try {
            val body = ServiceRequestBody(
                userId = getUserId(),
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

    override fun submitRequestStream(query: String, location: String, urgency: String): Flow<RequestState> = flow {
        emit(RequestState.Processing(emptyList()))

        try {
            val body = ServiceRequestBody(
                userId = getUserId(),
                rawQuery = query,
                urgency = urgency,
                location = LocationBody(
                    address = location,
                    lat = DEFAULT_LAT,
                    lng = DEFAULT_LNG
                )
            )

            val accumulatedTraces = mutableListOf<TraceItem>()
            var bookingId: String? = null

            httpClient.preparePost("$BASE_URL/requests/stream") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }.execute { response ->
                val channel = response.bodyAsChannel()
                while (!channel.isClosedForRead) {
                    val line = channel.readUTF8Line() ?: break
                    if (line.startsWith("data: ")) {
                        val rawData = line.substring(6).trim()
                        if (rawData == "[DONE]") {
                            break
                        }
                        
                        val jsonElement = runCatching {
                            Json.parseToJsonElement(rawData)
                        }.getOrNull() ?: continue

                        val jsonObject = jsonElement.jsonObject
                        for (key in jsonObject.keys) {
                            val stageObj = jsonObject[key]?.jsonObject ?: continue
                            
                            stageObj["trace"]?.jsonArray?.forEach { traceElem ->
                                val tObj = traceElem.jsonObject
                                val stage = tObj["stage"]?.jsonPrimitive?.content ?: ""
                                val msg = tObj["message"]?.jsonPrimitive?.content ?: ""
                                val status = tObj["status"]?.jsonPrimitive?.content ?: ""
                                val reqId = tObj["request_id"]?.jsonPrimitive?.content
                                
                                val traceItem = TraceItem(stage, msg, status, reqId)
                                if (!accumulatedTraces.any { it.stage == stage && it.message == msg }) {
                                    accumulatedTraces.add(traceItem)
                                }
                            }
                            
                            stageObj["booking"]?.jsonObject?.let { bObj ->
                                bookingId = bObj["id"]?.jsonPrimitive?.content
                            }
                        }

                        if (accumulatedTraces.isNotEmpty()) {
                            emit(RequestState.Processing(accumulatedTraces.toList()))
                        }
                    }
                }
            }

            val finalBookingId = bookingId
            if (finalBookingId != null) {
                val bookingDetails = getBookingDetails(finalBookingId)
                val serviceResult = ServiceResult(
                    success = true,
                    status = bookingDetails.status,
                    message = "Booking confirmed successfully",
                    bookingId = bookingDetails.id,
                    detectedService = bookingDetails.serviceType,
                    detectedLanguage = "en",
                    urgency = urgency,
                    provider = Provider(
                        id = bookingDetails.providerId,
                        name = "Provider",
                        phone = "",
                        rating = 5.0f,
                        distanceKm = 0.0f,
                        experienceYears = 0,
                        reasoning = "Assigned to provider"
                    ),
                    appointment = Appointment(
                        bookingId = bookingDetails.id,
                        timeDisplay = bookingDetails.scheduledAt,
                        address = bookingDetails.address,
                        costPerHour = bookingDetails.totalCost?.toInt() ?: 0,
                        currency = "PKR"
                    ),
                    nextSteps = emptyList(),
                    trace = accumulatedTraces.toList(),
                    followup = null,
                    error = null
                )
                emit(RequestState.Success(serviceResult))
            } else {
                emit(RequestState.Error("No booking was executed."))
            }

        } catch (e: Exception) {
            emit(RequestState.Error(e.message ?: "Network error. Please check your connection."))
        }
    }

    override suspend fun getServiceCategories(): List<ServiceCategory> =
        httpClient.get("$BASE_URL/services/")
            .body<UserApiResponse<List<ServiceCategoryDto>>>()
            .unwrap()
            .map { it.toDomain() }

    override suspend fun getAvailableProviders(): List<Provider> =
        httpClient.get("$BASE_URL/services/providers")
            .body<UserApiResponse<List<UserProviderDto>>>()
            .unwrap()
            .map { it.toDomain() }

    override suspend fun getBookingHistory(userId: String): List<Booking> =
        httpClient.get("$BASE_URL/bookings/user/$userId")
            .body<UserApiResponse<List<UserBookingDto>>>()
            .unwrap()
            .map { it.toDomain() }

    override suspend fun getBookingDetails(bookingId: String): Booking =
        httpClient.get("$BASE_URL/bookings/detail/$bookingId")
            .body<UserApiResponse<UserBookingDto>>()
            .unwrap()
            .toDomain()

    override suspend fun cancelBooking(bookingId: String): Booking =
        httpClient.post("$BASE_URL/bookings/$bookingId/cancel")
            .body<UserApiResponse<UserBookingDto>>()
            .unwrap()
            .toDomain()

    override suspend fun completeBooking(bookingId: String): List<TraceItem> {
        val response = httpClient.post("$BASE_URL/bookings/$bookingId/complete")
            .body<UserApiResponse<Map<String, List<ApiTrace>>>>()
            .unwrap()
        val traceList = response["trace"] ?: emptyList()
        return traceList.map { TraceItem(it.stage, it.message, it.status) }
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
                com.corestack.khidmatai.core.domain.model.NextStep(
                    id = s.step,
                    title = s.title,
                    description = s.description,
                    type = s.type,
                    actionValue = s.actionValue,
                    actionLabel = s.actionLabel
                )
            },
            trace = data.trace.map { t ->
                TraceItem(
                    t.stage,
                    t.message,
                    t.status
                )
            },
            followup = data.followup?.let { f ->
                com.corestack.khidmatai.core.domain.model.Followup(
                    f.reminderScheduled,
                    f.reminderTimeDisplay,
                    f.statusUpdate,
                    f.completionConfirmation
                )
            },
            error = data.error?.message
        )
    }

    private fun ServiceCategoryDto.toDomain() = ServiceCategory(
        id = id,
        value = value,
        label = label
    )

    private fun UserProviderDto.toDomain() = Provider(
        id = id,
        name = name,
        phone = phone,
        rating = rating,
        distanceKm = 0.0f,
        experienceYears = experienceYears,
        reasoning = "Provider available in catalog"
    )

    private fun UserBookingDto.toDomain() = Booking(
        id = id,
        userId = userId,
        providerId = providerId,
        serviceType = serviceType,
        status = status,
        scheduledAt = scheduledAt,
        address = location.address,
        lat = location.lat,
        lng = location.lng,
        totalCost = totalCost,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    override suspend fun cancelRequest(requestId: String): Boolean {
        return try {
            val response = httpClient.post("$BASE_URL/requests/$requestId/cancel")
                .body<UserApiResponse<Map<String, String>>>()
            response.success
        } catch (e: Exception) {
            false
        }
    }
}
