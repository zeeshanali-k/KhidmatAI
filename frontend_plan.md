# Frontend Implementation & Improvement Plan

## 1. Current State Assessment
The backend currently exposes a synchronous `POST /requests/` endpoint that expects a natural language query, location, and user ID. It eventually returns a deeply nested JSON object containing a `trace` of actions, booking details, and follow-up steps. However, to support a smooth, premium "live progress" UX, the frontend needs to adapt to streaming data.

## 2. Step-by-Step Implementation Guide

### Phase 1: Integrate Real-time Progress Tracking (Live Progress)
Currently, the frontend would hang while waiting for the synchronous backend endpoint. Once the backend implements the `POST /requests/stream` (SSE) endpoint, the frontend needs to handle it.

1. **Implement SSE Client (Server-Sent Events)**:
   - Use Ktor Client (or a relevant Kotlin Multiplatform SSE library) to consume the streaming endpoint.
   - Read chunks of JSON representing trace events (e.g., "intent_understanding completed", "provider_search started").
2. **Build the Live Progress UI**:
   - Create a dynamic UI component (e.g., a vertical stepper or animated list) that updates in real-time.
   - Map backend trace steps (`intent`, `discovery`, `ranking`, `booking_execution`) to user-friendly UI states:
     - 🟡 "Analyzing your request..."
     - 🟡 "Locating providers near you..."
     - 🟡 "Selecting the best match..."
     - 🟢 "Booking Confirmed!"
3. **State Management**:
   - Update your MVI State/ViewModel to append trace events to a list as they arrive, triggering UI recomposition.

### Phase 2: Processing the Final Orchestrator Response
Once the stream finishes or the final response payload is received, the UI must adapt to show the results.

1. **Handle Success Flow**:
   - Parse the `appointment`, `provider`, and `followup` objects from the final JSON.
   - Display the matched provider's details (Name, Rating, Distance) and the scheduled appointment time.
2. **Handle 'No Provider' Flow**:
   - Parse the `error` and `next_steps` array when `status` is `unavailable`.
   - Render the fallback buttons as defined by the backend's `next_steps` (e.g., "Dobara try karein", "Notifications on karein").
3. **Render Dynamic Action Buttons**:
   - The backend returns an array of `next_steps` with types like `phone_call`, `info`, `button`. 
   - Create a dynamic Compose UI renderer that loops through `next_steps` and displays standard buttons or info cards based on the `action_type`.

### Phase 3: Implement Supporting Screens
1. **Location Services Integration**:
   - The backend strictly requires `lat` and `lng`. Ensure the frontend actively fetches device location (with permissions) before hitting the `/requests/` endpoint.
2. **Booking History / Active Bookings Screen**:
   - Implement a screen that lists past and current bookings (requires new backend endpoint `GET /bookings/{user_id}`).
3. **Booking Detail Screen**:
   - Include a button to manually trigger the `POST /bookings/{booking_id}/complete` endpoint when a job is done.

### Phase 4: UI/UX Polish & Error Handling
1. **Network Resilience**:
   - Handle timeout errors gracefully. If the AI processing takes too long, show a reassuring message ("Still searching for the best option...").
2. **Micro-animations**:
   - Add pulsing or shimmering effects to the Live Progress UI to assure the user that the app is actively working, not frozen.
