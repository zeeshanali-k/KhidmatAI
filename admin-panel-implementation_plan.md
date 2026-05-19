# Admin Panel Implementation Plan

## Goal Description
Add a **web-based Admin Panel** to the KhidmatAI Kotlin Multiplatform (KMP) project. 
The admin panel will be a new `wasmJs` Compose Multiplatform module (`:adminWebApp`). To maximize code reuse and maintain consistency, the shared business logic (domain models, repositories, network DTOs) currently in the `:shared` module will be extracted into a new `:core` module. Both the mobile app (`:shared`) and the web admin (`:adminWebApp`) will then depend on `:core`. Additionally, the backend will be updated to support admin operations.

## User Review Required
> [!IMPORTANT]
> **Admin Authentication:** As per previous instructions, authentication is currently ignored. However, in a production environment, all admin routes must be secured with an admin-role JWT. This should be added once the auth system is finalized.
>
> **Database:** The backend currently uses an in-memory `mock_db.py`. Any providers created or requests logged via the admin panel will only persist until the backend server restarts.

## Open Questions
> [!WARNING]
> **Theme Location:** Should the UI theme files (`Color.kt`, `Theme.kt`, `AppStrings.kt`) be moved to `:core` so the admin panel can share them directly, or should the admin panel define its own admin-specific theme? Moving them to `:core` ensures consistency but adds Compose UI dependencies to the core module (which is acceptable since it targets `wasmJs`).

## Proposed Changes

### 1. Refactor: Extract `:core` Module

Create a new platform-agnostic `:core` module that can compile for Android, iOS, and wasmJs.

#### [NEW] `core/build.gradle.kts`
A new Gradle build file configuring the `core` module to support `androidLibrary`, `iosArm64`, `iosSimulatorArm64`, and `wasmJs`. It will include common dependencies like Ktor, Koin, Serialization, and Settings.

#### [MODIFY] Move Shared Logic to `:core`
Move the following directories/files from `:shared` to `:core` without changing their package names:
- `data/dto/` (API and Auth DTOs)
- `data/repository/` (API and Mock repository implementations)
- `domain/model/` (Service, Auth, and Location models)
- `domain/repository/` (Repository interfaces)
- `domain/AppEnvironment.kt`
- `domain/preferences/AppPreferences.kt`
- `di/AppModule.kt` and `di/CoreAppModule.kt`
- `data/location/` (Keep interfaces; platform implementations stay in `:shared`)

#### [MODIFY] `:shared` Dependencies
Update `shared/build.gradle.kts` to add `api(project(":core"))` so the mobile app can access the extracted logic.

### 2. Add Admin Domain Logic to `:core`

#### [NEW] `domain/model/AdminModels.kt`
Define admin-specific models:
- `AdminBooking`: Detailed booking info including user and provider IDs.
- `AdminProvider`: Service provider details for management.
- `AdminRequest`: Request logs including intent, trace, and status.

#### [NEW] `domain/repository/AdminRepository.kt`
Define the repository interface for admin operations:
- **Bookings:** `getAllBookings()`, `cancelBooking()`, `completeBooking()`
- **Providers:** `getAllProviders()`, `createProvider()`, `updateProvider()`, `deleteProvider()`, `toggleProviderAvailability()`
- **Requests:** `getAllRequests()`, `getRequestById()`

#### [NEW] `data/repository/ApiAdminRepositoryImpl.kt`
Implement the `AdminRepository` interface using Ktor to call the new backend admin endpoints.

### 3. Create `:adminWebApp` Module

Create a new Compose Multiplatform web module for the admin UI.

#### [NEW] `adminWebApp/build.gradle.kts`
Configure the module for the `wasmJs` target and add dependencies on `:core` and Compose Multiplatform.

#### [NEW] `adminWebApp/src/wasmJsMain/kotlin/com/corestack/khidmatai/admin/main.kt`
The entry point for the web app, initializing Koin and the `ComposeViewport`.

#### [NEW] Admin UI Screens and ViewModels
- **Dashboard (`DashboardScreen.kt`, `DashboardViewModel.kt`):** Summary stats (total bookings, active providers, recent requests).
- **Requests (`RequestsScreen.kt`, `RequestDetailScreen.kt`):** List of all requests and a detail view showing the live trace and assigned booking.
- **Bookings (`BookingsScreen.kt`, `BookingDetailScreen.kt`):** List of all bookings with filtering, and a detail view allowing admins to mark as complete or cancel.
- **Providers (`ProvidersScreen.kt`, `ProviderFormScreen.kt`):** Grid of providers with options to add, edit, delete, or toggle availability.

### 4. Backend Admin API Updates (FastAPI)

Update the Python backend to support admin functionalities.

#### [NEW] `routers/admin.py`
Create a new router with endpoints for:
- **Bookings:** `GET /admin/bookings/`, `GET /admin/bookings/{booking_id}`, `POST /admin/bookings/{booking_id}/complete`, `POST /admin/bookings/{booking_id}/cancel`
- **Providers:** `GET /admin/providers/`, `POST /admin/providers/`, `PUT /admin/providers/{provider_id}`, `DELETE /admin/providers/{provider_id}`
- **Requests:** `GET /admin/requests/`, `GET /admin/requests/{request_id}`

#### [MODIFY] `db/mock_db.py`
Add an in-memory `request_logs` list and methods (`log_request`, `get_all_request_logs`) to track request history.

#### [MODIFY] `routers/requests.py`
Instrument the `POST /requests/` endpoint to log the request details and trace into the new `request_logs`.

#### [MODIFY] `schemas/models.py`
Add `ProviderCreate` and `AdminRequestLog` Pydantic models.

## Verification Plan

### Automated Tests
- The backend will not have automated tests added in this phase, but we will ensure FastAPI starts correctly without syntax errors.
- Gradle sync and build will verify that the module refactoring (`:core` extraction and `:adminWebApp` creation) compiles successfully across all targets (Android, iOS, wasmJs).

### Manual Verification
- Start the FastAPI backend and use the `/docs` (Swagger UI) to verify the new `/admin` endpoints function correctly using the mock DB.
- Run the `adminWebApp` module (`./gradlew :adminWebApp:wasmJsBrowserRun`) and verify the web interface loads in the browser.
- Verify that the mobile app (`androidApp` or `iosApp`) still builds and runs correctly after the shared logic extraction to the `:core` module.
