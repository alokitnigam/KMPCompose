package com.example.kmpcompose.di

import com.example.kmpcompose.data.local.DatabaseDriverFactory
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory() }
}

fun initKoin() {
    startKoin {
        modules(platformModule, appModule)
    }
}
