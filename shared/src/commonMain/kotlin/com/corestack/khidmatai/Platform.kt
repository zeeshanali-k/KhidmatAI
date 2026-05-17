package com.corestack.khidmatai

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform