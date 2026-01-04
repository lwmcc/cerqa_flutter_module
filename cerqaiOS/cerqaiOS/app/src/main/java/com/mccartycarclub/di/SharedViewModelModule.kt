package com.mccartycarclub.di

import com.cerqa.domain.repository.ContactsRepository as SharedContactsRepository
import com.cerqa.domain.repository.LocalRepository as SharedLocalRepository
import com.cerqa.viewmodels.SharedContactsViewModel
import com.cerqa.viewmodels.SharedMainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Hilt module for providing shared ViewModels
 */
@Module
@InstallIn(ViewModelComponent::class)
object SharedViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideSharedMainViewModel(
        localRepository: SharedLocalRepository
    ): SharedMainViewModel {
        return SharedMainViewModel(localRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSharedContactsViewModel(
        contactsRepository: SharedContactsRepository,
        localRepository: SharedLocalRepository
    ): SharedContactsViewModel {
        return SharedContactsViewModel(contactsRepository, localRepository)
    }
}
