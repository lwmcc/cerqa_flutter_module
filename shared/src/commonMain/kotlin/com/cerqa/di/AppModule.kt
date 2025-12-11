package com.cerqa.di

import com.cerqa.data.Preferences
import com.cerqa.data.StoreDefaults
import com.cerqa.data.UserPreferences
// import com.cerqa.viewmodels.ContactsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val appModule = module {
    // Platform-specific StoreDefaults and Preferences are provided by platformModule
    single { Dispatchers.IO }
    single { Dispatchers.Main }
    // MainViewModel is now defined in commonModule with proper dependencies
    // single { ContactsViewModel(get(), get()) }
}
