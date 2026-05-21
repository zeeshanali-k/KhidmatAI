package com.corestack.khidmatai.core.util

import io.ktor.client.plugins.logging.Logger

private class WasmLogger : Logger {
    override fun log(message: String) {
        println("KtorHttpClient: $message")
    }
}

actual val ktorLogger: Logger = WasmLogger()
