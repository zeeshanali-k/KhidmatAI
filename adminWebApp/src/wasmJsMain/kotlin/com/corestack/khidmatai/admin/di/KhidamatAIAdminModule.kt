package com.corestack.khidmatai.admin.di

import com.corestack.khidmatai.core.data.repository.ApiAdminRepositoryImpl
import com.corestack.khidmatai.core.data.repository.MockAdminRepositoryImpl
import com.corestack.khidmatai.core.domain.AppEnvironment
import com.corestack.khidmatai.core.domain.repository.AdminRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@ComponentScan("com.corestack.khidmatai.admin")
@Module
class KhidamatAIAdminModule {

    @Single
    fun provideEnvironment(): AppEnvironment = AppEnvironment.DEV

    @Single
    fun provideHttpClient(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        install(Logging) {
            logger = AdminWebKtorLogger()
            level = LogLevel.ALL
        }
    }

    @Single
    fun provideAdminRepository(
        env: AppEnvironment,
        httpClient: HttpClient
    ): AdminRepository {
        return when (env) {
            AppEnvironment.DEV -> MockAdminRepositoryImpl()
            AppEnvironment.PROD -> ApiAdminRepositoryImpl(httpClient)
        }
    }
}

private class AdminWebKtorLogger : Logger {
    override fun log(message: String) {
        println("Ktor(AdminWeb): $message")
    }
}
