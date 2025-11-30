package com.cerqa.di

import android.content.Context
import android.content.SharedPreferences
import com.cerqa.auth.AndroidAuthTokenProvider
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.data.StoreDefaults
import com.cerqa.data.StoreUserDefaults
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

    // Auth and HTTP engine for Apollo (from androidModule)
    single<AuthTokenProvider> { AndroidAuthTokenProvider() }
    single<HttpClientEngine> { OkHttp.create() }

    // FetchContacts implementation
    single<com.cerqa.data.FetchContacts> {
        com.cerqa.data.FetchContactsRepository(
            defaults = get(),
            ioDispatcher = get() // Gets Dispatchers.IO from appModule
        )
    }
}