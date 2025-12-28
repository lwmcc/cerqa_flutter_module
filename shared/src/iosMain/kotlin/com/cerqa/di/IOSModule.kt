package com.cerqa.di

import com.cerqa.auth.AuthService
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.auth.IOSAuthTokenProvider
import com.cerqa.notifications.FcmTokenProvider
import com.cerqa.notifications.IosFcmTokenProvider
import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

/**
 * iOS-specific Koin module (LEGACY - not used for DI initialization)
 * The app now uses platformModule() from PlatformModule.ios.kt
 * This module is kept only for the getIOSAuthTokenProviderInstance() helper function
 */
val iosModule = module {
    single<AuthTokenProvider> { IOSAuthTokenProvider() }
    single<FcmTokenProvider> { IosFcmTokenProvider() }
    // AuthService is now defined in platformModule()
    single<HttpClientEngine> { Darwin.create() }
}

/**
 * Get all modules for iOS
 */
fun getIOSModules() = listOf(commonModule, iosModule)

/**
 * Get Koin instances from iOS
 */
object IOSKoinHelper : KoinComponent {
    fun getAuthTokenProvider(): IOSAuthTokenProvider {
        val provider: AuthTokenProvider by inject()
        return provider as IOSAuthTokenProvider
    }
}

/**
 * Get the IOSAuthTokenProvider instance for callback setup
 */
fun getIOSAuthTokenProviderInstance(): IOSAuthTokenProvider {
    return IOSKoinHelper.getAuthTokenProvider()
}
