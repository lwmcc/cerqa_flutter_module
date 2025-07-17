package com.cerqa.models

data class UserData(val userId: String?, val userName: String?)

class Contact(
    val contactId: String,
    val userId: String,
    val userName: String,
    val name: String,
    val avatarUri: String,
    val createdAt: String,
    val phoneNUmber: String,
)


