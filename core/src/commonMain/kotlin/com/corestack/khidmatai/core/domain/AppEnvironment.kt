package com.corestack.khidmatai.core.domain

enum class AppEnvironment(val value : String) {
    DEV("dev"), PROD("prod")
}

val ACTIVE_ENV = AppEnvironment.PROD