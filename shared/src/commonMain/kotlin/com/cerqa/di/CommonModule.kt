package com.cerqa.di

import com.cerqa.auth.AuthTokenProvider
import com.cerqa.data.TestDataSeeder
import com.cerqa.data.UserProfileRepository
import com.cerqa.data.UserRepository
import com.cerqa.data.UserRepositoryImpl
import com.cerqa.network.createApolloClient
import com.cerqa.network.createHttpClient
import com.cerqa.repository.ApolloContactsRepository
import com.cerqa.repository.ContactsRepository
import com.cerqa.repository.MockContactsRepository
import com.cerqa.viewmodels.ApolloContactsViewModel
import com.cerqa.viewmodels.ContactsViewModel
import com.cerqa.viewmodels.MainViewModel
import com.cerqa.viewmodels.MockContactsViewModel
import com.cerqa.viewmodels.ProfileViewModel
import com.cerqa.viewmodels.SearchViewModel
import org.koin.dsl.module

val commonModule = module {
    single { createHttpClient(get()) }
    single { createApolloClient(get(), get()) }
    single { ContactsRepository(get(), get()) }
    single { ApolloContactsRepository(apolloClient = get(), tokenProvider = get()) }
    single { MockContactsRepository() }
    single { UserProfileRepository(apolloClient = get(), authTokenProvider = get()) }
    single<UserRepository> {
        UserRepositoryImpl(
            apolloClient = get(),
            ioDispatcher = get(),
            authTokenProvider = get(),
            preferences = get(),
        )
    }

    single { TestDataSeeder(apolloClient = get(), authTokenProvider = get()) }

    factory { ContactsViewModel(get()) }
    factory { ApolloContactsViewModel(get()) }
    factory { MockContactsViewModel(get()) }
    factory { SearchViewModel(get()) }
    factory { ProfileViewModel(get(), get()) }
    factory { MainViewModel(preferences = get(), userRepository = get(), mainDispatcher = get()) }
}
