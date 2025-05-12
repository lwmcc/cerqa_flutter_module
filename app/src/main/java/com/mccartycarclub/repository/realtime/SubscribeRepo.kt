package com.mccartycarclub.repository.realtime


import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.datastore.generated.model.Channel
import com.amplifyframework.datastore.generated.model.Message
import com.amplifyframework.kotlin.api.KotlinApiFacade
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Named

class SubscribeRepo @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : RealtimeSubscribeRepo {
    override suspend fun createUserChannel(userId: String) {

        val channel = Channel.builder()
            .name("larry-channel")
            .build()

        val message = Message.builder()
            .content("my channel test")
            .channelName(channel.id)
            .build()


        //amplifyApi.mutate(ModelMutation.create(message))




    }
}