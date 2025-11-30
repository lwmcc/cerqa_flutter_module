package com.cerqa.models

import kotlinx.serialization.Serializable

data class UserData(val userId: String?, val userName: String?)

/**
 * User model matching AppSync schema
 */
@Serializable
data class User(
    val id: String,
    val userId: String? = null,
    val firstName: String,
    val lastName: String,
    val name: String? = null,
    val phone: String? = null,
    val userName: String? = null,
    val email: String? = null,
    val avatarUri: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * UserContact join table model
 */
@Serializable
data class UserContact(
    val id: String,
    val userId: String,
    val contactId: String,
    val user: User? = null,
    val contact: User? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * Contact is an alias for User when displaying in contacts list
 * Making it easier to work with in the UI
 */
typealias Contact = User


