# KaamKaro — AI Service Orchestrator
## Complete Mobile App Design Specification
### For: Jetpack Compose UI Generation

> **How to use this file:** This is a full design spec for an AI agent writing Jetpack Compose
> UI code. Every screen, component, color, animation, and interaction is described here.
> Follow these instructions precisely. Do not invent UI patterns not described below.

---

## 1. APP IDENTITY

| Property | Value |
|---|---|
| App Name | KaamKaro (or KhidmatAI) |
| Platform | Android (Jetpack Compose) |
| Primary Language | Roman Urdu (with English sub-labels) |
| Tone | Trustworthy, fast, local — smart neighborhood assistant |
| Target | Informal economy service booking via AI |

---

## 2. DESIGN SYSTEM

### 2.1 Color Tokens

```kotlin
// Define in ui/theme/Color.kt
val Primary          = Color(0xFF1A6BFF)   // CTAs, buttons, active states
val PrimaryDark      = Color(0xFF0D4ECC)   // pressed state
val PrimaryLight     = Color(0xFFEEF4FF)   // AI decision card background
val Success          = Color(0xFF12B76A)   // completed, confirmed
val SuccessLight     = Color(0xFFECFDF5)   // success card background
val Warning          = Color(0xFFF79009)   // pending, medium urgency
val WarningLight     = Color(0xFFFFFAEB)   // warning card background
val Error            = Color(0xFFF04438)   // failed, emergency, error
val ErrorLight       = Color(0xFFFEF3F2)   // error card background
val Background       = Color(0xFFF8F9FC)   // app scaffold background
val Surface          = Color(0xFFFFFFFF)   // cards
val TextPrimary      = Color(0xFF101828)   // headings, primary text
val TextSecondary    = Color(0xFF667085)   // body, captions
val Border           = Color(0xFFEAECF0)   // dividers, input borders
val EmergencyBg      = Color(0x0FF04438)   // 6% red — emergency screen tint
```

### 2.2 Typography

```kotlin
// Define in ui/theme/Type.kt — use Inter or Roboto font family
Display    // Bold, 24sp      — screen titles
Title      // SemiBold, 18sp  — card headings
Body       // Regular, 15sp   — descriptions, body text
Caption    // Regular, 12sp   — meta, timestamps, trace messages
Label      // Medium, 13sp    — badges, tags, chips
Mono       // Monospace, 12sp — trace values, booking IDs
```

### 2.3 Shape & Spacing

```kotlin
// Shapes
CardShape        = RoundedCornerShape(16.dp)
ButtonShape      = RoundedCornerShape(12.dp)
InputShape       = RoundedCornerShape(12.dp)
ChipShape        = RoundedCornerShape(999.dp)   // pill
BadgeShape       = RoundedCornerShape(999.dp)

// Elevation / Shadow
CardElevation    = 2.dp  (subtle shadow)
BottomBarElevation = 8.dp

// Spacing grid — use multiples of 4dp
xs = 4.dp   sm = 8.dp   md = 16.dp
lg = 24.dp  xl = 32.dp  xxl = 48.dp

// Standard component heights
ButtonHeight     = 52.dp
InputHeight      = 56.dp
BottomNavHeight  = 64.dp
```

### 2.4 Animation Specs

```kotlin
// Stage reveal (Processing screen)
StageRevealDelay      = 350ms per stage (staggered)
StageSlideIn          = slideInHorizontally from left + fadeIn
StagePulse            = infiniteTransition alpha 0.4f→1f, 800ms loop
SuccessFlash          = full-screen green overlay, 600ms fade out
ConfettiDuration      = 1200ms
EmergencyPulse        = border alpha 0.5f→1f, 400ms loop (red)

// AI Orb states
OrbIdle               = scale 0.95f→1f, 2000ms loop, ease in-out
OrbThinking           = rotation 0→360, 1200ms loop, linear
OrbDone               = scale 1f→1.3f→0.8f→1f, 400ms, spring

// General transitions
ScreenTransition      = fadeIn + slideInVertically(from bottom 32dp), 300ms
CardEntrance          = fadeIn + scale 0.96f→1f, 200ms
```

---

## 3. GLOBAL COMPONENTS

### 3.1 AI Orb Component (`AiOrbView`)

A persistent micro-character used across multiple screens to represent the AI agent.

```
Shape:    Geometric — circle with inner gradient ring (NOT a humanoid robot)
Size:     48dp on Processing screen | 24dp inline elsewhere
Colors:   Idle   → Primary gradient (blue)
          Thinking → Primary → PrimaryDark spinning gradient
          Done     → Success green burst then shrink
States:   AiOrbState.IDLE | AiOrbState.THINKING | AiOrbState.DONE | AiOrbState.ERROR
```

**Use `AiOrbView` on these screens:**

| Screen | Orb State | Orb Size | Behavior |
|---|---|---|---|
| Home (after submit tap) | THINKING | 24dp inside button | Replaces button text during API call |
| Processing screen | THINKING | 48dp top-center | Morphs as each stage runs |
| Result — Success | DONE | 24dp in banner | Bursts green, then shrinks into checkmark |
| Result — Unavailable | IDLE | 24dp in banner | Dims/grays out |

### 3.2 Bottom Navigation Bar

Three tabs — always visible except on Processing screen:

```
[🏠 Home]     [📋 Bookings]     [👤 Profile]
  Beranda         Bookings          Profile
```

- Active tab: `Primary` color icon + label
- Inactive: `TextSecondary`
- Height: 64dp, Surface background, top border `Border` color

### 3.3 Status Badge

Pill-shaped badge for booking/trace status:

```kotlin
// Variants
BadgeVariant.COMPLETED  → Success background + white text "Mukammal"
BadgeVariant.PENDING    → Warning background + white text "Jari hai"
BadgeVariant.FAILED     → Error background + white text "Nakam"
BadgeVariant.UPCOMING   → Primary background + white text "Aane wala"
BadgeVariant.EMERGENCY  → Error background + pulsing animation
```

### 3.4 Language Toggle

Shown in the top AppBar of Home screen and Booking Detail screen:

```
[ EN ]  [ RU ]  [ اردو ]    ← horizontal segmented control, 3 options
```

- Selected segment: `Primary` background, white text
- Unselected: `Surface`, `TextSecondary` text
- Height: 32dp, pill shape
- Stores selection in ViewModel (affects placeholder text only in this spec)

### 3.5 Next Step Card

Renders each item from `next_steps` array based on `type` field:

```
type: "action"  → Surface card + title + description + Primary button (full width)
type: "info"    → Surface card + title + description, no button
type: "warning" → Surface card + 4dp Warning left border + warning icon + text
```

Button action logic:
- If `action_value` starts with `+` → `Intent(ACTION_DIAL, tel:action_value)`
- If `action_value` is a booking ID → navigate to Booking Detail
- If `action_label == "Set Reminder"` → system reminder intent
- If `action_label == "Retry"` → pop back to Home screen

### 3.6 Trace Row Component

Used in Processing screen and Booking Detail accordion:

```
[Status Icon]  [Stage Label (translated)]      [Badge]
               [Message text — Caption style]
```

Status icons:
- `completed` → Animated green circle with checkmark (pop scale in)
- `pending`   → Pulsing blue dot (infiniteTransition)
- `failed`    → Red X icon
- `waiting`   → Gray empty circle (outline only)

Stage label translations:
```
intent_detection       → "Apki request samjhi"
llm_analysis           → "AI analysis"
service_classification → "Service identify ki"
urgency_classification → "Urgency level set"
provider_discovery     → "Providers dhundhe"
provider_ranking       → "Best match chuna"
provider_selection     → "Provider select kiya"
booking_execution      → "Booking confirm ki"
followup               → "Reminders set kiye"
```

---

## 4. SCREENS

---

### SCREEN 1: Onboarding — Location Setup (First Run Only)

**Route:** `OnboardingScreen` — shown once on first app launch

**Purpose:** Request location permission gracefully before the user hits the main flow.

**Layout (full screen, no bottom nav):**

```
[Top 40%]
  Illustration: Simple map pin dropping onto a city grid (SVG/Lottie)
  — soft blue and white tones

[Middle]
  Title (Display):    "Pehle apni location batayein"
  Subtitle (Body):    "Taake hum aapke qareeb ke
                       best service providers dhundh sakein"

[Bottom section]
  ┌────────────────────────────────────────┐
  │ 📍  Location use karne ki ijazat dein  │
  │     (Allow Location Access)            │
  └────────────────────────────────────────┘
  → Primary filled button, full width, 52dp height

  Below button (Caption, TextSecondary, centered):
  "Sirf service matching ke liye use hogi.
   Koi data share nahi hoga."

  [Skip for now] — TextButton, TextSecondary
```

**Permission result handling:**
- GRANTED → navigate to Home, auto-fill detected address
- DENIED → navigate to Home with empty address field + manual entry prompt
- DENIED PERMANENTLY → show a Settings deep-link snackbar

---

### SCREEN 2: Home — Service Request Input

**Route:** `HomeScreen`

**AppBar:**
```
[App logo / name — left]              [Language Toggle — right]
                                      [ EN ]  [ RU ]  [ اردو ]
```

**Greeting section:**
```
"Assalam o Alaikum!"               ← Title, TextPrimary
"Aaj kya chahiye aapko?"           ← Body, TextSecondary
```

**Main Input Card (elevation, CardShape):**
```
┌────────────────────────────────────────────┐
│  [🎤 Mic Icon — right aligned, 40dp tap]   │
│                                            │
│  TextField (multiline, no border):         │
│  Placeholder: "Apni zaroorat likhen...     │
│               Urdu, Roman Urdu ya English" │
│                                            │
│  ─────────────────────────────────────     │
│  0 / 300                  [Clear ✕]        │
└────────────────────────────────────────────┘
```

- TextField: no visual border inside card, transparent background
- Mic icon: tap → navigate to Voice Input Screen (Screen 2b)
- Character counter: turns `Warning` color at 250+, `Error` at 300
- Card padding: 16dp

**Location Row (below card, 12dp gap):**
```
┌────────────────────────────────────────────┐
│  📍  G-13, Islamabad              [Change] │
└────────────────────────────────────────────┘
```
- Background: Surface, CardShape, Border stroke 1dp
- "Change" → opens bottom sheet location picker
- Auto-populated from GPS; shows "Detecting..." with shimmer while loading

**Location Picker Bottom Sheet (triggered by Change):**
```
Search field: "Koi bhi jagah dhundhen..."
Recent locations list (mocked):
  • G-13, Islamabad
  • F-10, Islamabad
  • Blue Area, Islamabad
[Use Current Location 📍]
```

**Urgency Selector:**
Label: `"Kitni zaroorat hai?"` — Caption, TextSecondary

```
[ 🟢 Low ]  [ 🟡 Medium ]  [ 🔴 High ]  [ 🚨 Emergency ]
```

- Horizontal Row, each chip: ChipShape, 36dp height
- Selected: filled Primary background, white text
- Emergency selected: filled Error background, pulsing border animation (`EmergencyPulse`)
- Default selection: Medium

**Quick Service Chips (horizontal LazyRow, no padding snap):**

Label: `"Kya chahiye?"` — Caption, TextSecondary (12dp above)

```
[❄️ AC Tech]  [🔧 Plumber]  [⚡ Electrician]  [📚 Tutor]  [💅 Beautician]  [🏠 Carpenter]
```

- Each chip: outlined (Border stroke), ChipShape, 40dp height, horizontal padding 12dp
- On tap: auto-fills the text field with a starter phrase (Roman Urdu)

Starter phrase map:
```
AC Tech       → "Mujhe AC technician chahiye"
Plumber       → "Mujhe plumber chahiye, pipe leak hai"
Electrician   → "Bijli ki problem hai, electrician chahiye"
Tutor         → "Bacche ko tutor chahiye math ke liye"
Beautician    → "Ghar par beautician chahiye"
Carpenter     → "Carpenter chahiye furniture repair ke liye"
```

**Submit Button (bottom, full width, 52dp):**

Normal state:
```
[  Find Service →  ]    ← Primary background
```

Loading state (after tap, during API call):
```
[  AiOrb(THINKING, 24dp)  Processing...  ]   ← button disabled, orb spins
```

Disabled state (empty query or no location):
```
[  Find Service →  ]    ← 40% opacity Primary, not clickable
```

**Emergency Mode — Whole Screen State Change:**

When urgency = Emergency, apply globally on this screen:
- Scaffold background tints to `EmergencyBg` (6% red)
- Warning banner appears above input card:
  ```
  ┌─────────────────────────────────────────┐
  │  ⚠️  Emergency Mode Active              │
  │     Sirf genuine emergencies ke liye    │
  │     use karein. Misuse se bachein.      │
  └─────────────────────────────────────────┘
  ```
  Background: `ErrorLight`, left border 4dp `Error`
- Submit button turns `Error` red: `"Find Emergency Service 🚨"`

---

### SCREEN 2b: Voice Input Screen

**Route:** `VoiceInputScreen` — full screen modal (bottom sheet or overlay)

**Purpose:** WhatsApp-style voice message recording

**Layout (dark overlay background, 90% dim):**
```
[Top — dismiss handle]

[Center]
  AiOrb(THINKING, 64dp) ← large orb, listening animation

  "Bol kar batayein..."
  "Speak in Urdu, Roman Urdu, or English"

  [Animated waveform — horizontal bars, real-time amplitude]
  Height: 80dp, Primary color bars, centered

  Recording timer: "0:04" ← Caption, TextSecondary

[Bottom row]
  [✕ Cancel]          [⏹ Stop & Use]
  TextButton/Error    Primary filled button
```

**State flow:**
1. Screen opens → immediately starts recording (mic permission assumed from onboarding)
2. Waveform animates with simulated amplitude (mock if actual audio not implemented)
3. Tap "Stop & Use" → screen closes → text field on Home fills with transcribed text (mock: fill with a preset phrase for demo)
4. Tap Cancel → dismiss, no changes

**Mock transcription for demo:**
- After 3+ seconds recording → auto-fill: `"Mujhe kal subah G-13 mein AC technician chahiye"`
- Show brief `"Transcribing..."` shimmer text before filling

---

### SCREEN 3: Processing — Live Agent Trace

**Route:** `ProcessingScreen`

**No bottom navigation bar on this screen.**

**Background:** `Background` color. Back button DISABLED during processing.

**Top Section:**
```
AiOrb(THINKING, 48dp) ← centered, bobbing + spinning animation

"Agent chal raha hai..."           ← Title, TextPrimary, centered
"AI is orchestrating your request" ← Caption, TextSecondary, centered
```

**Progress Bar:**
```
Stage [N] of [Total]     ████████░░░░░░   [N/Total * 100]%
```
- Determinate LinearProgressIndicator, Primary color
- Updates as each stage completes

**Trace Timeline (LazyColumn, center of screen):**

Each trace item is a `TraceRowComponent` revealed with staggered animation:

```
┌──────────────────────────────────────────────┐
│  ✅  "Apki request samjhi"    [Mukammal]     │
│      "Input Query received and parsed"        │
└──────────────────────────────────────────────┘
        │ (connecting vertical line, Border color)
┌──────────────────────────────────────────────┐
│  ✅  "AI analysis"            [Mukammal]     │
│      "intent=ac_technician language=roman_urdu"│
└──────────────────────────────────────────────┘
        │
┌──────────────────────────────────────────────┐
│  ⏳  "Providers dhundhe"      [Jari hai]     │  ← pulsing blue dot
│      "Searching near G-13..."                 │
└──────────────────────────────────────────────┘
        │
┌──────────────────────────────────────────────┐
│  ○   "Best match chuna"                      │  ← gray empty circle
│      Waiting...                               │
└──────────────────────────────────────────────┘
```

**Animation sequence (trigger on screen enter):**
1. All stages start as `waiting` (gray circles)
2. Reveal each `completed` stage one-by-one with 350ms stagger
3. Show `pending` stage with pulsing animation after last completed
4. When `booking_execution` completes → full-screen green flash (600ms) → confetti burst (1200ms) → auto-navigate to Result screen

**Emergency mode trace:**
- All Primary blues → Error reds
- Header: `"Emergency Request — Priority Processing 🚨"`
- Stage reveal delay reduced to 150ms (feels faster/urgent)

---

### SCREEN 4: Result — Success

**Route:** `ResultSuccessScreen`

**Top Status Banner (full width, SuccessLight background):**
```
┌──────────────────────────────────────────────┐
│  AiOrb(DONE, 24dp)  ✅  Booking Confirmed!  │
│  "Kamran Khan will arrive at 10:30 AM"       │
│                              [BK-1747391234] │
└──────────────────────────────────────────────┘
```
- Booking ID: monospace font, Caption, tappable (copies to clipboard)
- Orb bursts green on screen enter, then settles into checkmark icon

**AI Decision Card (PROMINENT — first card below banner):**

Background: `PrimaryLight` (#EEF4FF), left border 4dp `Primary`, CardShape

```
┌──────────────────────────────────────────────┐
│  🤖  Kyun chuna?  (AI Decision)              │  ← Label, Primary color
│  ──────────────────────────────────────────  │
│  "Kamran Khan is the top match with          │
│   rating 4.7, located 1.2km from you."       │  ← italics, Body, TextSecondary
│                                              │
│  Score: 12.16  •  Ranked #1 of 3 providers  │  ← Caption, TextSecondary
└──────────────────────────────────────────────┘
```

> This card MUST be visually prominent and appear BEFORE the provider card.
> It is the most important element proving AI decision-making.

**Provider Card:**

```
┌──────────────────────────────────────────────┐
│  [Avatar: colored circle, initial letter]    │
│  Kamran Khan                    ⭐ 4.7       │
│  AC Technician  •  1.2 km away  •  8 yrs exp│
│                                              │
│  ┌─────────────┐   ┌──────────────────────┐ │
│  │  📞 Call Now │   │  💬 WhatsApp         │ │
│  └─────────────┘   └──────────────────────┘ │
└──────────────────────────────────────────────┘
```

- Avatar: 48dp circle, background color derived from name hash, white initial letter
- Rating: filled star icon (Warning color) + number
- Both buttons: outlined style, equal width, 44dp height

**Map View (inside or below Provider Card):**

```
┌──────────────────────────────────────────────┐
│  [Static map view — 200dp height]            │
│  Shows: Blue pin (user) + Red pin (provider) │
│  Label overlay: "1.2 km away"               │
│  [Get Directions] button overlay — bottom    │
└──────────────────────────────────────────────┘
```

- Use Google Maps Static API URL or embed MapView composable
- For demo/mock: use a placeholder map image with pins overlaid as composables
- "Get Directions" → opens Google Maps intent with provider coordinates

**Appointment Details Card:**

```
┌──────────────────────────────────────────────┐
│  📅  Appointment Details                     │
│  ──────────────────────────────────────────  │
│  Booking ID:   BK-1747391234   [Copy]        │
│  Time:         10:30 AM, 17 May              │
│  Location:     G-13, Islamabad               │
│  Cost:         PKR 1,500 / hr                │
└──────────────────────────────────────────────┘
```

**Next Steps Section:**

Label: `"Agle Steps"` — Title

Render each item from `next_steps` using `NextStepCard` component based on `type`.
See Section 3.5 for NextStepCard spec.

**Bottom CTAs:**
```
[  View Full Booking Details  ]   ← outlined button, navigates to BookingDetailScreen
[  Back to Home               ]   ← TextButton, TextSecondary
```

---

### SCREEN 5: Result — Unavailable

**Route:** `ResultUnavailableScreen`

**Top Banner (WarningLight background, Warning left border):**
```
┌──────────────────────────────────────────────┐
│  AiOrb(IDLE, 24dp, grayed)  ⚠️              │
│  "Koi Provider Available Nahi"               │
│  "Filhal is area mein AC Technician          │
│   available nahi hai"                        │
└──────────────────────────────────────────────┘
```

**Illustration (center):**
```
Empty state SVG: abstract searching animation
(magnifying glass, dotted circle, no results)
200dp height, TextSecondary color tones
```

**Error Detail Card:**
```
┌──────────────────────────────────────────────┐
│  Kya hua?                                    │
│  G-13 ke qareeb koi verified provider nahi  │
│  mila for AC Technician.                     │
│                                              │
│  Mashwara:                                   │
│  "Kuch der baad dobara try karein."          │
└──────────────────────────────────────────────┘
```

**Next Steps:** Render from `next_steps` array using `NextStepCard`

**Bottom CTAs:**
```
[  🔄 Retry Request  ]    ← Primary button, full width
[  Try Different Service  ]   ← outlined button, navigates to Home
```

---

### SCREEN 6: Booking Detail

**Route:** `BookingDetailScreen(bookingId: String)`

**AppBar:**
```
[← Back]    Booking Detail    [Share icon]
```

Share icon → share booking summary as text

**Content (scrollable LazyColumn):**

**Section 1 — Status Banner:**
Color-coded by booking status:
```
Upcoming   → PrimaryLight background, Primary text
Confirmed  → SuccessLight background, Success text
Cancelled  → ErrorLight background, Error text
Completed  → Border background, TextSecondary text
```

**Section 2 — AI Decision Card:** (same as Screen 4)

**Section 3 — Provider Card:** (same as Screen 4)

**Section 4 — Map View:** (same as Screen 4)

**Section 5 — Appointment Details Card:** (same as Screen 4)

**Section 6 — Next Steps:** (NextStepCard list)

**Section 7 — Follow-up Info Card:**
```
┌──────────────────────────────────────────────┐
│  🔔  Follow-up Info                          │
│  ──────────────────────────────────────────  │
│  Reminder set:   ✅ Yes                      │
│  Reminder time:  09:30 AM                    │
│  Status:         Booking Confirmed           │
│  Completion:     Pending                     │
└──────────────────────────────────────────────┘
```

**Section 8 — Agent Trace Accordion (collapsed by default):**

```
▶  AI Agent Log    ← tappable row, expands/collapses
```

Expanded state (animated height expansion):
```
  ✅  "Apki request samjhi"     "Input Query received and parsed"
  ✅  "AI analysis"             "intent=ac_technician"
  ✅  "Service identify ki"     "AC_TECHNICIAN detected"
  ✅  "Providers dhundhe"       "3 providers found near G-13"
  ✅  "Best match chuna"        "Score: 12.16"
  ✅  "Booking confirm ki"      "BK-1747391234 confirmed in DB"
  ✅  "Reminders set kiye"      "Reminder at 09:30 AM"
```

- Use `TraceRowComponent` for each row
- Message values: monospace font, Caption, TextSecondary
- This section is KEY for the demo video — make it clear and readable

---

### SCREEN 7: My Bookings

**Route:** `BookingsScreen`

**AppBar:**
```
My Bookings
```

**Filter Tabs (top, scrollable if needed):**
```
[Tamam]  [Aane wale]  [Mukammal]  [Cancel]
  All      Upcoming    Completed
```
- Selected tab: Primary underline + Primary text
- Unselected: TextSecondary

**Booking List (LazyColumn):**

Each item (`BookingListItem`):
```
┌──────────────────────────────────────────────┐
│  [Service Icon 40dp]   AC Technician         │
│                        Kamran Khan  ⭐ 4.7  │
│                        10:30 AM, 17 May      │
│                        G-13, Islamabad       │
│                              [Upcoming]      │
└──────────────────────────────────────────────┘
```
- Service icon: colored circle (Primary bg) with service emoji
- Status badge: right-aligned, uses `StatusBadge` component
- Tap → navigate to `BookingDetailScreen`
- Divider between items: 1dp Border color

**Empty State:**
```
[📋 Illustration — 160dp]

"Abhi tak koi booking nahi"    ← Title, TextPrimary
"Apni pehli service book karein!" ← Body, TextSecondary

[  Service Dhundho  ]          ← Primary button → Home tab
```

---

### SCREEN 8: Push Notification Mock (Demo Only)

**Purpose:** Shown in demo video to illustrate `followup.reminder_scheduled: true`

**Implementation:** Custom in-app notification overlay composable that appears as a system-style notification

**Trigger:** 3 seconds after Result Success screen loads

**Layout (slides down from top, overlays content):**
```
┌──────────────────────────────────────────────┐
│  [App Icon]  KaamKaro                        │
│              "⏰ Yaad dihani: Aapki AC       │
│               service 1 ghante mein hai.     │
│               Kamran Khan aa raha hai."      │
│                              09:30 AM        │
└──────────────────────────────────────────────┘
```

- Slides in from top (animateContentSize / AnimatedVisibility)
- Rounded corners bottom: 16dp
- Background: Surface, elevation 12dp
- Auto-dismisses after 4 seconds OR on swipe up
- Tappable → navigates to BookingDetailScreen

---

## 5. DATA FLOW & STATE MANAGEMENT

### 5.1 ViewModel Structure

```kotlin
// One shared ViewModel for the booking flow
class ServiceRequestViewModel : ViewModel() {
    // Input state
    val query: StateFlow<String>
    val address: StateFlow<String>
    val urgency: StateFlow<String>  // "low"|"medium"|"high"|"emergency"
    val selectedLanguage: StateFlow<Language>  // EN, RU, URDU

    // API state
    val uiState: StateFlow<ServiceRequestUiState>
    // ServiceRequestUiState = Loading | Success(data) | Unavailable(data) | Error(msg)

    // Trace animation state
    val visibleTraceCount: StateFlow<Int>  // increments with delay for stagger animation
}
```

### 5.2 API Response → UI Mapping

All fields map directly from the `parseResponse()` function defined in the API spec document.

```
result.success          → navigate to ResultSuccess or ResultUnavailable
result.provider         → ProviderCard data (null-safe)
result.appointment      → AppointmentCard data (null-safe)
result.nextSteps[]      → NextStepCard list
result.trace[]          → TraceRowComponent list
result.followup         → FollowupInfoCard data
result.error            → ErrorDetailCard data
result.provider.reasoning → AiDecisionCard (PROMINENT)
```

### 5.3 Navigation Graph

```
OnboardingScreen (first-run only)
    ↓
HomeScreen ←─────────────────────────────┐
    ↓                                    │
VoiceInputScreen (modal)                 │
    ↓ (on submit)                        │
ProcessingScreen                         │
    ↓ (on complete)                      │
ResultSuccessScreen ──────────────────── Back to Home
ResultUnavailableScreen ──────────────── Back to Home
    ↓ (View Booking)
BookingDetailScreen
    ↑
BookingsScreen (bottom nav tab)
```

---

## 6. EMERGENCY MODE SUMMARY

Emergency mode activates when `urgency == "emergency"`. Apply these changes:

| Element | Normal | Emergency |
|---|---|---|
| Screen background | `Background` | `EmergencyBg` (6% red tint) |
| Submit button | Primary blue | Error red + 🚨 |
| Urgency chip | Primary selected | Error pulsing border |
| Warning banner | Hidden | Shown (ErrorLight card) |
| Processing header | "Agent chal raha hai..." | "Emergency Request — Priority Processing 🚨" |
| Trace color | Primary blue | Error red |
| Trace animation speed | 350ms delay | 150ms delay |
| Result banner | SuccessLight | Error red "EMERGENCY BOOKING" |
| Provider phone | In next steps | FIRST element, full-width red button |
| Reminder step | Shown | Hidden — replaced by "Track Provider Live" |

---

## 7. MOCK DATA REFERENCE

Use this mock data when API is unavailable during development:

```kotlin
val mockSuccessResponse = ServiceResult(
    success = true,
    status = "success",
    message = "Booking confirmed. Kamran Khan will contact you before 10:30 AM, 17 May.",
    bookingId = "BK-1747391234",
    detectedService = "ac_technician",
    detectedLanguage = "roman_urdu",
    urgency = "medium",
    provider = Provider(
        id = "p1", name = "Kamran Khan",
        phone = "+923001234567", rating = 4.7f,
        distanceKm = 1.2f, experienceYears = 8,
        reasoning = "Kamran Khan is the top match with rating 4.7, located 1.2km from you."
    ),
    appointment = Appointment(
        bookingId = "BK-1747391234",
        timeDisplay = "10:30 AM, 17 May",
        address = "G-13, Islamabad",
        costPerHour = 1500, currency = "PKR"
    )
)

val mockTrace = listOf(
    TraceItem("intent_detection",      "Input Query received and parsed",         "completed"),
    TraceItem("llm_analysis",          "intent=ac_technician language=roman_urdu","completed"),
    TraceItem("service_classification","Service Detected: AC_TECHNICIAN",         "completed"),
    TraceItem("provider_discovery",    "Found 3 verified providers near G-13",    "completed"),
    TraceItem("provider_ranking",      "Kamran Khan selected with score 12.16",   "completed"),
    TraceItem("booking_execution",     "Booking BK-1747391234 confirmed in DB",   "completed"),
    TraceItem("followup",              "Reminder scheduled for 09:30 AM",         "completed")
)

val mockNextSteps = listOf(
    NextStep(1, "Provider call karega",
        "Kamran Khan aapko 15 minutes ke andar call karega.", "action",
        "+923001234567", "Call Now"),
    NextStep(2, "Jagah saaf karein",
        "Service ke liye relevant area clear karein.", "info", null, null),
    NextStep(3, "Reminder mil jayega",
        "Appointment se 1 ghanta pehle (09:30 AM) aapko reminder milega.", "info",
        "09:30 AM", "Set Reminder"),
    NextStep(4, "Booking track karein",
        "App mein apni booking ka real-time status dekh sakte hain.", "info",
        "BK-1747391234", "Track")
)
```

---

## 8. ACCESSIBILITY & EDGE CASES

- All interactive elements: minimum 48dp touch target
- All images/icons: `contentDescription` in both Roman Urdu and English
- Color is never the ONLY indicator — always pair with icon or text
- Shimmer loading state for all network-dependent text fields
- All TextFields: `keyboardOptions` set to appropriate type
- Network error → bottom Snackbar: `"Internet connection check karein. Dobara try karein."`
- Empty query submission: shake animation on input card + error border

---

## 9. DEMO VIDEO CHECKLIST

Ensure these elements are clearly visible in the 3–5 min demo:

- [ ] Language toggle visible in AppBar
- [ ] Voice input flow (Screen 2b) shown
- [ ] Quick service chip auto-fill shown
- [ ] Emergency mode visual shift shown
- [ ] Processing screen with staggered trace animation (cinematic moment)
- [ ] AI Orb transitions: THINKING → DONE
- [ ] AI Decision Card (Kyun chuna?) visible prominently
- [ ] Map view with provider pin shown
- [ ] All 4 Next Step cards rendered
- [ ] Agent Trace accordion expanded in Booking Detail
- [ ] Push notification overlay shown
- [ ] Unavailable state shown (test with a different location)

---

*End of Design Specification — v1.0*
*Generated for: Hackathon Challenge 2 — AI Service Orchestrator*
