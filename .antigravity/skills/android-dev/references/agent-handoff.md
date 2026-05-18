# KaamKaro AI (KhidmatAI) - Agent Handoff Reference

When switching between agents or starting a new session, please refer to these critical architectural guidelines and project states to ensure continuity.

## 1. UI & Compose Rules
- **Spacing:** NEVER hardcode `dp` values for padding, margins, or sizes. Always use the globally provided `MaterialTheme.spacing` (e.g., `MaterialTheme.spacing.medium`).
- **Insets:** Ensure all root screens handle system insets to prevent the UI from overlapping with the status bar or navigation bar. Use `modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars)` on the root `Scaffold` or `Column`/`Box`.
- **Material 3:** Stick exclusively to Material 3 components and the custom `AppTheme` colors/typography provided.
- **Images/Icons:** Use Compose's native drawing or text emojis for mocked icons until actual asset SVGs are implemented.

## 2. MVI & State Management Architecture
- **StateFlow Lifecycle:** Always collect state safely in Compose using `collectAsStateWithLifecycle()` from `androidx.lifecycle.compose`, NEVER use the standard `collectAsState()`.
- **Contract Strictness:** Every feature has a `*Contract.kt` file containing:
  - An `@Immutable` data class for `State`.
  - A `sealed interface` (NOT a sealed class) for `Intent`/`Action`.
- **ViewModel Dispatching:** ViewModels should expose an `onAction(action: Intent)` method (do NOT name it `handleIntent`) to process UI events cleanly.

## 3. Dependency Injection & Routing
- **Koin for DI:** Use `@KoinViewModel` for injecting ViewModels. All dependencies are configured in `AppModule.kt`.
- **Environment Targeting:** The application currently supports an `AppEnvironment` system (`DEV` vs `PROD`). When in `DEV` mode, `MockServiceRepositoryImpl` is injected to bypass backend calls and allow rapid UI/UX iteration.
- **Navigation:** Use Compose Navigation with type-safe `@Serializable` data objects. All routes are registered in `Screens.kt` and managed in `App.kt`.

## 4. Current Progress & Next Steps
- **Completed:** Feature slices for Onboarding, Home (Service Request), AI Processing Trace, Success/Failure Results, and Booking Details are structurally complete and fully adhere to UI/MVI rules.
- **Pending:**
  - Implement actual Voice Input in `HomeScreen`.
  - Add mocked Push Notifications overlay in the `Bookings` flow.
  - Integrate with real Ktor backend endpoints (transitioning from `MockServiceRepositoryImpl` to `ApiServiceRepositoryImpl`).
