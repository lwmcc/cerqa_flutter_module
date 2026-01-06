package com.cerqa.di

import com.cerqa.auth.AuthService
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.data.TestDataSeeder
import com.cerqa.data.UserProfileRepository
import com.cerqa.data.UserRepository
import com.cerqa.data.UserRepositoryImpl
import com.cerqa.network.createApolloClient
import com.cerqa.network.createHttpClient
import com.cerqa.notifications.Notifications
import com.cerqa.notifications.NotificationsImpl
import com.cerqa.realtime.AblyService
import com.cerqa.realtime.createAblyClient
import com.cerqa.repository.AblyRepository
import com.cerqa.repository.AblyRepositoryImpl
import com.cerqa.repository.ApolloContactsRepository
import com.cerqa.repository.AuthRepository
import com.cerqa.repository.AuthRepositoryImpl
import com.cerqa.repository.ContactsRepository
import com.cerqa.repository.ConversationRepository
import com.cerqa.repository.ConversationRepositoryImpl
import com.cerqa.repository.GroupRepository
import com.cerqa.repository.GroupRepositoryImpl
import com.cerqa.repository.MockContactsRepository
import com.cerqa.repository.NotificationRepository
import com.cerqa.repository.NotificationRepositoryImpl
import com.cerqa.repository.RealtimeRepository
import com.cerqa.repository.RealtimeRepositoryImpl
import com.cerqa.viewmodels.ApolloContactsViewModel
import com.cerqa.viewmodels.ChatViewModel
import com.cerqa.viewmodels.ContactsViewModel
import com.cerqa.viewmodels.ConversationViewModel
import com.cerqa.viewmodels.CreateGroupViewModel
import com.cerqa.viewmodels.MainViewModel
import com.cerqa.viewmodels.MockContactsViewModel
import com.cerqa.viewmodels.ProfileViewModel
import com.cerqa.viewmodels.SearchViewModel
import org.koin.dsl.module

val commonModule = module {
    single { createHttpClient(get()) }
    single { createApolloClient(get(), get()) }
    single { ContactsRepository(get(), get(), get()) }
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
    single<AuthRepository> {
        AuthRepositoryImpl(authService = get<AuthService>())
    }
    single<AblyRepository> {
        AblyRepositoryImpl(apolloClient = get())
    }
    single { createAblyClient() }
    single {
        AblyService(ablyRepository = get(), ablyClient = get())
    }
    single<Notifications> {
        NotificationsImpl(apolloClient = get())
    }
    single<NotificationRepository> {
        NotificationRepositoryImpl(apolloClient = get())
    }

    single { TestDataSeeder(apolloClient = get(), authTokenProvider = get()) }
    single<RealtimeRepository> { RealtimeRepositoryImpl(ablyService = get()) }
    single<ConversationRepository> { ConversationRepositoryImpl(apolloClient = get(), ablyClient = get()) }
    single<GroupRepository> { GroupRepositoryImpl(apolloClient = get()) }

    factory { ContactsViewModel(get(), get(), get()) }
    factory { ApolloContactsViewModel(get()) }
    factory { MockContactsViewModel(get()) }
    factory {
        SearchViewModel(
            repository = get(),
            userRepository = get(),
            notifications = get(),
            deviceContactsProvider = get(),
            smsProvider = get(),
            realtimeRepository = get(),
        )
    }
    factory {
        ProfileViewModel(
            apolloClient = get(),
            authTokenProvider = get(),
            authRepository = get(),
            userRepository = get(),
            preferences = get(),
        )
    }
    factory {
        MainViewModel(
            authTokenProvider = get(),
            preferences = get(),
            userRepository = get(),
            mainDispatcher = get(),
            ablyService = get(),
            notifications = get(),
            notificationRepository = get(),
            fcmTokenProvider = get(),
            realtimeRepository = get(),
        )
    }
    factory {
        ConversationViewModel(
            authTokenProvider = get(),
            mainDispatcher = get(),
            conversationRepository = get()
        )
    }
    factory {
        ChatViewModel(
            authTokenProvider = get(),
            conversationRepository = get(),
            mainDispatcher = get(),
            groupRepository = get(),
        )
    }
    factory {
        CreateGroupViewModel(
            groupRepository = get(),
            contactsRepository = get(),
            authTokenProvider = get(),
            mainDispatcher = get(),
        )
    }
}
