package com.corestack.khidmatai

import android.app.Application
import com.corestack.khidmatai.di.AndroidKoinApp
import org.koin.android.ext.koin.androidContext
import org.koin.plugin.module.dsl.startKoin

class KhidmatAIApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin<AndroidKoinApp> {
            androidContext(applicationContext)
        }
    }

}
