package com.mccartycarclub.module

import android.content.BroadcastReceiver
import android.content.Context
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.amplifyframework.kotlin.core.Amplify
import com.mccartycarclub.CarClubApplication.Companion.CHAT_INITIAL_ROUTE
import com.mccartycarclub.data.websocket.AblyRealtimeProvider
import com.mccartycarclub.data.websocket.AblyService
import com.mccartycarclub.domain.UserPreferencesManager
import com.mccartycarclub.domain.helpers.ContactsHelper
import com.mccartycarclub.domain.helpers.SearchResult
import com.mccartycarclub.domain.usecases.user.SearchResultBuilder
import com.mccartycarclub.domain.websocket.AblyProvider
import com.mccartycarclub.domain.websocket.RealtimeService
import com.mccartycarclub.pigeon.CerqaFlutterApi
import com.mccartycarclub.pigeon.PigeonFlutterApi
import com.mccartycarclub.receiver.AblyBroadcastReceiver
import com.mccartycarclub.repository.datastore.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context.applicationContext

    @Provides
    @Singleton
    fun provideContactsHelper(
        @ApplicationContext context: Context,
        @Named("IoDispatcher") ioDispatcher: CoroutineDispatcher,
    ): ContactsHelper = ContactsHelper(context, ioDispatcher)

    @Provides
    @Singleton
    fun provideAmplifyApi(): KotlinApiFacade = Amplify.API

    @Provides
    @Named("IoDispatcher")
    fun provideIoContext(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideAblyRealtimeProvider(@ApplicationContext context: Context): AblyProvider {
        return AblyRealtimeProvider(context)
    }

    @Provides
    @Singleton
    fun provideAblyService(ablyProvider: AblyProvider): RealtimeService {
        return AblyService(ablyProvider)
    }

    @Provides
    @Singleton
    fun provideAblyBroadcastReceiver(ablyService: AblyService): BroadcastReceiver {
        return AblyBroadcastReceiver(ablyService)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferencesManager {
        return UserPreferences(context)
    }


    @Provides
    @Singleton
    fun provideSearchResultBuilder(): SearchResult = SearchResultBuilder

    /**
     * Cached Flutter Engine which enables chat to start up faster than using a new
     * engine each time chat is started
     */
    @Provides
    @Singleton
    fun provideFlutterEngine(@ApplicationContext context: Context): FlutterEngine {
        return FlutterEngine(context).apply {
            navigationChannel.setInitialRoute(CHAT_INITIAL_ROUTE);
            dartExecutor.executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )
        }
    }

    /**
     * Initialize the Pigeon Flutter API in order to
     * send data from Android to Flutter
     */
    @Provides
    @Singleton
    fun providePigeonFlutterApi(flutterEngine: FlutterEngine): PigeonFlutterApi {
        return PigeonFlutterApi(flutterEngine.dartExecutor.binaryMessenger)
    }

    @Provides
    @Singleton
    fun provideCerqaFlutterApi(flutterEngine: FlutterEngine): CerqaFlutterApi {
        return CerqaFlutterApi(flutterEngine.dartExecutor.binaryMessenger)
    }

}
