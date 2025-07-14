package com.cerqa.di

import com.cerqa.data.Preferences
import com.cerqa.data.StoreDefaults
import com.cerqa.data.StoreUserDefaults
import com.cerqa.data.UserPreferences
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual fun platformModule(): Module = module {
    single { NSUserDefaults.standardUserDefaults }
    single<StoreDefaults> { StoreUserDefaults(get()) }
    single<Preferences> { UserPreferences(get()) }
}