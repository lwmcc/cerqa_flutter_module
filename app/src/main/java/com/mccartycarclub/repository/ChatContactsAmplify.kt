package com.mccartycarclub.repository

import com.amplifyframework.core.model.ModelList
import com.amplifyframework.kotlin.api.KotlinApiFacade
import javax.inject.Inject

class ChatContactsAmplify @Inject constructor(private val amplifyApi: KotlinApiFacade,): ChatContacts {
    override fun fetchChats() {
        //amplifyApi.query(ModelList)
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
        TODO("Not yet implemented")
    }
}