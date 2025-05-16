package com.mccartycarclub.repository.realtime

import android.util.Log
import aws.smithy.kotlin.runtime.io.closeIfCloseable
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.graphql.GraphQLResponse
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelSubscription
import com.amplifyframework.datastore.generated.model.Channel
import com.amplifyframework.datastore.generated.model.Message
import com.amplifyframework.datastore.generated.model.User
import com.amplifyframework.kotlin.api.KotlinApiFacade
import com.mccartycarclub.domain.websocket.AblyProvider
import com.mccartycarclub.domain.websocket.RealtimeService
import com.mccartycarclub.repository.AmplifyRepo.Companion.DUMMY
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named

class SubscribeRepo @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    private val ablyProvider: AblyProvider,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : RealtimeSubscribeRepo {
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    override suspend fun createUserChannel(userId: String) {

        val user = User.builder()
            .userId(userId)
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
            .id(channel.id)
            .build()

        /*try {
            amplifyApi.mutate(ModelMutation.create(channel))
            amplifyApi.mutate(ModelMutation.create(message))



            amplifyApi.subscribe(ModelSubscription.onCreate(Channel::class.java))
                .collect { response ->
                    println("SubscribeRepo ***** RESPONSE ${response.data}")
                    response.data?.let { send(it) }
                }
        } catch (e: Exception) {
            println("SubscribeRepo ***** MSG ${e.message}")
        }*/


    }
}