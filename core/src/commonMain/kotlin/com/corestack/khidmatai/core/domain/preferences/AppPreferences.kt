package com.corestack.khidmatai.core.domain.preferences

import com.russhwolf.settings.Settings
import org.koin.core.annotation.Single

@Single
class AppPreferences {
    private val settings: Settings = Settings()
    var authToken: String
        get() = settings.getString("auth_token", "")
        set(value) = settings.putString("auth_token", value)

    var lastEmail: String
        get() = settings.getString("last_email", "")
        set(value) = settings.putString("last_email", value)

    var language: String
        get() = settings.getString("app_language", "EN")
        set(value) = settings.putString("app_language", value)

    var onboardingCompleted: Boolean
        get() = settings.getBoolean("onboarding_completed", false)
        set(value) = settings.putBoolean("onboarding_completed", value)

    val isLoggedIn: Boolean
        get() = authToken.isNotEmpty()

    fun clearAuth() {
        authToken = ""
    }
}
