package com.mccartycarclub.pigeon

import com.mccartycarclub.domain.helpers.createChannelId
import com.mccartycarclub.domain.helpers.toPigeonContact
import com.mccartycarclub.pigeon.Contact
import com.mccartycarclub.repository.ChatRepository
import com.mccartycarclub.repository.ContactsRepository
import com.mccartycarclub.repository.LocalRepository
import com.mccartycarclub.repository.NetworkResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named


// TODO: split this up
class ChatHostApi @Inject constructor(
    private val chatRepository: ChatRepository,
    private val contactsRepository: ContactsRepository,
    private val localRepository: LocalRepository,
    @param:Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : CerqaHostApi {

    private val scope = MainScope()

    override fun createChat(receiverUserId: String) {

    }

    override fun deleteChat() {
        TODO("Not yet implemented")
    }

    override fun doesGroupNameExist(groupName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun fetchGroupChats(callback: (Result<List<Group>>) -> Unit) {
        //callback(Result.success(chatRepository.fetchGroups()))
        callback(Result.success(emptyList<Group>()))
    }

    override fun fetchGroupConversation() {
        TODO("Not yet implemented")
    }

    override fun deleteGroup() {
        TODO("Not yet implemented")
    }

    override fun fetchContacts(callback: (Result<List<Contact>>) -> Unit) {
        scope.launch {
            contactsRepository.fetchAllContacts().collect { result ->
                when(result) {
                    is NetworkResponse.Error -> {
                        // TODO: handle error
                        println("ChatHostApi ***** ERROR")
                    }

                    NetworkResponse.NoInternet -> {
                        // TODO: handle error
                        println("ChatHostApi ***** NO INTERNET")
                    }

                    is NetworkResponse.Success -> {
                        //callback(Result.success(toPigeonContact(result.data)))
                        callback(Result.success(emptyList<Contact>()))
                    }
                }
            }
        }
    }

    override fun fetchChats(callback: (Result<List<Chat>>) -> Unit) {
        scope.launch {
            // callback(Result.success(chatRepository.fetchChats()))
            callback(Result.success(emptyList()))
        }
    }

    override fun fetchDirectMessages(
        receiverUserId: String,
        callback: (Result<List<Message>>) -> Unit
    ) {
        scope.launch {
            chatRepository.fetchDirectMessages(receiverUserId).collect { messages ->
                //callback(Result.success(messages))
                callback(Result.success(emptyList()))
            }
        }
    }

    override fun createMessage(
        message: String,
        receiverUserId: String,
        callback: (
            Result<Boolean>
        ) -> Unit
    ) {
        scope.launch {
            try {
                val sender = localRepository.getUserId().firstOrNull()

                if (sender.isNullOrBlank()) {
                    callback(Result.failure(IllegalStateException("senderUserId is missing")))
                    return@launch
                }
                // TODO: move to repo
                val channelId = sender.createChannelId(receiverUserId)

                val success = chatRepository.createMessage(channelId, message, receiverUserId).first()
                if (success) {
                    callback(Result.success(success))
                } else {
                    callback(Result.failure(Exception("Message could not be sent")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }

    override fun deleteMessage() {
        TODO("Not yet implemented")
    }

    override fun createGroupMessage() {
        TODO("Not yet implemented")
    }

    override fun createGroup(groupName: String) {
        TODO("Not yet implemented")
    }

    override fun deleteGroupMessage() {
        TODO("Not yet implemented")
    }

    override fun fetchGroupMessage() {
        TODO("Not yet implemented")
    }
}

data class ChatContact(
    var userName: String?,
    var phoneNumber: String?,
    var userId: String?,
    var avatarUri: String?,
)