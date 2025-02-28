package com.mccartycarclub.module

import com.mccartycarclub.domain.usecases.user.GetUser
import com.mccartycarclub.domain.usecases.user.GetUserData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class module {
    @Binds
    abstract fun bindGetUser(getUserData: GetUserData): GetUser
}