package com.corestack.khidmatai.data.repository

import com.corestack.khidmatai.domain.preferences.AppPreferences
import com.corestack.khidmatai.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val appPreferences: AppPreferences
) : SettingsRepository {

    override var language: String
        get() = appPreferences.language
        set(value) {
            appPreferences.language = value
        }

    override var onboardingCompleted: Boolean
        get() = appPreferences.onboardingCompleted
        set(value) {
            appPreferences.onboardingCompleted = value
        }
}
