package com.mccartycarclub.pigeon

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mccartycarclub.domain.helpers.createChannelId
import com.mccartycarclub.domain.helpers.toPigeonContact
import com.mccartycarclub.pigeon.Contact
import com.mccartycarclub.repository.ChatRepository
import com.mccartycarclub.repository.ContactsRepository
import com.mccartycarclub.repository.LocalRepository
import com.mccartycarclub.repository.NetworkResponse
import com.mccartycarclub.viewmodels.ChatViewModel
import com.mccartycarclub.viewmodels.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named


data class ChatUiState(
    val chats: List<Chat> = emptyList(),
)

// TODO: split this up
class ChatHostApi @Inject constructor(
    private val chatRepository: ChatRepository,
    private val contactsRepository: ContactsRepository,
    private val localRepository: LocalRepository,
    @param:Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : CerqaHostApi {

    private val scope = MainScope()

    var chatUiState by mutableStateOf(ChatUiState())
        private set

    override fun createChat(receiverUserId: String) {
        TODO("Not yet implemented")
    }

    override fun deleteChat() {
        TODO("Not yet implemented")
    }

    override fun doesGroupNameExist(groupName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun fetchGroupChats(callback: (Result<List<Group>>) -> Unit) {
        callback(Result.success(chatRepository.fetchGroups()))
        //callback(Result.success(emptyList<Group>()))
    }

    override fun fetchGroupConversation() {
        TODO("Not yet implemented")
    }

    override fun deleteGroup() {
        TODO("Not yet implemented")
    }

    init {
        scope.launch {
            chatRepository.fetchChats().collect { chats ->
                chatUiState = chatUiState.copy(chats = chats)
            }

            // TODO: push updates to flutter
        }
    }

    override fun fetchContacts(callback: (Result<List<Contact>>) -> Unit) {
        scope.launch {
            try {
                // Check if user is authenticated before fetching contacts
                val userId = localRepository.getUserId().firstOrNull()
                if (userId.isNullOrBlank()) {
                    // Return empty list if not authenticated
                    callback(Result.success(emptyList()))
                    return@launch
                }

                contactsRepository.fetchAllContacts().collect { result ->
                    when(result) {
                        is NetworkResponse.Error -> {
                            // Return empty list on error instead of crashing
                            callback(Result.success(emptyList()))
                        }

                        NetworkResponse.NoInternet -> {
                            // Return empty list when no internet
                            callback(Result.success(emptyList()))
                        }

                        is NetworkResponse.Success -> {
                            callback(Result.success(toPigeonContact(result.data)))
                        }
                    }
                }
            } catch (e: Exception) {
                // Catch any unexpected errors and return empty list
                callback(Result.success(emptyList()))
            }
        }
    }

    override fun fetchChats(callback: (Result<List<Chat>>) -> Unit) {
        scope.launch {
            callback(Result.success(chatUiState.chats))
        }
    }

    // TODO: this is not correct, redo for direct messages
    override fun fetchDirectMessages(callback: (Result<List<ChannelsItem>>) -> Unit) {
        scope.launch {
            chatRepository.fetchDirectMessages().collect { messages ->
                callback(Result.success(messages))
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