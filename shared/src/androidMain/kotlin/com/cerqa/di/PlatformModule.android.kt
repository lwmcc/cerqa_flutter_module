package com.cerqa.di

import android.content.Context
import android.content.SharedPreferences
import com.cerqa.data.StoreDefaults
import com.cerqa.data.StoreUserDefaults
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }
    single<StoreDefaults> { StoreUserDefaults(get()) }
    // single<FetchContacts> { FetchContactsRepository(get(), get(), get()) }
}