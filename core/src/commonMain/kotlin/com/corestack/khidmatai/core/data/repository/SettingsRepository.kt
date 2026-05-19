package com.corestack.khidmatai.core.data.repository

import com.corestack.khidmatai.core.domain.preferences.AppPreferences
import org.koin.core.annotation.Single

@Single
class SettingsRepository(
    private val appPreferences: AppPreferences
) {

    var language: String
        get() = appPreferences.language
        set(value) {
            appPreferences.language = value
        }

    var onboardingCompleted: Boolean
        get() = appPreferences.onboardingCompleted
        set(value) {
            appPreferences.onboardingCompleted = value
        }
}
