package com.corestack.khidmatai.admin.di

import org.koin.core.annotation.KoinApplication


@KoinApplication(
    modules = [KhidamatAIAdminModule::class],
    configurations = []
)
class KhidmatAIAdminKoinApp
