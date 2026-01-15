package com.example.kmpcompose.di

import android.content.Context
import com.example.kmpcompose.data.local.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseDriverFactory(get()) }
}

fun initKoin(context: Context) {
    org.koin.core.context.startKoin {
        modules(
            module {
                single<Context> { context }
            },
            platformModule,
            appModule
        )
    }
}
