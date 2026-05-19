package com.corestack.khidmatai.core.data.repository

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
import org.koin.core.annotation.Single

private const val BASE_URL = "http://localhost:8000"

@Single(binds = [AdminRepository::class])
class ApiAdminRepositoryImpl(
    private val httpClient: HttpClient
) : AdminRepository {

    override suspend fun getAllBookings(): List<AdminBooking> =
        httpClient.get("${BASE_URL}/admin/bookings/").body<List<AdminBookingDto>>().map { it.toDomain() }

    override suspend fun getBookingById(bookingId: String): AdminBooking =
        httpClient.get("${BASE_URL}/admin/bookings/$bookingId").body<AdminBookingDto>().toDomain()

    override suspend fun cancelBooking(bookingId: String): AdminBooking =
        httpClient.post("${BASE_URL}/admin/bookings/$bookingId/cancel").body<AdminBookingDto>().toDomain()

    override suspend fun completeBooking(bookingId: String): AdminBooking =
        httpClient.post("${BASE_URL}/admin/bookings/$bookingId/complete").body<AdminBookingDto>().toDomain()

    override suspend fun getAllProviders(): List<AdminProvider> =
        httpClient.get("${BASE_URL}/admin/providers/").body<List<AdminProviderDto>>().map { it.toDomain() }

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
        }.body<AdminProviderDto>().toDomain()
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
        }.body<AdminProviderDto>().toDomain()
    }

    override suspend fun deleteProvider(providerId: String) {
        httpClient.delete("${BASE_URL}/admin/providers/$providerId")
    }

    override suspend fun toggleProviderAvailability(providerId: String): AdminProvider =
        httpClient.patch("${BASE_URL}/admin/providers/$providerId/availability").body<AdminProviderDto>().toDomain()

    override suspend fun getAllRequests(): List<AdminRequest> =
        httpClient.get("${BASE_URL}/admin/requests/").body<List<AdminRequestDto>>().map { it.toDomain() }

    override suspend fun getRequestById(requestId: String): AdminRequest =
        httpClient.get("${BASE_URL}/admin/requests/$requestId").body<AdminRequestDto>().toDomain()
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
