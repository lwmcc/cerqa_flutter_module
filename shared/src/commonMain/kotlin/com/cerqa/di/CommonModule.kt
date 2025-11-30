package com.cerqa.di

import com.cerqa.data.TestDataSeeder
import com.cerqa.network.createApolloClient
import com.cerqa.network.createHttpClient
import com.cerqa.repository.ApolloContactsRepository
import com.cerqa.repository.ContactsRepository
import com.cerqa.repository.MockContactsRepository
import com.cerqa.viewmodels.ApolloContactsViewModel
import com.cerqa.viewmodels.ContactsViewModel
import com.cerqa.viewmodels.MockContactsViewModel
import org.koin.dsl.module

/**
 * Common Koin module for shared dependencies.
 * Platform-specific modules will provide the AuthTokenProvider and HttpClientEngine implementations.
 */
val commonModule = module {
    // HTTP Client (depends on AuthTokenProvider from platform module)
    single { createHttpClient(get()) }

    // Apollo GraphQL Client (depends on AuthTokenProvider and HttpClientEngine from platform module)
    single { createApolloClient(get(), get()) }

    // Repositories
    single { ContactsRepository(get(), get()) }
    single { ApolloContactsRepository(apolloClient = get(), tokenProvider = get()) }
    single { MockContactsRepository() }

    // Test Data Seeder (for development)
    single { TestDataSeeder(apolloClient = get(), authTokenProvider = get()) }

    // ViewModels
    factory { ContactsViewModel(get()) }
    factory { ApolloContactsViewModel(get()) }
    factory { MockContactsViewModel(get()) }
}
