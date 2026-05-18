package com.corestack.khidmatai.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.KoinApplication

@KoinApplication(
    modules = [AppModule::class],
    configurations = []
)
@ComponentScan("com.corestack.khidmatai")
class KhidmatAIKoinApp