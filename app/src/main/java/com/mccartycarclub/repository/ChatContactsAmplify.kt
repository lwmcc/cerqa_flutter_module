package com.mccartycarclub.repository

import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.model.ModelList
import com.amplifyframework.kotlin.api.KotlinApiFacade
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ChatContactsAmplify @Inject constructor(
    private val amplifyApi: KotlinApiFacade,
    private val localRepository: LocalRepository,
) : ChatContacts {
    override suspend fun fetchChats() {
        localRepository.getUserId().first()

       // amplifyApi.query(ModelQuery.list(modelClass, predicate))

      //  amplifyApi.query(ModelList)
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