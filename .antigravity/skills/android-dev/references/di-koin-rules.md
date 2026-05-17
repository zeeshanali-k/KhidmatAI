# Rules — Dependency Injection (Koin with Annotations)

## Setup

- Use **Koin** with `koin-annotations`. Use KSP for code generation (never `kapt`).
- Approved annotations: `@Single`, `@Factory`, `@KoinViewModel`, `@Module`, `@ComponentScan`, `@Named`.
- Each feature module declares its own `@Module` class. `:app` collects all modules via `KoinApplication`.

---

## Module Declaration

```kotlin
// feature/profile/data/ProfileDataModule.kt
@Module
@ComponentScan("com.example.feature.profile.data")
class ProfileDataModule

// feature/profile/ui/ProfileUiModule.kt
@Module
@ComponentScan("com.example.feature.profile.ui")
class ProfileUiModule
```

- Use `@ComponentScan` to auto-discover annotated classes within a package — avoid manual `includes`.
- Core modules (network, database, dispatchers) live in `:core:*` and are included once at app level.

---

## Dispatcher Bindings

Provide named dispatchers as singletons in `:core:common`. This is the **only** dispatcher-related Koin config needed:

```kotlin
@Module
class DispatchersModule {
    @Single @Named("io")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Single @Named("default")
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Single @Named("main")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main.immediate
}
```

Inject by qualifier wherever a dispatcher is needed:

```kotlin
@Single
class UserRepositoryImpl(
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
    ...
) : UserRepository
```

> **Note:** `viewModelScope` is used inside ViewModels for launching coroutines. Dispatchers are injected only for `withContext(...)` calls, not to replace the scope.

---

## ViewModel Registration

```kotlin
// ✅ Annotation-driven — no manual DSL
@KoinViewModel
class ProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val getUserProfile: GetUserProfileUseCase,
) : ViewModel()

// ❌ Never register ViewModels manually
val module = module { viewModel { ProfileViewModel(get(), get()) } }
```

---

## Repository & Use Case Binding

```kotlin
// ✅ Bind implementation to interface via annotation
@Single(binds = [UserRepository::class])
class UserRepositoryImpl(
    private val remote: UserRemoteDataSource,
    private val local: UserLocalDataSource,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
) : UserRepository

// ✅ Use cases are @Factory (new instance per injection)
@Factory
class GetUserProfileUseCase(private val repo: UserRepository)
```

---

## App-Level Wiring

```kotlin
// app/src/main/kotlin/.../App.kt
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                DispatchersModule().module,
                NetworkModule().module,
                DatabaseModule().module,
                ProfileDataModule().module,
                ProfileUiModule().module,
                // ... other feature modules
            )
        }
    }
}
```

---

## Rules Summary

| Rule | Detail |
|---|---|
| Annotation-driven | Use `@Single`, `@Factory`, `@KoinViewModel` — avoid manual DSL for annotated classes |
| No scope injection | Inject `CoroutineDispatcher`, never `CoroutineScope` |
| One module per layer | Separate `@Module` for data and ui layers of each feature |
| KSP only | Never add `kapt` — configure Room, Koin compiler via KSP |
| `@Named` for multibindings | Use `@Named("io")` / `@Named("default")` for multiple dispatcher bindings |
