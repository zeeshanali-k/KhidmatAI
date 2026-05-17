# Rules — MVI (Model-View-Intent)

Every screen follows a strict MVI contract. The three types always live in a single `<Screen>Contract.kt` file.

---

## 1 · Contract Definition

```kotlin
// ProfileContract.kt
@Immutable
data class ProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val errorMessage: String? = null,
)

sealed interface ProfileAction {
    data object RefreshProfile : ProfileAction
    data class UpdateDisplayName(val name: String) : ProfileAction
    data object DismissError : ProfileAction
}

sealed interface ProfileEvent {
    data object NavigateBack : ProfileEvent
    data class ShowSnackbar(val message: String) : ProfileEvent
}
```

| Type | Purpose | Rule |
|---|---|---|
| `State` | Immutable UI snapshot | Always has sensible defaults; annotate `@Immutable` |
| `Action` | User intents → ViewModel | One-directional; never carry callbacks or lambdas |
| `Event` | One-shot side effects | Navigation, snackbars, dialogs; consumed once via `Channel` |

- Use `sealed interface` (not `sealed class`) when subtypes carry no shared state.
- Use `data object` (not `object`) for singleton sealed subtypes (Kotlin 1.9+).

---

## 2 · ViewModel Rules

```kotlin
@KoinViewModel
class ProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val getUserProfile: GetUserProfileUseCase,
    private val updateDisplayName: UpdateDisplayNameUseCase,
    @Named("io") private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val userId = savedStateHandle.toRoute<ProfileRoute>().userId

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events: Flow<ProfileEvent> = _events.receiveAsFlow()

    init { observeProfile() }

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.RefreshProfile -> observeProfile()
            is ProfileAction.UpdateDisplayName -> updateName(action.name)
            ProfileAction.DismissError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun observeProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getUserProfile(userId)
                .catch { e -> _state.update { it.copy(isLoading = false, errorMessage = e.localizedMessage) } }
                .collect { profile -> _state.update { it.copy(isLoading = false, profile = profile) } }
        }
    }
}
```

### Coroutine scope

- Always use **`viewModelScope`** — it is provided by `ViewModel` and is lifecycle-aware. **Never inject a `CoroutineScope`.**
- **Inject `CoroutineDispatcher`** (e.g., `@Named("io") ioDispatcher: CoroutineDispatcher`) so that unit tests can substitute a `TestDispatcher` without touching the scope itself. Use `withContext(ioDispatcher)` for I/O work triggered from the ViewModel.
- All `viewModelScope.launch { }` calls must handle errors — either via `.catch { }` on flows or `try/catch` on suspend functions.

### State & Events

- **`StateFlow`** for UI state — never `LiveData`, never plain `MutableState` at ViewModel level.
- **`Channel<Event>(Channel.BUFFERED)`** for one-shot side effects — never `SharedFlow` for events (risks replay issues).
- Use `_state.update { ... }` (atomic) — never `_state.value = _state.value.copy(...)`.

### What ViewModels must NOT do

- Import `android.content.Context` or any View/Compose reference.
- Import anything from the data layer (repositories, DAOs, DTOs).
- Contain business logic — delegate all logic to use cases.
- Expose mutable state directly (`MutableStateFlow` must be private).

---

## 3 · UI — Collecting State & Events

```kotlin
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // One-shot events
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        viewModel.events.flowWithLifecycle(lifecycleOwner.lifecycle).collect { event ->
            when (event) {
                ProfileEvent.NavigateBack -> navController.popBackStack()
                is ProfileEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    ProfileContent(state = state, onAction = viewModel::onAction)
}
```

- Always use **`collectAsStateWithLifecycle()`** — never `collectAsState()`.
- Collect events inside `LaunchedEffect` with `flowWithLifecycle` to respect the lifecycle.
- Pass `viewModel::onAction` as a single lambda — never pass the ViewModel itself into composables.
