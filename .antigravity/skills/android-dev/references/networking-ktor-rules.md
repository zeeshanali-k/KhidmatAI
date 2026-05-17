# Rules — Networking (Ktor)

## Client Setup

- Use **Ktor Client** with the `OkHttp` engine on Android, `Darwin` on iOS.
- Configure a single `HttpClient` instance as a **Koin `@Single`** in `:core:network`.
- Use `kotlinx.serialization` for all JSON. Never use Gson, Moshi, or Jackson.
- All DTOs are `@Serializable` data classes in `:core:network`.

```kotlin
// core/network/HttpClientFactory.kt
@Single
class HttpClientFactory(
    @Named("isDebug") private val isDebug: Boolean,
) {
    fun create(): HttpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 15_000
            socketTimeoutMillis = 30_000
        }
        install(Logging) {
            level = if (isDebug) LogLevel.BODY else LogLevel.NONE
            logger = object : Logger {
                override fun log(message: String) = Timber.tag("Ktor").d(message)
            }
        }
        defaultRequest {
            url(BuildConfig.BASE_URL)
            contentType(ContentType.Application.Json)
        }
    }
}

@Single
fun provideHttpClient(factory: HttpClientFactory): HttpClient = factory.create()
```

---

## Service Classes

- API calls live in **typed service classes** — never call `client.get(...)` directly inside a repository.
- One service class per domain area (e.g., `UserService`, `ProductService`).

```kotlin
@Single
class UserService(private val client: HttpClient) {
    suspend fun fetchUser(id: String): UserDto =
        client.get("users/$id").body()

    suspend fun updateDisplayName(id: String, name: String): UserDto =
        client.patch("users/$id") {
            setBody(UpdateDisplayNameRequest(name))
        }.body()
}
```

---

## Error Mapping

Every service call must be wrapped in `runCatching` in the **data source**, and the exception mapped to `DomainException` before it leaves the data layer:

```kotlin
@Single
class UserRemoteDataSource(private val service: UserService) {
    suspend fun fetchUser(id: String): Result<UserDto> = runCatching {
        service.fetchUser(id)
    }.mapError { throwable ->
        when (throwable) {
            is ClientRequestException -> when (throwable.response.status) {
                HttpStatusCode.NotFound -> DomainException.NotFoundException("User $id")
                HttpStatusCode.Unauthorized -> DomainException.UnauthorizedException()
                else -> DomainException.ServerException(throwable.response.status.value, throwable.message ?: "")
            }
            is ServerResponseException -> DomainException.ServerException(throwable.response.status.value, throwable.message ?: "")
            is IOException -> DomainException.NetworkException(throwable)
            else -> DomainException.UnknownException(throwable)
        }
    }
}

// Extension to map Result<T> error to DomainException
fun <T> Result<T>.mapError(transform: (Throwable) -> DomainException): Result<T> =
    this.recoverCatching { throw transform(it) }
```

---

## DTOs

```kotlin
// ✅ DTOs are @Serializable and live in :core:network
@Serializable
data class UserDto(
    @SerialName("id") val id: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

// ❌ Never use domain models as DTOs or annotate them with @Serializable
```

---

## Rules Summary

| Rule | Detail |
|---|---|
| Single client | One `HttpClient` singleton provided by Koin |
| Serialization | `kotlinx.serialization` only — no Gson/Moshi |
| Service layer | Typed service classes — no raw client calls in repositories |
| Error mapping | Ktor exceptions → `DomainException` at the data source boundary |
| Engine | `OkHttp` on Android, `Darwin` on iOS (configured in CMP module — see `cmp.md`) |
| Debug logging | `LogLevel.BODY` in debug, `LogLevel.NONE` in release |
