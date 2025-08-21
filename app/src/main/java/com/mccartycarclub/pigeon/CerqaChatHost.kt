package com.mccartycarclub.pigeon


// TODO: split this up
class ChatHostApi : CerqaHostApi {

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
        println("ChatHostApi ***** CHATS FROM ANDROID")
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