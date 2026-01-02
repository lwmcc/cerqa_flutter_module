package com.mccartycarclub.di

import com.cerqa.domain.repository.ContactsRepository as SharedContactsRepository
import com.cerqa.domain.repository.LocalRepository as SharedLocalRepository
import com.mccartycarclub.repository.ContactsRepository
import com.mccartycarclub.repository.LocalRepository
import com.mccartycarclub.repository.shared.SharedContactsRepositoryImpl
import com.mccartycarclub.repository.shared.SharedLocalRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing shared repository implementations
 */
@Module
@InstallIn(SingletonComponent::class)
object SharedRepositoryModule {

    @Provides
    @Singleton
    fun provideSharedContactsRepository(
        androidContactsRepository: ContactsRepository
    ): SharedContactsRepository {
        return SharedContactsRepositoryImpl(androidContactsRepository)
    }

    @Provides
    @Singleton
    fun provideSharedLocalRepository(
        androidLocalRepository: LocalRepository
    ): SharedLocalRepository {
        return SharedLocalRepositoryImpl(androidLocalRepository)
    }
}
