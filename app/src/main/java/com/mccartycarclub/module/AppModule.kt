package com.mccartycarclub.module

import android.content.Context
import com.amplifyframework.api.ApiCategory
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.amplifyframework.kotlin.core.Amplify
import com.mccartycarclub.domain.helpers.ContactsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideContactsHelper(@ApplicationContext context: Context): ContactsHelper = ContactsHelper(context)

    @Provides
    @Singleton
    fun provideAmplifyApi(): KotlinApiFacade = Amplify.API

    @Provides
    @Named("IoDispatcher")
    fun provideIoContext(): CoroutineDispatcher = Dispatchers.IO
}