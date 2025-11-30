package com.cerqa.di

import com.cerqa.auth.AndroidAuthTokenProvider
import com.cerqa.auth.AuthTokenProvider
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.dsl.module

/**
 * Android-specific Koin module.
 * Provides the Android implementation of AuthTokenProvider and HttpClientEngine.
 */
val androidModule = module {
    single<AuthTokenProvider> { AndroidAuthTokenProvider() }
    single<HttpClientEngine> { OkHttp.create() }
}

/**
 * Get all modules for Android.
 */
fun getAndroidModules() = listOf(commonModule, androidModule)
