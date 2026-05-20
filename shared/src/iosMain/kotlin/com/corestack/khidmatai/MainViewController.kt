package com.corestack.khidmatai

import androidx.compose.ui.window.ComposeUIViewController
import com.corestack.khidmatai.di.iOSKoinApp
import org.koin.plugin.module.dsl.startKoin

fun MainViewController() = ComposeUIViewController {
    startKoin<iOSKoinApp>()
    App()
}