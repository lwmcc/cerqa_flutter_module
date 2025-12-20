package com.cerqa.di

import com.cerqa.auth.AuthService
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.auth.IOSAuthTokenProvider
import com.cerqa.data.Preferences
import com.cerqa.data.StoreDefaults
import com.cerqa.data.StoreUserDefaults
import com.cerqa.data.UserPreferences
import com.cerqa.platform.DeviceContactsProvider
import com.cerqa.platform.SmsProvider
import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual fun platformModule(): Module = module {
    single { NSUserDefaults.standardUserDefaults }
    single<StoreDefaults> { StoreUserDefaults(get()) }
    single<Preferences> { UserPreferences(get()) }

    // Auth and HTTP engine for Apollo (from iosModule)
    single<AuthTokenProvider> { IOSAuthTokenProvider() }
    single { AuthService(get()) }
    single<HttpClientEngine> { Darwin.create() }

    // FetchContacts implementation
    single<com.cerqa.data.FetchContacts> {
        com.cerqa.data.FetchContactsRepository(
            defaults = get(),
            ioDispatcher = get() // Gets Dispatchers.IO from appModule
        )
    }

    // Device contacts and SMS providers
    single { DeviceContactsProvider() }
    single { SmsProvider() }
}