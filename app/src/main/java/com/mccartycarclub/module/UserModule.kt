package com.mccartycarclub.module

import com.mccartycarclub.domain.usecases.user.GetContacts
import com.mccartycarclub.domain.usecases.user.GetContactsData
import com.mccartycarclub.domain.usecases.user.GetUser
import com.mccartycarclub.domain.usecases.user.GetUserData
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UserModule {
    @Binds
    abstract fun bindGetUser(getUserData: GetUserData): GetUser

    @Binds
    abstract fun bindGetContacts(getUserData: GetContactsData): GetContacts
}
