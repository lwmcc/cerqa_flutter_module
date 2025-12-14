package com.cerqa.di

import com.cerqa.auth.AndroidAuthTokenProvider
import com.cerqa.auth.AuthService
import com.cerqa.auth.AuthTokenProvider
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.dsl.module

/**
 * Android-specific Koin module (LEGACY - not used for DI initialization)
 * The app now uses platformModule() from PlatformModule.android.kt
 * This module is kept for backward compatibility
 */
val androidModule = module {
    single<AuthTokenProvider> { AndroidAuthTokenProvider() }
    // AuthService is now defined in platformModule()
    single<HttpClientEngine> { OkHttp.create() }
}

/**
 * Get all modules for Android.
 */
fun getAndroidModules() = listOf(commonModule, androidModule)
