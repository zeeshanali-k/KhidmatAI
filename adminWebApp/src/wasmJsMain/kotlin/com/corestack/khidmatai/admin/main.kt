package com.corestack.khidmatai.admin

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.corestack.khidmatai.admin.di.KhidmatAIAdminKoinApp
import kotlinx.browser.document
import org.koin.plugin.module.dsl.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin<KhidmatAIAdminKoinApp>()

    ComposeViewport(document.body!!) {
        AdminApp()
    }
}
