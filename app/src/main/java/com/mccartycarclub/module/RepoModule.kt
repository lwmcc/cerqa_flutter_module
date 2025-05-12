package com.mccartycarclub.module

import com.mccartycarclub.domain.usecases.user.GetUser
import com.mccartycarclub.domain.usecases.user.GetUserData
import com.mccartycarclub.repository.AmplifyDbRepo
import com.mccartycarclub.repository.AmplifyRepo
import com.mccartycarclub.repository.ContactsQueryBuilder
import com.mccartycarclub.repository.DbRepo
import com.mccartycarclub.repository.LocalRepo
import com.mccartycarclub.repository.QueryBuilder
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.repository.Repo
import com.mccartycarclub.repository.realtime.RealtimeSubscribeRepo
import com.mccartycarclub.repository.realtime.SubscribeRepo
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

    @Binds
    abstract fun bindAmplifyRepo(amplifyRepo: AmplifyRepo): RemoteRepo

    @Binds
    abstract fun bindRepo(repo: Repo): LocalRepo

    @Binds
    abstract fun bindQueryBuilder(contactsQueryBuilder: ContactsQueryBuilder): QueryBuilder

    @Binds
    abstract fun bindRealtimeSubscribeRepo(subscribeRepo: SubscribeRepo): RealtimeSubscribeRepo
}