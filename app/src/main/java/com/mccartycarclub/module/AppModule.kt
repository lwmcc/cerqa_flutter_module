package com.mccartycarclub.module

import android.content.Context
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.amplifyframework.kotlin.core.Amplify
import com.mccartycarclub.R
import com.mccartycarclub.domain.helpers.ContactsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ably.lib.realtime.AblyRealtime
import io.ably.lib.types.ClientOptions
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
    fun provideContactsHelper(@ApplicationContext context: Context): ContactsHelper =
        ContactsHelper(context)

    @Provides
    @Singleton
    fun provideAmplifyApi(): KotlinApiFacade = Amplify.API

    @Provides
    @Named("IoDispatcher")
    fun provideIoContext(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideAbly(context: Context): AblyRealtime {
        // TODO: ABLY_TESTING_KEY is only for testing
        val options = ClientOptions(context.applicationContext.getString(R.string.ABLY_TESTING_KEY))
        return AblyRealtime(options)
    }

}