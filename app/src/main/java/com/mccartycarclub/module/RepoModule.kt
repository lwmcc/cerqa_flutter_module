package com.mccartycarclub.module

import com.mccartycarclub.domain.usecases.user.GetUser
import com.mccartycarclub.domain.usecases.user.GetUserData
import com.mccartycarclub.repository.AmplifyDbRepo
import com.mccartycarclub.repository.DbRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    abstract fun bindAmplifyDbRepo(amplifyDbRepo: AmplifyDbRepo): DbRepo
}