# Rules — Compose Multiplatform (CMP)

## Core Principle

**Prefer interfaces in `commonMain` over `expect`/`actual`.**

`expect`/`actual` creates tight coupling between platforms, makes refactoring harder, and causes subtle issues when Android `Context` or iOS platform APIs leak into shared code. Reserve `expect`/`actual` for cases where the Kotlin compiler genuinely requires it (e.g., platform-specific type aliases or constructs with no interface equivalent).

---

## Preferred Pattern: Interface + Koin

Define a `common` interface and inject the platform-specific implementation via Koin:

```kotlin
// commonMain — define the contract
interface PlatformInfo {
    val osName: String
    val osVersion: String
}

interface FileStorage {
    suspend fun readFile(path: String): ByteArray
    suspend fun writeFile(path: String, bytes: ByteArray)
}

// androidMain — provide implementation
class AndroidPlatformInfo : PlatformInfo {
    override val osName = "Android"
    override val osVersion = Build.VERSION.RELEASE
}

@Module
class AndroidPlatformModule {
    @Single(binds = [PlatformInfo::class])
    fun providePlatformInfo(): PlatformInfo = AndroidPlatformInfo()
}

// iosMain — provide implementation
class IosPlatformInfo : PlatformInfo {
    override val osName: String = UIDevice.currentDevice.systemName()
    override val osVersion: String = UIDevice.currentDevice.systemVersion
}

@Module
class IosPlatformModule {
    @Single(binds = [PlatformInfo::class])
    fun providePlatformInfo(): PlatformInfo = IosPlatformInfo()
}
```

---

## When `expect`/`actual` Is Acceptable

Use `expect`/`actual` only when:
1. You need a **platform type alias** (e.g., `typealias Context`).
2. You need a **top-level function** that cannot be expressed as an interface method (e.g., `getPlatformHttpEngine()`).
3. An official Jetpack/KMP library already requires it (e.g., `SQLiteDriver`).

```kotlin
// ✅ Acceptable — Ktor engine has no interface-based alternative
// commonMain
expect fun httpEngine(): HttpClientEngine

// androidMain
actual fun httpEngine(): HttpClientEngine = OkHttp.create()

// iosMain
actual fun httpEngine(): HttpClientEngine = Darwin.create()
```

Keep `expect`/`actual` declarations **small and at the boundary** — never put business logic inside them.

---

## Ktor Client (CMP)

Provide a platform-specific `HttpClientEngine` and consume it in `commonMain`:

```kotlin
// commonMain
@Single
fun provideHttpClient(
    engine: HttpClientEngine,  // injected from platform module
    factory: HttpClientFactory,
): HttpClient = factory.create(engine)

// androidMain Koin module
@Single
fun provideEngine(): HttpClientEngine = OkHttp.create()

// iosMain Koin module
@Single
fun provideEngine(): HttpClientEngine = Darwin.create()
```

---

## Room on CMP

- Use Room on both Android and iOS with `androidx.sqlite:sqlite-bundled` for the iOS SQLite driver.
- Provide the `RoomDatabase` builder via a `commonMain` factory function; platform modules supply the builder path:

```kotlin
// commonMain
expect fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<AppDatabase>

// androidMain
actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder(context as Context, AppDatabase::class.java, "app.db")

// iosMain
actual fun getDatabaseBuilder(context: Any?): RoomDatabase.Builder<AppDatabase> =
    Room.databaseBuilder<AppDatabase>(
        name = NSHomeDirectory() + "/app.db",
        factory = { AppDatabase::class.instantiateImpl() },
    )
```

---

## Shared UI Rules

- Never use `LocalContext` in shared (`commonMain`) composables — abstract context-dependent operations behind an injected interface.
- Never reference Android `R.drawable` or `R.string` in `commonMain` — use multiplatform resource declarations (`compose-multiplatform` resources API).
- Use the **Compose Multiplatform resources API** (`Res.string.*`, `Res.drawable.*`) for all shared assets.

```kotlin
// ✅ Multiplatform resources
Text(stringResource(Res.string.profile_title))
Image(painterResource(Res.drawable.ic_avatar))

// ❌ Android-only resources in commonMain
Text(stringResource(R.string.profile_title))
```

---

## Module Source Sets

```
src/
  commonMain/   — shared business logic, domain, shared UI, interfaces
  androidMain/  — Android-specific implementations, Activity, Context usage
  iosMain/      — iOS-specific implementations, UIKit interop
  commonTest/   — shared unit tests (use cases, mappers, fakes)
  androidTest/  — Android-specific integration tests (Room migration, etc.)
```

---

## Rules Summary

| Rule | Detail |
|---|---|
| Interfaces over `expect`/`actual` | Default to interfaces + Koin injection for platform differences |
| `expect`/`actual` scope | Only for platform type aliases, engine factories, or library requirements |
| No `LocalContext` in common | Use injected interfaces to wrap context-dependent APIs |
| CMP resources | Use `Res.*` API — never `R.*` in `commonMain` |
| Ktor engine | Platform-specific engine injected into a common `HttpClient` factory |
| Room on iOS | Use `sqlite-bundled` driver via `expect`/`actual` builder factory |
