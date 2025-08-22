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

    override fun fetchGroupChats(callback: (Result<List<Group>>) -> Unit) {
        callback(Result.success(chatRepository.fetchGroups()))
    }

    override fun fetchGroupConversation() {
        TODO("Not yet implemented")
    }

    override fun deleteGroup() {
        TODO("Not yet implemented")
    }

    override fun fetchContacts(callback: (Result<List<Contact>>) -> Unit) {
        callback(Result.success(chatRepository.fetchContacts()))
    }

    override fun fetchChats(callback: (Result<List<Chat>>) -> Unit) {
        callback(Result.success(chatRepository.fetchChats()))
    }

    override fun fetchDirectConversation(
        receiverUserId: String,
        callback: (
            Result<List<Message>>,
        ) -> Unit
    ) {
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