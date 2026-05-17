package com.corestack.khidmatai.di

import com.corestack.khidmatai.data.repository.ApiServiceRepositoryImpl
import com.corestack.khidmatai.data.repository.MockServiceRepositoryImpl
import com.corestack.khidmatai.domain.AppEnvironment
import com.corestack.khidmatai.domain.repository.ServiceRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.corestack.khidmatai")
class AppModule {

    @Single
    fun provideEnvironment(): AppEnvironment = AppEnvironment.DEV

    @Single
    fun provideHttpClient() = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    @Single
    fun provideServiceRepository(
        env: AppEnvironment,
        mockRepo: MockServiceRepositoryImpl,
        prodRepo: ApiServiceRepositoryImpl
    ): ServiceRepository {
        return when (env) {
            AppEnvironment.DEV -> mockRepo
            AppEnvironment.PROD -> prodRepo
        }
    }
}
