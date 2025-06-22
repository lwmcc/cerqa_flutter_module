package com.mccartycarclub.module

import com.mccartycarclub.data.websocket.RealTimeMessaging
import com.mccartycarclub.domain.helpers.ContactsHelper
import com.mccartycarclub.domain.helpers.DeviceContacts
import com.mccartycarclub.domain.websocket.RealTime
import com.mccartycarclub.repository.AmplifyDbRepo
import com.mccartycarclub.repository.AmplifyRepo
import com.mccartycarclub.repository.CombinedContactsHelper
import com.mccartycarclub.repository.CombinedContactsRepository
import com.mccartycarclub.repository.ContactsQueryBuilder
import com.mccartycarclub.repository.ContactsQueryHelper
import com.mccartycarclub.repository.ContactsRepository
import com.mccartycarclub.repository.DbRepository
import com.mccartycarclub.repository.LocalRepository
import com.mccartycarclub.repository.QueryBuilder
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.repository.Repo
import com.mccartycarclub.repository.realtime.PublishRepo
import com.mccartycarclub.repository.realtime.RealtimePublishRepo
import com.mccartycarclub.repository.realtime.RealtimeSubscribeRepo
import com.mccartycarclub.repository.realtime.SubscribeRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    abstract fun bindAmplifyDbRepo(amplifyDbRepo: AmplifyDbRepo): DbRepository

    @Binds
    abstract fun bindAmplifyRepo(amplifyRepo: AmplifyRepo): RemoteRepo

    @Binds
    abstract fun bindRepo(repo: Repo): LocalRepository

    @Binds
    abstract fun bindQueryBuilder(contactsQueryBuilder: ContactsQueryBuilder): QueryBuilder

    @Binds
    abstract fun bindRealtimeSubscribeRepo(subscribeRepo: SubscribeRepo): RealtimeSubscribeRepo

    @Binds
    abstract fun bindRealTimeMessaging(realTimeMessaging: RealTimeMessaging): RealTime

    @Binds
    abstract fun bindPublishRepo(publishRepo: PublishRepo): RealtimePublishRepo

    @Binds
    abstract fun bindDeviceContacts(contactsHelper: ContactsHelper): DeviceContacts

    @Binds
    abstract fun bindContactsRepository(combinedContactsRepository: CombinedContactsRepository): ContactsRepository

    @Binds
    abstract fun bindContactsHelper(contactsQueryHelper: ContactsQueryHelper): CombinedContactsHelper
}
