# Rules — Architecture & Module Structure

## Module Layout

```
:app                          → Android entry point (Application, MainActivity, NavHost)
:core:ui                      → Shared composables, theme, spacing, icons
:core:common                  → Pure Kotlin utilities, extensions, Result wrappers, DomainException
:core:network                 → Ktor client, interceptors, base DTOs
:core:database                → Room setup, DAOs, entities, type converters
:core:testing                 → Shared fakes, TestDispatcher helpers, coroutine test utilities
:feature:<name>:ui            → Screens, composables, MVI contracts, ViewModels
:feature:<name>:domain        → Use cases, domain models, repository interfaces
:feature:<name>:data          → Repository implementations, mappers, remote/local data sources
```

- Each `:feature:*` module is **self-contained**. No feature imports another feature directly.
- Cross-feature communication uses **shared domain models** or **navigation events only**.
- Use `build-logic` (convention plugins) for shared Gradle configuration.

---

## Layer Rules

### Domain Layer — pure Kotlin, zero framework imports

- Contains: use cases, repository interfaces, domain models, domain exceptions.
- Use cases have a **single `operator fun invoke(...)` entry point**.
- Domain models are plain `data class` or sealed hierarchies — never Room entities, never DTOs.
- Repository interfaces return `Flow<T>` for streams, `Result<T>` for one-shot operations.
- No Koin, no Android, no Ktor, no Room imports anywhere in this layer.

```kotlin
// ✅
class GetUserProfileUseCase(private val repo: UserRepository) {
    operator fun invoke(id: String): Flow<UserProfile> = repo.observeProfile(id)
}

// ❌ — use case must never reference Room, Ktor, or Android APIs
```

### Data Layer

- Contains: repository implementations, remote data sources, local data sources, mappers.
- Mappers are **pure top-level or companion-object functions** — never inlined inside repositories.
- All I/O operations use `withContext(ioDispatcher)` where `ioDispatcher` is injected.
- Every external call is wrapped in `runCatching` and mapped to `DomainException` before it leaves the layer.

```kotlin
// ✅ Mappers as top-level functions
fun UserDto.toDomain(): UserProfile = UserProfile(id = id, name = displayName)
fun UserEntity.toDomain(): UserProfile = UserProfile(id = id, name = name)
fun UserProfile.toEntity(): UserEntity = UserEntity(id = id, name = name)
```

### UI Layer

- Contains: screens, composables, ViewModels, MVI contracts.
- ViewModels depend **only on use cases** — never on repositories or data sources directly.
- See `ui-compose.md` for Compose rules, `mvi.md` for ViewModel/state rules.

---

## Error Handling Contract

Define a sealed `DomainException` in `:core:common`. All layers above data only ever see this type:

```kotlin
sealed class DomainException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(cause: Throwable? = null) : DomainException("Network error", cause)
    class ServerException(val code: Int, message: String) : DomainException(message)
    class NotFoundException(resource: String) : DomainException("$resource not found")
    class UnauthorizedException : DomainException("Unauthorized")
    class UnknownException(cause: Throwable? = null) : DomainException("Unknown error", cause)
}
```

- **Data layer** catches raw exceptions and maps them to `DomainException`.
- **ViewModels** handle `Result<T>` and map `DomainException` to user-facing string resources.
- Never let `IOException`, `HttpException`, or Ktor exceptions propagate above the data layer.
