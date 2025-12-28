package com.cerqa.di

import android.content.Context
import android.content.SharedPreferences
import com.cerqa.auth.AndroidAuthTokenProvider
import com.cerqa.auth.AuthService
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.data.Preferences
import com.cerqa.data.StoreDefaults
import com.cerqa.data.StoreUserDefaults
import com.cerqa.data.UserPreferences
import com.cerqa.notifications.AndroidFcmTokenProvider
import com.cerqa.notifications.FcmTokenProvider
import com.cerqa.platform.DeviceContactsProvider
import com.cerqa.platform.SmsProvider
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }
    single<StoreDefaults> { StoreUserDefaults(get()) }
    single<Preferences> { UserPreferences(get()) }

    // Auth and HTTP engine for Apollo (from androidModule)
    single<AuthTokenProvider> { AndroidAuthTokenProvider() }
    single<FcmTokenProvider> { AndroidFcmTokenProvider() }
    single { AuthService(get()) }
    single<HttpClientEngine> { OkHttp.create() }

    // FetchContacts implementation
    single<com.cerqa.data.FetchContacts> {
        com.cerqa.data.FetchContactsRepository(
            defaults = get(),
            ioDispatcher = get() // Gets Dispatchers.IO from appModule
        )
    }

    // Device contacts and SMS providers
    single { DeviceContactsProvider(androidContext()) }
    single { SmsProvider(androidContext()) }
}