package com.corestack.khidmatai.di

import com.corestack.khidmatai.core.di.AppModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@ComponentScan("com.corestack.khidmatai.ui")
@Module(includes = [AppModule::class, PlatformModule::class])
class CommonModule
