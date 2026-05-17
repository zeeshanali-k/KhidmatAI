# Rules — Gradle & Build Configuration

## Version Catalog

All dependencies are declared in `libs.versions.toml`. Never use string literals in `build.gradle.kts`:

```toml
[versions]
kotlin = "2.1.0"
compose-multiplatform = "1.8.0"
agp = "8.9.0"
ksp = "2.1.0-1.0.29"
room = "2.7.0"
koin = "4.0.0"
koin-annotations = "2.0.0"
ktor = "3.1.0"
coroutines = "1.10.0"
lifecycle = "2.9.0"
navigation = "2.9.0"
window = "1.4.0"
turbine = "1.2.0"
junit5 = "5.11.0"
mockk = "1.13.14"

[libraries]
# Compose
compose-ui = { module = "androidx.compose.ui:ui" }
compose-material3 = { module = "androidx.compose.material3:material3" }
compose-navigation = { module = "androidx.navigation:navigation-compose", version.ref = "navigation" }
# Lifecycle
lifecycle-viewmodel-compose = { module = "androidx.lifecycle:lifecycle-viewmodel-compose", version.ref = "lifecycle" }
lifecycle-runtime-compose = { module = "androidx.lifecycle:lifecycle-runtime-compose", version.ref = "lifecycle" }
# Window
androidx-window = { module = "androidx.window:window", version.ref = "window" }
# Room
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
# Koin
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-androidx-compose", version.ref = "koin" }
koin-annotations = { module = "io.insert-koin:koin-annotations", version.ref = "koin-annotations" }
koin-ksp-compiler = { module = "io.insert-koin:koin-ksp-compiler", version.ref = "koin-annotations" }
# Ktor
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-serialization-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
# Test
test-junit5 = { module = "org.junit.jupiter:junit-jupiter", version.ref = "junit5" }
test-mockk = { module = "io.mockk:mockk", version.ref = "mockk" }
test-coroutines = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "coroutines" }
test-turbine = { module = "app.cash.turbine:turbine", version.ref = "turbine" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
compose-multiplatform = { id = "org.jetbrains.compose", version.ref = "compose-multiplatform" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

---

## KSP Configuration

```kotlin
// build.gradle.kts — any module using Room or Koin annotations
plugins {
    alias(libs.plugins.ksp)
}

ksp {
    // Room
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
    arg("room.generateKotlin", "true")
    // Koin
    arg("KOIN_CONFIG_CHECK", "true")
    arg("KOIN_DEFAULT_MODULE", "false")
}

dependencies {
    ksp(libs.room.compiler)
    ksp(libs.koin.ksp.compiler)
}
```

**Never** add `kapt` or `annotationProcessor` blocks. If any module introduces them, remove and replace with KSP equivalents.

---

## Convention Plugins (`build-logic`)

Create shared convention plugins to eliminate Gradle boilerplate:

```
build-logic/
  src/main/kotlin/
    AndroidLibraryConventionPlugin.kt   → common android library config
    AndroidFeatureConventionPlugin.kt   → feature module (library + compose + koin + testing)
    KmpLibraryConventionPlugin.kt       → KMP library base config
    ComposeConventionPlugin.kt          → Compose + Material3 dependencies
```

Every feature module applies `AndroidFeatureConventionPlugin` — never copy-paste android config blocks.

---

## Build Variants

```kotlin
// app/build.gradle.kts
android {
    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://dev.api.example.com/\"")
            buildConfigField("Boolean", "ENABLE_LOGGING", "true")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"https://api.example.com/\"")
            buildConfigField("Boolean", "ENABLE_LOGGING", "false")
        }
    }
    buildFeatures { buildConfig = true }
}
```

- Enable **R8 full mode** in release: add `-allowaccessmodification` and `-repackageclasses` to ProGuard rules.
- Every module declares `namespace` explicitly.
- Lint errors are treated as build failures in CI: `lintOptions { abortOnError = true }`.

---

## CI Checklist

Before merging, generated code must pass:

- [ ] `./gradlew detekt` — no new issues
- [ ] `./gradlew lint` — no new errors  
- [ ] `./gradlew testDebugUnitTest` — all tests pass
- [ ] `./gradlew assembleRelease` — clean R8 build, no missing ProGuard rules
- [ ] No `kapt` plugin in any `build.gradle.kts`
- [ ] No hardcoded color/dp values in composables
- [ ] No ViewModel type in composable parameters (only state + lambda callbacks)
- [ ] No domain model imports in DTOs/entities
