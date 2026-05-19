# KhidmatAI (KaamKaro)

An AI-powered service orchestration platform for the informal economy. Users describe what service they need in natural language (Urdu, Roman Urdu, or English), and an AI agent finds, ranks, and books the best nearby provider automatically.

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                   Client Applications                   │
│                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  Android App │  │   iOS App    │  │  Admin Web   │  │
│  │  (Compose)   │  │  (Compose)   │  │  (WASM/JS)   │  │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  │
│         └─────────────────┴─────────────────┘          │
│                           │                             │
│              ┌────────────▼────────────┐                │
│              │   Shared KMP Modules    │                │
│              │  (shared + core)        │                │
│              │  UI · Domain · Data     │                │
│              └────────────┬────────────┘                │
└───────────────────────────┼─────────────────────────────┘
                            │ HTTP (Ktor)
                            ▼
┌─────────────────────────────────────────────────────────┐
│               Backend — FastAPI + LangGraph              │
│                                                         │
│  POST /requests/  ──►  LangGraph Agent Graph            │
│                        │                               │
│                   Intent Parser (Gemini)                │
│                        │                               │
│                   Provider Discovery                    │
│                        │                               │
│                   Provider Ranking                      │
│                        │                               │
│                   Booking Execution                     │
│                        │                               │
│                   Follow-up / Reminders                 │
│                                                         │
│  Admin CRUD  ──►  routers/admin.py                      │
└─────────────────────────────────────────────────────────┘
```

---

## Repository Structure

```
KhidmatAI/
├── androidApp/          # Android application entry point
├── shared/              # Compose Multiplatform UI (Android + iOS)
├── core/                # Shared domain models, repositories, DTOs
├── adminWebApp/         # Admin dashboard (Kotlin/WASM, runs in browser)
├── iosApp/              # iOS native wrapper (SwiftUI entry point)
└── .github/workflows/   # CI: build APK + deploy admin panel to GitHub Pages
```

---

## Modules

### `androidApp`
Native Android entry point. Initialises Koin DI and delegates everything to the `:shared` Compose UI.

- Entry: `MainActivity.kt`, `KhidmatAIApp.kt`
- DI bootstrap: `di/AndroidKoinApp.kt`
- Dependencies: `:shared`, Play Services Location, OkHttp

### `shared`
All Compose Multiplatform UI and platform-bridging code shared across Android and iOS.

```
shared/src/commonMain/.../khidmatai/
├── ui/
│   ├── auth/           # Login, Register screens + ViewModel
│   ├── onboarding/     # Location permission onboarding (first run)
│   ├── home/           # Service request input, urgency selector, voice entry
│   ├── voice/          # Mic recording overlay screen
│   ├── processing/     # Live agent trace with staggered animation
│   ├── result/         # Success and Unavailable result screens
│   ├── bookings/       # Booking list + detail screen
│   ├── profile/        # User profile
│   ├── location/       # Location picker bottom sheet + geocoding
│   ├── components/     # AiOrbView, StatusBadge, BottomNavBar,
│   │                   # NextStepCard, TraceRowComponent,
│   │                   # MockPushNotification
│   └── theme/          # Colors, Typography, Shapes (design system)
├── data/location/      # Nominatim reverse geocoder, location prefs
└── domain/location/    # LocationService interface
```

### `core`
Pure KMP library — no UI, no platform code. Consumed by `shared`, `adminWebApp`, and (via Android) `androidApp`.

```
core/src/commonMain/.../core/
├── domain/
│   ├── model/          # AuthModels, ServiceModels, AdminModels, LocationModels
│   ├── repository/     # AuthRepository, ServiceRepository, AdminRepository (interfaces)
│   └── preferences/    # AppPreferences
└── data/
    ├── dto/            # ApiDtos, AuthDtos, AdminDtos (Ktor serialization)
    └── repository/     # Api*RepositoryImpl (Ktor) + Mock*RepositoryImpl
```

Repository injection is environment-driven: `Mock*` implementations are swapped in for local development without a running backend.

### `adminWebApp`
Web admin dashboard compiled to WASM, deployed to GitHub Pages via CI.

```
adminWebApp/src/wasmJsMain/.../admin/
├── ui/
│   ├── dashboard/      # Stats overview
│   ├── bookings/       # All bookings list + detail, status updates
│   ├── providers/      # Provider CRUD, availability toggle
│   └── requests/       # Full request log + agent trace viewer
└── di/                 # Koin WASM module setup
```

Build output: `adminWebApp/build/dist/wasmJs/productionExecutable/`

---

## Backend

> The backend lives in a separate repository. This section describes its architecture and the contract it exposes to this client.

**Stack:** Python · FastAPI · LangGraph · Google Gemini (`ChatGoogleGenerativeAI`)

**Base URL:** `http://localhost:8000` (local) / deployed URL (production)

### Agent Graph (`agents/graph.py`)

The core of the backend is a LangGraph state machine. Each node runs sequentially and appends to a shared `trace` list that the frontend animates in real time.

```
[intent_parser_node]          ← Gemini LLM: extract service type, urgency, language
        │
[provider_discovery_node]     ← Query mock_db for providers near (lat, lng)
        │
[provider_ranking_node]       ← Score by rating × distance × availability
        │
[provider_selection_node]     ← Pick top-ranked, attach AI reasoning
        │
[booking_execution_node]      ← Write booking to mock_db, generate booking ID
        │
[followup_node]               ← Schedule reminder, build next_steps array
```

### Key Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/requests/` | Submit a service request; returns full trace + booking |
| `GET`  | `/bookings/{user_id}` | User's booking history |
| `GET`  | `/bookings/detail/{booking_id}` | Single booking details |
| `POST` | `/bookings/{booking_id}/cancel` | Cancel a booking |
| `GET`  | `/admin/bookings/` | All bookings (admin) |
| `POST` | `/admin/bookings/{id}/complete` | Mark booking complete |
| `GET`  | `/admin/providers/` | All providers |
| `POST` | `/admin/providers/` | Create provider |
| `PUT`  | `/admin/providers/{id}` | Update provider |
| `DELETE` | `/admin/providers/{id}` | Delete provider |
| `PATCH` | `/admin/providers/{id}/availability` | Toggle availability |
| `GET`  | `/admin/requests/` | Full request + trace log |

### Request / Response Shape

```json
// POST /requests/
{
  "query": "Mujhe AC technician chahiye",
  "lat": 33.6844,
  "lng": 73.0479,
  "user_id": "u1"
}

// Response (success)
{
  "success": true,
  "status": "success",
  "booking_id": "BK-1747391234",
  "provider": {
    "id": "p1",
    "name": "Kamran Khan",
    "rating": 4.7,
    "distance_km": 1.2,
    "reasoning": "Top match with rating 4.7, located 1.2km from you."
  },
  "appointment": {
    "time_display": "10:30 AM, 17 May",
    "cost_per_hour": 1500,
    "currency": "PKR"
  },
  "trace": [
    { "stage": "intent_detection", "message": "Input Query received", "status": "completed" },
    { "stage": "provider_discovery", "message": "3 providers found near G-13", "status": "completed" }
  ],
  "next_steps": [...]
}
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Android UI | Jetpack Compose (via Compose Multiplatform) |
| iOS UI | Compose Multiplatform (SwiftUI wrapper) |
| Admin Web | Compose Multiplatform (WASM/JS) |
| Shared UI & Logic | Kotlin Multiplatform 2.x |
| Networking | Ktor Client 3.x (OkHttp / Darwin / JS engines) |
| DI | Koin 4.x + Koin Annotations |
| Navigation | Navigation Compose (type-safe) |
| Serialization | Kotlinx Serialization (JSON) |
| State | Compose StateFlow + MVI contracts |
| Geocoding | Nominatim (OpenStreetMap) |
| Backend | Python FastAPI |
| AI Orchestration | LangGraph |
| LLM | Google Gemini (`ChatGoogleGenerativeAI`) |
| Backend DB | Mock in-memory DB (production: PostgreSQL-ready) |

---

## Design System

Defined in `shared/src/commonMain/.../ui/theme/`:

| Token | Value | Usage |
|-------|-------|-------|
| `Primary` | `#1A6BFF` | CTAs, active states |
| `Success` | `#12B76A` | Confirmed bookings |
| `Warning` | `#F79009` | Pending, urgency |
| `Error` | `#F04438` | Failed, emergency |
| `Background` | `#F8F9FC` | Scaffold |
| `Surface` | `#FFFFFF` | Cards |

**Emergency mode** — when urgency is `emergency`, the entire UI shifts: background tints red, buttons turn `Error` color, the Processing screen speeds up animations from 350ms → 150ms stagger, and the provider phone number becomes the first full-width red CTA.

---

## Running Locally

### Android
```bash
./gradlew :androidApp:assembleDebug
# Install on connected device:
adb install androidApp/build/outputs/apk/debug/*.apk
```

### Admin Web (browser)
```bash
./gradlew :adminWebApp:wasmJsBrowserRun
# Opens at http://localhost:8080
```

### Backend
```bash
cd path/to/backend
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

Set the base URL in `core/src/commonMain/.../core/domain/AppEnvironment.kt` to point at your running backend instance.

---

## CI / CD

Defined in `.github/workflows/deploy.yml`, triggered on every push to `main`:

| Job | What it does |
|-----|-------------|
| `build-android` | Builds debug + release APKs, publishes them as a **GitHub Release** |
| `build-web` | Runs `wasmJsBrowserDistribution`, uploads pages artifact |
| `deploy-web` | Deploys artifact to **GitHub Pages** (official `actions/deploy-pages`) |

**One-time setup:** In repo Settings → Pages → Source, select **"GitHub Actions"**.

---

## Screens

| Screen | Route | Description |
|--------|-------|-------------|
| Onboarding | `OnboardingScreen` | Location permission, first run only |
| Home | `HomeScreen` | Text/voice query, urgency selector, quick chips |
| Voice Input | `VoiceInputScreen` | Mic overlay with animated waveform |
| Processing | `ProcessingScreen` | Live agent trace, staggered animation, no back nav |
| Result — Success | `ResultSuccessScreen` | AI decision card, provider card, map, next steps |
| Result — Unavailable | `ResultUnavailableScreen` | Empty state, retry options |
| Booking Detail | `BookingDetailScreen` | Full detail + collapsible agent trace accordion |
| My Bookings | `BookingsScreen` | Filtered list (All / Upcoming / Completed / Cancelled) |
