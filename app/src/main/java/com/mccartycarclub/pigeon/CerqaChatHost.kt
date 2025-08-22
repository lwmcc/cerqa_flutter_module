package com.mccartycarclub.pigeon

import com.mccartycarclub.repository.ChatRepository
import javax.inject.Inject


// TODO: split this up
class ChatHostApi @Inject constructor(private val chatRepository: ChatRepository) : CerqaHostApi {

    override fun createChat(receiverUserId: String) {
        // loggedInUser and participant
        //  println("ChatHostApi ***** CREATE CHAT $receiverUserId")
    }

    override fun deleteChat() {
        TODO("Not yet implemented")
    }

    override fun doesGroupNameExist(groupName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun fetchGroupChats() {
        TODO("Not yet implemented")
    }

    override fun fetchGroupConversation() {
        TODO("Not yet implemented")
    }

    override fun deleteGroup() {
        TODO("Not yet implemented")
    }

    override fun fetchContacts(): List<Contact> {
        TODO("Not yet implemented")
    }

    override fun fetchChats(callback: (Result<List<Chat>>) -> Unit) {
        callback(Result.success(chatRepository.fetchChats()))
    }

    override fun fetchDirectConversation(receiverUserId: String) {
        TODO("Not yet implemented")
    }

    override fun createMessage() {
        TODO("Not yet implemented")
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