package com.mccartycarclub.pigeon

class ChatHostApi : CerqaHostApi {

    override fun createChat(receiverUserId: String) {
        // loggedInUser and participant
      //  println("ChatHostApi ***** CREATE CHAT $receiverUserId")
    }

    override fun deleteChat() {
        TODO("Not yet implemented")
    }

    override fun createGroup() {
        TODO("Not yet implemented")
    }

    override fun deleteGroup() {
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

    override fun deleteGroupMessage() {
        TODO("Not yet implemented")
    }
}

data class ChatContact(
    var userName: String?,
    var phoneNumber: String?,
    var userId: String?,
    var avatarUri: String?,
)