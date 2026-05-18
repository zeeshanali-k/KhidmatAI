# KaamKaro - AI Service Orchestrator Implementation Plan

This document outlines the step-by-step implementation plan for the KaamKaro (KhidmatAI) application. The project targets Android and iOS using Compose Multiplatform (KMP), adhering to Clean Architecture principles and using Koin for Dependency Injection. The strategy is feature-sliced (vertical slices), implementing UI and Logic together for each screen, supported by a Dev/Prod environment configuration.

## User Review Required

> [!IMPORTANT]
> The plan has been updated to follow a **Feature-by-Feature** implementation strategy.
> It also introduces a **Dev/Prod Environment Switch** to easily inject mock data during development and testing. 
> 
> Please review this updated structure to ensure it aligns with your expectations before we proceed with the execution phase.

## Environment Strategy

To support rapid testing and demonstration without relying on live APIs, we will implement an environment configuration:
- **`AppEnvironment` Enum**: Defines `DEV` and `PROD`.
- **Koin Injection**: We will inject different repository implementations based on the active environment.
  - `DEV`: Injects `MockServiceRepositoryImpl` which returns the dummy data outlined in the design spec, with built-in delays to simulate network and agent reasoning.
  - `PROD`: Injects `ApiServiceRepositoryImpl` (Ktor) to hit the actual Antigravity backend endpoints.

## Proposed Changes (Feature-by-Feature Implementation)

### Phase 1: Core Foundation & Environment Setup
Establish the base models, theming, and the dependency injection environment.

- **Domain Models**: Create `ServiceResult`, `Provider`, `Appointment`, `TraceItem`, `NextStep`, and `AiOrbState` in `domain/model/`.
- **Repository Interface**: Create `ServiceRepository` interface.
- **Environment Setup**: 
  - Create `AppEnvironment.kt`.
  - Implement `MockServiceRepositoryImpl` (Dev) and `ApiServiceRepositoryImpl` (Prod).
  - Update `di/AppModule.kt` to inject the correct repository based on the environment flag.
- **Theming**: Implement `Color.kt` and `Type.kt` based on the design spec (Primary, Success, Warning, Error colors).

### Phase 2: Onboarding Feature
Implement the first-run experience and location permission flow.

- **UI**: Create `OnboardingScreen.kt` with the specified layout and illustrations.
- **Logic**: Implement basic state to handle location permission "Granted", "Denied", and "Skip" actions.
- **Navigation**: Setup `navigation-compose` in `App.kt` and define the route for Onboarding -> Home.

### Phase 3: Home Feature (Service Request Input)
Build the primary input interface where the user submits their service needs.

- **Components**: Create `AiOrbView` (idle/thinking states), `StatusBadge`, and the `BottomNavBar`.
- **UI**: Create `HomeScreen.kt` with text input, quick service chips, location row, and urgency selector.
- **Logic**: Create `ServiceRequestViewModel` handling MVI intents (Form Input, Quick Chip Click, Urgency Change, Submit). Hook it to the injected `ServiceRepository`.
- **Voice Input Modal**: Create `VoiceInputScreen.kt` as a mock overlay that auto-fills text after a simulated recording.

### Phase 4: Processing Feature (Live Agent Trace)
Implement the core visual orchestration element: the simulated reasoning trace.

- **Components**: Create `TraceRowComponent` to handle individual log lines and status icons.
- **UI**: Create `ProcessingScreen.kt`.
- **Logic & Animation**: Use `ServiceRequestViewModel` to observe the trace flow. Implement staggered reveal animations (350ms per stage) to show the agent thinking, ending with the full-screen confetti burst on completion. Implement "Emergency Mode" (red tint, faster animations).

### Phase 5: Results & Follow-Up Features
Display the outcome of the agent's work.

- **Components**: Create `NextStepCard` to handle different follow-up actions (info, warning, action).
- **UI**: 
  - Create `ResultSuccessScreen.kt`: Prominent AI Decision card, Provider Card, Appointment Details, and Next Steps.
  - Create `ResultUnavailableScreen.kt`: Empty state illustration and retry logic.
- **Logic**: Map the `ServiceResult` object from the ViewModel to these screens. Implement button intents (mock calling, navigation).

### Phase 6: Bookings & Details Features
Implement the post-booking experience and historical data.

- **UI**:
  - Create `BookingsScreen.kt`: Filter tabs and a `LazyColumn` list of mock bookings using a new `BookingListItem` component.
  - Create `BookingDetailScreen.kt`: Detailed view including the interactive **Agent Trace Accordion** and Follow-up info card.
- **Logic & Demo Polish**: 
  - Wire up navigation for these screens.
  - Create `MockPushNotification` overlay to demonstrate the follow-up reminder triggered 3 seconds after success.

## Verification Plan

### Manual Verification
1. **Environment Switch**: Set environment to `DEV`, verify mock data loads. Set to `PROD`, verify it attempts to make real API calls (or fails gracefully).
2. **Onboarding -> Home**: Test navigation and basic flow.
3. **Execution Pipeline**: Submit a request -> Verify Processing Screen staggering -> Verify Success Screen displays accurate data and AI decision reasoning.
4. **Emergency Flow**: Select 'Emergency' on Home -> Verify UI shifts to red styling and processing is faster.
5. **Details**: Open Booking Details -> Verify the trace accordion expands correctly to show reasoning logs.
