package com.cerqa.di

import com.cerqa.data.Preferences
import com.cerqa.data.UserPreferences
import com.cerqa.viewmodels.ContactsViewModel
import com.cerqa.viewmodels.MainViewModel
import org.koin.dsl.module

val appModule = module {
    single { MainViewModel(get()) }
    single { ContactsViewModel() }
    single<Preferences> { UserPreferences() }
}