package com.mccartycarclub.repository.realtime

import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amplifyframework.kotlin.api.KotlinApiFacade
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Named

class SubscribeRepo @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : RealtimeSubscribeRepo {
    override fun createUserChannel(userId: String) {

/*        val message = Message.builder()
            .channelName("world")
            .content("My first message!")
            .build()*/

    }
}