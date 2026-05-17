# Rules — Security

## Secrets Management

- **Never** commit API keys, tokens, or secrets to source control.
- Store secrets in `local.properties` and inject via `buildConfigField` in `build.gradle.kts`.
- For CI/CD, inject secrets as environment variables and read them in Gradle:

```kotlin
// build.gradle.kts
val apiKey = System.getenv("API_KEY") ?: properties["API_KEY"].toString()
buildConfigField("String", "API_KEY", "\"$apiKey\"")
```

---

## Data at Rest

- Sensitive user data must use **`EncryptedSharedPreferences`** (`androidx.security:security-crypto`) or **DataStore with encryption**.
- Room databases containing sensitive data should use **SQLCipher** via `SupportFactory`.
- Never store plaintext passwords, tokens, or PII in `SharedPreferences` or unencrypted Room databases.

---

## Network Security

- Configure **certificate pinning** in the Ktor `OkHttp` engine for production builds:

```kotlin
OkHttp.create {
    config {
        if (!BuildConfig.DEBUG) {
            val spec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_3)
                .build()
            connectionSpecs(listOf(spec))
        }
    }
}
```

- Enforce `HTTPS` only in `network_security_config.xml` — never allow cleartext traffic in release builds.
- Never log request/response bodies in release builds (`LogLevel.NONE`).

---

## ProGuard / R8

Maintain ProGuard rules for every library that uses reflection:

```proguard
# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class com.example.**$$serializer { *; }

# Koin
-keep class org.koin.** { *; }
-keep @org.koin.core.annotation.* class * { *; }

# Ktor
-keep class io.ktor.** { *; }
```

- Verify release APK/AAB with `./gradlew assembleRelease` after any dependency change.
- Use `@Keep` on classes that must survive R8 shrinking and cannot use ProGuard rules.
