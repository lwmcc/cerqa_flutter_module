package com.mccartycarclub.repository.realtime

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.datastore.generated.model.Channel
import com.amplifyframework.datastore.generated.model.Message
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.mccartycarclub.repository.AmplifyRepo.Companion.DUMMY
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject
import javax.inject.Named

class SubscribeRepo @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : RealtimeSubscribeRepo {
    override suspend fun createUserChannel(userId: String) {

        val user = User.builder()
            .userId("31cb55f0-1031-7026-1ea5-9e5c424b27de")
            .firstName(DUMMY)
            .lastName(DUMMY)
            .build()

        val channel = Channel.builder()
            .user(user)
            .name("larry-channel")
            .build()

        val message = Message.builder()
            .content("larry test message")
            .channel(channel)
            .build()

        try {
            val response1 = amplifyApi.mutate(ModelMutation.create(channel))
            val response2 = amplifyApi.mutate(ModelMutation.create(message))

            println("SubscribeRepo ***** R1 ${response1.data}")
            println("SubscribeRepo ***** R2 ${response2.data}")
        } catch (e: AmplifyException) {
            println("SubscribeRepo ***** ERROR ${e.message}")
        }
    }
}