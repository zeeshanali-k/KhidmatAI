package com.corestack.khidmatai.core.di

import com.corestack.khidmatai.core.data.repository.ApiAuthRepositoryImpl
import com.corestack.khidmatai.core.data.repository.ApiServiceRepositoryImpl
import com.corestack.khidmatai.core.data.repository.MockAuthRepositoryImpl
import com.corestack.khidmatai.core.data.repository.MockServiceRepositoryImpl
import com.corestack.khidmatai.core.data.repository.SettingsRepository
import com.corestack.khidmatai.core.domain.ACTIVE_ENV
import com.corestack.khidmatai.core.domain.AppEnvironment
import com.corestack.khidmatai.core.domain.preferences.AppPreferences
import com.corestack.khidmatai.core.domain.repository.AuthRepository
import com.corestack.khidmatai.core.domain.repository.ServiceRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import com.corestack.khidmatai.core.util.ktorLogger
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AppModule {

    @Single
    fun provideEnvironment(): AppEnvironment = ACTIVE_ENV

    @Single
    fun provideAppPreferences(): AppPreferences = AppPreferences()

    @Single
    fun provideSettingsRepository(appPreferences: AppPreferences): SettingsRepository =
        SettingsRepository(appPreferences)

    @Single
    fun provideHttpClient() : HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        install(Logging) {
            logger = ktorLogger
            level = LogLevel.ALL
        }
    }

    @Single
    fun provideServiceRepository(
        env: AppEnvironment,
        httpClient: HttpClient,
    ): ServiceRepository {
        return when (env) {
            AppEnvironment.DEV -> MockServiceRepositoryImpl()
            AppEnvironment.PROD -> ApiServiceRepositoryImpl(
                httpClient
            )
        }
    }

    @Single
    fun provideAuthRepository(
        env: AppEnvironment,
        httpClient: HttpClient,
        appPreferences: AppPreferences
    ): AuthRepository {
        return when (env) {
            AppEnvironment.DEV -> MockAuthRepositoryImpl(
                appPreferences
            )
            AppEnvironment.PROD -> ApiAuthRepositoryImpl(
                httpClient,
                appPreferences
            )
        }
    }
}
