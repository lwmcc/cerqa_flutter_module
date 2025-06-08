package com.mccartycarclub.repository


import com.amplifyframework.datastore.generated.model.User

// TODO: rename this
interface DbRepo {
    fun fetchUserGroups(userId: String)
    fun fetchUserContacts(userId: String, userContacts: (List<Contact>) -> Unit) // TODO:: move to own interface

    fun fetchUsers()
    fun fetchUser(userId: String, user: (User) -> Unit)
    fun fetchUserContacts(userId: String)
    fun fetchUserByUserName(userName: String, data: (NetResult<User?>) -> Unit)
    fun fetchUserIdFromSentInvite(rowId: String, userId: (String?) -> Unit)

    // TODO: rename
    fun acceptContactInvite(userId: String, rowId: (String?) -> Unit)


    fun updateSenderReceiverContacts()

    fun createContact(user: User)
    fun createConnectInvite(
        userIds: Pair<String?, String?>,
        hasExistingInvite: (Boolean) -> Unit,
    )

    fun contactExists(
        senderUserId: String,
        receiverUserId: String,
        hasConnection: (Boolean) -> Unit,
    )

    fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String,
        hasInvite: (Boolean) -> Unit,
    )
}
