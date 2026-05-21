package com.corestack.khidmatai.di

import org.koin.core.annotation.KoinApplication

@KoinApplication(
    modules = [CommonModule::class, AndroidAppModule::class],
    configurations = []
)
class AndroidKoinApp
