package com.mccartycarclub.repository.realtime

import com.amplifyframework.datastore.generated.model.Message
import com.amplifyframework.kotlin.api.KotlinApiFacade
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Named

class SubscribeRepo @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : RealtimeSubscribeRepo {
    override fun createUserChannel(userId: String) {

        val message = Message.builder()
            .content("my channel test").channelName("larry-channel")
            .build()

    }
}