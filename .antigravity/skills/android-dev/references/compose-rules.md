# Rules — UI, Compose & Theming

## 1 · Material3 Theme

- Use **Material3** (`androidx.compose.material3`) exclusively. Never import Material2.
- Define a single `AppTheme` composable in `:core:ui`.
- Support **dynamic color** on Android 12+ and a manual `darkTheme` override.
- All color, typography, and shape tokens must come from `MaterialTheme` — never hardcode hex values.

```kotlin
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = {
            CompositionLocalProvider(LocalSpacing provides adaptiveSpacing()) {
                content()
            }
        },
    )
}
```

---

## 2 · Spacing System

### 2.1 Spacing class & CompositionLocal

```kotlin
// core/ui/Spacing.kt
data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val screenPadding: Dp = 16.dp,
)

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable @ReadOnlyComposable
    get() = LocalSpacing.current
```

### 2.2 Adaptive spacing based on screen size

Follow the [Material Design adaptive layout guidelines](https://m3.material.io/foundations/layout/applying-layout/window-size-classes). Derive spacing from the current `WindowSizeClass`:

```kotlin
// core/ui/Spacing.kt
@Composable
fun adaptiveSpacing(): Spacing {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return when (windowSizeClass.windowWidthSizeClass) {
        WindowWidthSizeClass.COMPACT ->
            Spacing(screenPadding = 16.dp, medium = 16.dp, large = 24.dp)
        WindowWidthSizeClass.MEDIUM ->
            Spacing(screenPadding = 24.dp, medium = 20.dp, large = 28.dp)
        WindowWidthSizeClass.EXPANDED ->
            Spacing(screenPadding = 32.dp, medium = 24.dp, large = 32.dp)
        else -> Spacing()
    }
}
```

- `adaptiveSpacing()` is called **once** inside `AppTheme` and provided via `LocalSpacing`.  
  All composables downstream automatically receive the correct spacing for the current window class.
- Screen-level composables additionally use `WindowSizeClass` directly for **layout decisions** 
  (e.g., switching between single-pane and two-pane layouts).
- Use `calculateWindowSizeClass()` / `currentWindowAdaptiveInfo()` from `androidx.window:window`.
- Never branch on screen size inside leaf/component composables — only at screen level.

### 2.3 Enforcement

```kotlin
// ✅
Modifier.padding(MaterialTheme.spacing.medium)
Spacer(Modifier.height(MaterialTheme.spacing.small))

// ❌ Never use raw dp literals in composables
Modifier.padding(16.dp)
Spacer(Modifier.height(8.dp))
```

---

## 3 · Composable Structure Rules

### 3.1 Stateful / Stateless split

**Only apply the stateful + stateless pattern when a composable:**
- Lives in **its own dedicated file**, AND
- That file contains **a single primary composable** (so the file can have a self-contained preview).

In all other cases (shared component files, small reusable composables), write a single composable with a `modifier` parameter and default values — do not artificially split it.

```kotlin
// ✅ ProfileScreen.kt — owns a full screen, lives alone in its file
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileContent(state = state, onAction = viewModel::onAction)
}

@Composable
internal fun ProfileContent(
    state: ProfileState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) { /* ... */ }

// ✅ Reusable component in a shared components file — no split needed
@Composable
fun UserAvatarChip(
    name: String,
    avatarUrl: String?,
    modifier: Modifier = Modifier,
) { /* ... */ }
```

### 3.2 Previews

- **Every composable must have at least one `@Preview`.**
- For screen-level composables use `@PreviewLightDark` + `@PreviewFontScale`.
- For component composables a single `@PreviewLightDark` is sufficient.
- Previews always call the **stateless** overload (for screens) or pass hardcoded data directly.
- Wrap all previews in `AppTheme { }`.

```kotlin
@PreviewLightDark
@PreviewFontScale
@Composable
private fun ProfileContentPreview() {
    AppTheme {
        ProfileContent(
            state = ProfileState(profile = previewUserProfile()),
            onAction = {},
        )
    }
}
```

- Define `preview*()` helper functions (e.g., `previewUserProfile()`) in a `PreviewData.kt` file inside the feature's UI module. Annotate them with `@VisibleForTesting`.

### 3.3 General Composable Rules

- Composables accept **stable, immutable state** — never a ViewModel reference.
- Always add a `modifier: Modifier = Modifier` parameter to every composable that renders UI.
- Use `remember + derivedStateOf` for derived/expensive computations — never compute inline during composition.
- Hoist state to the **lowest common ancestor** that needs it.
- Use `LazyColumn`/`LazyRow` for all dynamic lists — never `Column + forEach`.
- Apply `WindowInsets` handling in every screen-level `Scaffold`.
- Avoid `Modifier.fillMaxSize()` on inner/leaf components; apply it only on screen-level containers.

---

## 4 · Navigation

- Use **Jetpack Compose Navigation** (`androidx.navigation:navigation-compose` 2.8+).  
  Do **not** use Navigation3, Voyager, Decompose, or any other navigation library.
- All routes are **type-safe `@Serializable` objects or data classes**.
- A single `NavHost` lives in `:app`. Feature modules expose `NavGraphBuilder.featureNavGraph(navController)`.
- Pass only **primitive scalar arguments** via nav arguments. Fetch complex objects by ID inside the destination ViewModel using `SavedStateHandle`.
- Use `navController.navigate(Route)` — never navigate with raw strings.

```kotlin
// Route definitions
@Serializable data object HomeRoute
@Serializable data class ProfileRoute(val userId: String)

// Feature registration
fun NavGraphBuilder.profileGraph(navController: NavController) {
    composable<ProfileRoute> { ProfileScreen(navController = navController) }
}

// In ViewModel
class ProfileViewModel(
    savedStateHandle: SavedStateHandle,
    private val getProfile: GetUserProfileUseCase,
) : ViewModel() {
    private val userId = savedStateHandle.toRoute<ProfileRoute>().userId
}
```
