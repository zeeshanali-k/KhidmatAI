package com.corestack.khidmatai.domain.repository

interface SettingsRepository {
    var language: String
    var onboardingCompleted: Boolean
}
