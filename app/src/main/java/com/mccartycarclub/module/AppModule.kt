package com.mccartycarclub.module

import android.content.Context
import com.amplifyframework.core.Amplify
import com.mccartycarclub.domain.helpers.ContactsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
}