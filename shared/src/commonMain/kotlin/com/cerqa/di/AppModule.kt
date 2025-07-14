package com.cerqa.di

import com.cerqa.data.FetchContacts
import com.cerqa.data.FetchContactsRepository
import com.cerqa.data.Preferences
import com.cerqa.data.StoreDefaults
import com.cerqa.data.UserPreferences
import com.cerqa.viewmodels.ContactsViewModel
import com.cerqa.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val appModule = module {
    single<StoreDefaults> { provideStoreDefaults() }
    single<Preferences> { UserPreferences(get()) }
    single { Dispatchers.IO }
    single { Dispatchers.Main }
    single<FetchContacts> { FetchContactsRepository(get()) }
    single { MainViewModel(get(), get(), get()) }
    single { ContactsViewModel(get(), get()) }
}
