package com.mccartycarclub.repository

import com.amplifyframework.AmplifyException
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.model.ModelList
import com.amplifyframework.core.model.query.predicate.QueryField
import com.amplifyframework.datastore.generated.model.Invite
import com.amplifyframework.datastore.generated.model.UserChannel
import com.amplifyframework.kotlin.api.KotlinApiFacade
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class ChatContactsAmplify @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    private val localRepository: LocalRepository,
    @param:Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : ChatContacts {
    override suspend fun fetchChats(): List<UserChannel> = withContext(ioDispatcher) {
        try {
            val predicate = QueryField.field("userId").eq(localRepository.getUserId().first())
            val response = amplifyApi.query(ModelQuery.list(UserChannel::class.java, predicate))
            response.data.items.toList()
        } catch (ae: AmplifyException) {
            emptyList()
        }
    }

    override fun fetchGroupsChats() {
        TODO("Not yet implemented")
    }

    override fun createMessage() {
        TODO("Not yet implemented")
    }

    override fun deleteMessage() {
        TODO("Not yet implemented")
    }

    override fun startChat() {
        println("ChatContactsAmplify ***** UID")
    }

    fun String.createDirectChatChannelName(senderUserId: String, receiverUserId: String): String {

        return ""
    }

    companion object {
        const val SEPARATOR = ":"
    }
}