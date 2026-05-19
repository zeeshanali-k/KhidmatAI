package com.corestack.khidmatai.data.location

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Single

@Single
class NominatimGeocoder(@Provided private val httpClient: HttpClient) {
    suspend fun reverseGeocode(lat: Double, lon: Double): String = try {
        val response = httpClient.get("https://nominatim.openstreetmap.org/reverse") {
            parameter("format", "json")
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("zoom", 15)
            parameter("addressdetails", 1)
            header("User-Agent", "KhidmatAI/1.0")
        }
        val body = response.body<NominatimResponse>()
        val suburb = body.address?.suburb ?: body.address?.neighbourhood
        val city = body.address?.city ?: body.address?.town ?: body.address?.village
        listOfNotNull(suburb, city).joinToString(", ").ifBlank { body.displayName.take(50) }
    } catch (e: Exception) {
        "$lat, $lon"
    }
}

@Serializable
private data class NominatimResponse(
    @SerialName("display_name") val displayName: String = "",
    val address: NominatimAddress? = null
)

@Serializable
private data class NominatimAddress(
    val suburb: String? = null,
    val neighbourhood: String? = null,
    val city: String? = null,
    val town: String? = null,
    val village: String? = null
)
