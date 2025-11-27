package com.cerqa.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initialize Koin DI for the shared module.
 * This function can be called from both iOS and Android.
 *
 * Usage from iOS Swift:
 * KoinHelperKt.initKoin()
 *
 * Usage from Android Kotlin:
 * initKoin { androidContext(this) }
 */
fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            appModule,
            platformModule()
        )
    }
}
