# Rules — Code Style, Conventions & Compose Performance

## Kotlin Conventions

- Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) exactly.
- Max line length: **120 characters**.
- One top-level class/object per file. Exception: closely related sealed subclasses may share a file.
- `sealed interface` is preferred over `sealed class` when subtypes share no state.
- `data object` (not `object`) for singleton sealed subtypes (Kotlin 1.9+).
- Prefer `val` over `var`. Every `var` must be justified with a comment.
- Avoid `!!` (non-null assertion). Use `?: return`, `checkNotNull(...)`, or `requireNotNull(...)` with a descriptive message.
- Use named arguments when calling functions with 3 or more parameters of similar types.

```kotlin
// ✅
val user = checkNotNull(state.profile) { "Profile must be loaded before editing" }

// ❌
val user = state.profile!!
```

---

## Naming

| Element | Convention | Example |
|---|---|---|
| Files | PascalCase matching primary class | `ProfileViewModel.kt` |
| Composables | PascalCase, noun or noun-phrase | `UserAvatarChip`, `ProfileContent` |
| Actions | Sealed interface, verb-noun or noun | `RefreshProfile`, `UpdateDisplayName` |
| Events | Sealed interface, verb-phrase | `NavigateBack`, `ShowSnackbar` |
| Use cases | Verb + noun + `UseCase` | `GetUserProfileUseCase` |
| Repositories | Interface: noun + `Repository`; Impl: `…Impl` | `UserRepository`, `UserRepositoryImpl` |
| DAOs | Noun + `Dao` | `UserDao` |
| Entities | Noun + `Entity` | `UserEntity` |
| DTOs | Noun + `Dto` | `UserDto` |
| Fakes (test) | `Fake` + interface name | `FakeUserRepository` |

---

## Compose Stability

### Annotate state classes

```kotlin
// ✅ Explicitly annotated — Compose skips unnecessary recompositions
@Immutable
data class ProfileState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
)

// ✅ Use ImmutableList for collections in state
@Immutable
data class FeedState(
    val items: ImmutableList<FeedItem> = persistentListOf(),
)

// ❌ Plain List triggers recomposition even when contents haven't changed
data class FeedState(val items: List<FeedItem> = emptyList())
```

- Use `ImmutableList`/`ImmutableMap` from `kotlinx.collections.immutable` for all collection fields in state classes.
- Use `@Stable` on interfaces and classes that are stable but not `data class`.

### derivedStateOf

Use `remember { derivedStateOf { ... } }` for values derived from other state. Never compute inline during composition:

```kotlin
// ✅
val hasUnread by remember { derivedStateOf { items.any { !it.isRead } } }

// ❌ — re-evaluated on every recomposition
val hasUnread = items.any { !it.isRead }
```

### LazyList keys

Always provide `key` in `LazyColumn`/`LazyRow` items:

```kotlin
LazyColumn {
    items(items, key = { it.id }) { item ->
        FeedItemCard(item = item)
    }
}
```

### Modifier lambda deferral

Avoid reading `State` inside `Modifier` calls — defer with a lambda where possible:

```kotlin
// ✅ — offset read is deferred, avoids layout recomposition
Modifier.offset { IntOffset(x = offsetState.value.roundToInt(), y = 0) }

// ❌ — reads state during composition, triggers more recompositions
Modifier.offset(x = offsetState.value.dp)
```

---

## Baseline Profiles

- Add a `BaselineProfile` test in `:app` using `androidx.benchmark:benchmark-macro-junit4`.
- Add `androidx.profileinstaller:profileinstaller` to `:app` for profile installation on first launch.
- Regenerate the baseline profile after any significant UI change.

---

## General Do / Don't

| Do | Don't |
|---|---|
| `internal` visibility by default | `public` unless it's a public API |
| `@Immutable` / `@Stable` on state | Mutable state classes |
| `persistentListOf()` for collections in state | `listOf()` in state classes |
| `Modifier` as last parameter with default | Omit `Modifier` from composable signature |
| `collectAsStateWithLifecycle()` | `collectAsState()` |
| `viewModelScope.launch { }` | `GlobalScope`, custom injected scope |
| `Result<T>` for one-shot repository returns | Throwing exceptions from repository interfaces |
