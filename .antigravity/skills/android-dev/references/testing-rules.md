# Rules — Unit Testing

> UI/instrumentation tests are **not required** by default. Add them only when explicitly requested.

---

## What to Test (Mandatory)

| Layer | What to test |
|---|---|
| **Domain** | Every use case — happy path, error paths, edge cases |
| **Data** | Repository implementations with faked data sources; all mapper functions with non-trivial logic |
| **UI (ViewModel)** | Every `onAction` branch, all state transitions, all `Event` emissions |

---

## Libraries

| Library | Purpose |
|---|---|
| `junit-jupiter` (JUnit 5) | Test runner |
| `MockK` | Mocking — never Mockito |
| `kotlinx-coroutines-test` | `runTest`, `TestDispatcher`, `advanceUntilIdle` |
| `turbine` | Flow / StateFlow assertion |
| `kotlin.test` | Assertions (multiplatform-compatible) |

---

## Fakes Over Mocks

For **repository interfaces**, write a `Fake` implementation instead of mocking:

```kotlin
// core/testing/FakeUserRepository.kt
class FakeUserRepository : UserRepository {
    private val _profiles = MutableSharedFlow<UserProfile>(replay = 1)

    fun emit(profile: UserProfile) { _profiles.tryEmit(profile) }
    fun emitError(exception: DomainException) { /* ... */ }

    override fun observeProfile(id: String): Flow<UserProfile> = _profiles
    override suspend fun updateProfile(profile: UserProfile): Result<Unit> = Result.success(Unit)
}
```

- Fakes live in `:core:testing` and are only accessible via `testImplementation`.
- Reserve `mockk { }` for simple one-off collaborators where a full fake is overkill.

---

## ViewModel Tests

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeRepo = FakeUserRepository()
    private val getProfile = GetUserProfileUseCase(fakeRepo)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun buildViewModel() = ProfileViewModel(
        savedStateHandle = SavedStateHandle(mapOf("userId" to "user-1")),
        getUserProfile = getProfile,
        ioDispatcher = testDispatcher,
    )

    @Test
    fun `given valid user, when init, then state contains profile`() = runTest {
        fakeRepo.emit(previewUserProfile())
        val vm = buildViewModel()

        vm.state.test {
            val state = awaitItem()
            assertEquals(previewUserProfile(), state.profile)
            assertFalse(state.isLoading)
        }
    }

    @Test
    fun `given network error, when RefreshProfile, then state contains error message`() = runTest {
        fakeRepo.emitError(DomainException.NetworkException())
        val vm = buildViewModel()
        vm.onAction(ProfileAction.RefreshProfile)

        vm.state.test {
            val state = awaitItem()
            assertNotNull(state.errorMessage)
        }
    }

    @Test
    fun `when NavigateBack event, then event is emitted`() = runTest {
        val vm = buildViewModel()
        vm.onAction(ProfileAction.NavigateBack)

        vm.events.test {
            assertEquals(ProfileEvent.NavigateBack, awaitItem())
        }
    }
}
```

---

## Use Case Tests

```kotlin
class GetUserProfileUseCaseTest {
    private val repo = FakeUserRepository()
    private val useCase = GetUserProfileUseCase(repo)

    @Test
    fun `given valid id, when invoked, then emits profile`() = runTest {
        repo.emit(previewUserProfile())

        useCase("user-1").test {
            assertEquals(previewUserProfile(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `given error, when invoked, then flow throws DomainException`() = runTest {
        repo.emitError(DomainException.NotFoundException("User"))

        useCase("bad-id").test {
            assertTrue(awaitError() is DomainException.NotFoundException)
        }
    }
}
```

---

## Mapper Tests

Test every non-trivial mapper that transforms between layers:

```kotlin
class UserMappersTest {
    @Test
    fun `UserDto maps to UserProfile correctly`() {
        val dto = UserDto(id = "1", displayName = "Alice", avatarUrl = null)
        val domain = dto.toDomain()
        assertEquals("1", domain.id)
        assertEquals("Alice", domain.name)
        assertNull(domain.avatarUrl)
    }
}
```

---

## Naming Convention

Use backtick descriptive names following `given_when_then` structure:

```
`given <precondition>, when <action>, then <expectation>`
```

---

## What NOT to Test

- Composables (don't write UI/screenshot tests unless asked).
- Data classes and value objects with no logic.
- Koin module configuration (covered by integration tests if needed).
- Mapper functions that are trivially a 1:1 field assignment with no branching.
