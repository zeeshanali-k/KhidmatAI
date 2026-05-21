# Frontend Implementation Plan: AI Service Orchestrator

This plan outlines the architecture, screens, and API integrations required for the frontend mobile application to successfully consume the AI Service Orchestrator backend and fulfill the hackathon requirements.

## User Review Required

> [!IMPORTANT]
> **Tech Stack Decision:** Which framework is the frontend team using? (e.g., React Native with Expo, Flutter, native Android/iOS, or a Progressive Web App?) The integration steps below are conceptually identical, but specific libraries (like Firebase SDKs or SSE clients) will differ.

> [!WARNING]
> **Maps & Geolocation:** The backend relies heavily on `lat`/`lng` coordinates for provider matching. The mobile app MUST request location permissions from the user and send real coordinates to the backend for accurate matching.

---

## 1. App Architecture & State

### Recommended App Flow
1. **Onboarding / Login** -> 2. **Home Screen** (AI Chat / Voice Input) -> 3. **Streaming State** (Progress UI) -> 4. **Provider Selection** (Shortlist) -> 5. **Booking Confirmed** -> 6. **Booking Tracking & FCM**.

### Core State Management Needs
- **User Session:** JWT Token storage (SecureStore / SharedPreferences)
- **FCM Token:** Device token for push notifications
- **Current Location:** Latitude, Longitude, and resolved Address
- **Active Request ID:** To allow cancellations and track the current AI workflow
- **SSE Stream Data:** Real-time updates from LangGraph

---

## 2. API Integration Strategy

### 2.1 Authentication & Initialization
When the app launches or the user logs in:
1. Call `POST /token` to get the JWT access token.
2. Initialize the Firebase SDK on the client device.
3. Retrieve the device's FCM token.
4. Call `POST /notifications/register` with `{"user_id": "...", "fcm_token": "..."}`.

### 2.2 The AI Booking Experience (The Core Hackathon Feature)
The user types or speaks a request (e.g., "Mujhe kal subah AC technician chahiye").

1. **Trigger Request:** Call `POST /requests/stream`.
2. **Listen to SSE:** Use a library like `react-native-sse` or Flutter's `http` streaming. 
   - You will receive events like `{"event": "step_start", "stage": "intent_detection"}`.
   - **UI Action:** Show a beautiful, dynamic progress UI (e.g., "Understanding your request...", "Searching for providers nearby...", "Analyzing best matches..."). Do NOT show a standard loading spinner; the SSE stream is specifically built to "Wow" the judges with AI transparency.
3. **Provider Shortlist:** If the event `provider_shortlist` is received:
   - **UI Action:** Pause the loading state. Display the top 3 providers as cards (showing Name, Rating, Distance, Price, and AI Reasoning).
   - Let the user tap one.
   - Call `POST /requests/{request_id}/select` with the chosen `provider_id`.
4. **Completion:** When the event `booking_ready` or `step_complete` for `followup` arrives, transition to the Success Screen.

### 2.3 Booking Tracking & Push Notifications (FCM)
The backend now supports AI-generated contextual follow-ups triggered by the admin panel.

1. **Background Listener:** Register a background FCM handler.
2. **Foreground Listener:** Register a foreground FCM listener to show in-app banners.
3. **Rich Data:** The FCM payload will contain a `data.followup_actions` JSON string.
   - **UI Action:** Parse this JSON and display the actionable cards.
   - Example: If the status is `CONFIRMED` for an Electrician, show a card: **"Safety Tip: Please switch off the main breaker before the provider arrives."**

---

## 3. UI/UX Screen Breakdown

### Screen 1: Home / AI Assistant
- **Design:** Clean, minimalist chat interface or a large search bar with a prominent microphone icon (for voice-to-text).
- **Functionality:** Capture the raw query. Grab GPS location before hitting the `POST /requests/stream` endpoint.

### Screen 2: Orchestration Loading (The "Magic" Screen)
- **Design:** Dynamic steps lighting up as the LangGraph state machine progresses.
- **Data Source:** The SSE `message` and `stage` fields.

### Screen 3: Provider Shortlist
- **Design:** A sleek carousel or list of 2-3 provider cards.
- **Highlight:** Emphasize the `reasoning` field returned by the AI (e.g., "Chosen because of high 4.9 rating and 2km proximity").

### Screen 4: Booking Confirmed & Next Steps
- **Design:** Receipt-style layout with a map snippet of the user's location.
- **Content:** Show the Scheduled Time, Provider Info, and the `next_steps` array returned by the API (e.g., "Call Provider", "Set Reminder").

### Screen 5: Push Notification Action View
- **Design:** When a push notification is tapped (e.g., Provider is On The Way or Completed), open a modal showing the AI's contextual `followup_actions` (e.g., "Rate the provider", "Check for leaks").

---

## 4. Error Handling & Edge Cases

- **No Providers Found:** If the SSE stream returns `No provider available in range`, fallback gracefully. Show a button to "Try Again Later" or "Expand Search Radius".
- **LLM Timeouts:** The backend has a strict 60s timeout on the workflow. If an HTTP 504 is returned, show a friendly "The AI is thinking too hard, please try again" message.
- **Cancellation:** Provide a prominent "Cancel Request" button while the orchestration is running, which calls `POST /requests/{request_id}/cancel`.

---

## Next Steps for the Frontend Team
1. Confirm the frontend framework being used.
2. Review the `API_CONTRACT.md` already present in the backend repository for exact JSON structures.
3. Set up the Firebase project in the frontend app (download `google-services.json` for Android / `GoogleService-Info.plist` for iOS).
