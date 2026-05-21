package com.corestack.khidmatai.core.data.repository

import com.corestack.khidmatai.core.data.dto.AdminApiResponse
import com.corestack.khidmatai.core.data.dto.AdminBookingDto
import com.corestack.khidmatai.core.data.dto.AdminProviderCreateDto
import com.corestack.khidmatai.core.data.dto.AdminProviderDto
import com.corestack.khidmatai.core.data.dto.AdminRequestDto
import com.corestack.khidmatai.core.data.dto.ApiLocation
import com.corestack.khidmatai.core.domain.model.AdminBooking
import com.corestack.khidmatai.core.domain.model.AdminProvider
import com.corestack.khidmatai.core.domain.model.AdminRequest
import com.corestack.khidmatai.core.domain.model.TraceItem
import com.corestack.khidmatai.core.domain.repository.AdminRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
val BASE_URL = "https://ascertain-antler-poking.ngrok-free.dev"

class ApiAdminRepositoryImpl(
    private val httpClient: HttpClient
) : AdminRepository {

    private fun <T> AdminApiResponse<T>.unwrap(): T {
        if (!success) {
            val errType = error?.type ?: "UnknownError"
            throw Exception("$errType: $message")
        }
        return data ?: throw Exception("Response data is null")
    }

    override suspend fun getAllBookings(): List<AdminBooking> =
        httpClient.get("${BASE_URL}/admin/bookings/")
            .body<AdminApiResponse<List<AdminBookingDto>>>()
            .unwrap()
            .map { it.toDomain() }

    override suspend fun getBookingById(bookingId: String): AdminBooking =
        httpClient.get("${BASE_URL}/admin/bookings/$bookingId")
            .body<AdminApiResponse<AdminBookingDto>>()
            .unwrap()
            .toDomain()

    override suspend fun cancelBooking(bookingId: String): AdminBooking =
        httpClient.post("${BASE_URL}/admin/bookings/$bookingId/cancel")
            .body<AdminApiResponse<AdminBookingDto>>()
            .unwrap()
            .toDomain()

    override suspend fun completeBooking(bookingId: String): AdminBooking =
        httpClient.post("${BASE_URL}/admin/bookings/$bookingId/complete")
            .body<AdminApiResponse<AdminBookingDto>>()
            .unwrap()
            .toDomain()

    override suspend fun updateBookingStatus(bookingId: String, status: String): AdminBooking =
        httpClient.patch("${BASE_URL}/admin/bookings/$bookingId/status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status))
        }.body<AdminApiResponse<AdminBookingDto>>()
            .unwrap()
            .toDomain()

    override suspend fun getAllProviders(): List<AdminProvider> =
        httpClient.get("${BASE_URL}/admin/providers/")
            .body<AdminApiResponse<List<AdminProviderDto>>>()
            .unwrap()
            .map { it.toDomain() }

    override suspend fun createProvider(provider: AdminProvider): AdminProvider {
        val dto = AdminProviderCreateDto(
            name = provider.name,
            serviceType = provider.serviceType,
            rating = provider.rating,
            phone = provider.phone,
            pricePerHour = provider.pricePerHour,
            experienceYears = provider.experienceYears,
            availability = provider.availability,
            location = ApiLocation(
                address = provider.locationAddress,
                lat = 0.0,
                lng = 0.0
            )
        )
        return httpClient.post("${BASE_URL}/admin/providers/") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body<AdminApiResponse<AdminProviderDto>>().unwrap().toDomain()
    }

    override suspend fun updateProvider(providerId: String, provider: AdminProvider): AdminProvider {
        val dto = AdminProviderCreateDto(
            name = provider.name,
            serviceType = provider.serviceType,
            rating = provider.rating,
            phone = provider.phone,
            pricePerHour = provider.pricePerHour,
            experienceYears = provider.experienceYears,
            availability = provider.availability,
            location = ApiLocation(
                address = provider.locationAddress,
                lat = 0.0,
                lng = 0.0
            )
        )
        return httpClient.put("${BASE_URL}/admin/providers/$providerId") {
            contentType(ContentType.Application.Json)
            setBody(dto)
        }.body<AdminApiResponse<AdminProviderDto>>().unwrap().toDomain()
    }

    override suspend fun deleteProvider(providerId: String) {
        val response = httpClient.delete("${BASE_URL}/admin/providers/$providerId")
            .body<AdminApiResponse<kotlinx.serialization.json.JsonElement?>>()
        if (!response.success) {
            val errType = response.error?.type ?: "UnknownError"
            throw Exception("$errType: ${response.message}")
        }
    }

    override suspend fun toggleProviderAvailability(providerId: String): AdminProvider =
        httpClient.patch("${BASE_URL}/admin/providers/$providerId/availability")
            .body<AdminApiResponse<AdminProviderDto>>()
            .unwrap()
            .toDomain()

    override suspend fun getAllRequests(): List<AdminRequest> =
        httpClient.get("${BASE_URL}/admin/requests/")
            .body<AdminApiResponse<List<AdminRequestDto>>>()
            .unwrap()
            .map { it.toDomain() }

    override suspend fun getRequestById(requestId: String): AdminRequest =
        httpClient.get("${BASE_URL}/admin/requests/$requestId")
            .body<AdminApiResponse<AdminRequestDto>>()
            .unwrap()
            .toDomain()
}

private fun AdminBookingDto.toDomain() =
    AdminBooking(
        id = id,
        userId = userId,
        providerId = providerId,
        serviceType = serviceType,
        status = status,
        scheduledAt = scheduledAt,
        address = location.address,
        totalCost = totalCost,
        createdAt = createdAt
    )

private fun AdminProviderDto.toDomain() =
    AdminProvider(
        id = id,
        name = name,
        serviceType = serviceType,
        rating = rating,
        phone = phone,
        pricePerHour = pricePerHour,
        experienceYears = experienceYears,
        availability = availability,
        locationAddress = location.address
    )

private fun AdminRequestDto.toDomain() =
    AdminRequest(
        id = id,
        userId = userId,
        rawQuery = rawQuery,
        urgency = urgency,
        intent = intent,
        language = language,
        status = status,
        bookingId = bookingId,
        trace = trace.map {
            TraceItem(
                it.stage,
                it.message,
                it.status
            )
        },
        createdAt = createdAt
    )
