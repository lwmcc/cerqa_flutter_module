package com.cerqa.models

import kotlinx.serialization.Serializable

data class UserData(
    val userId: String?,
    val userName: String?,
    val userEmail: String?,
    val createdAt: String?,
    val avatarUri: String?,
)

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
 * Base Contact sealed class representing different contact states
 */
@Serializable
sealed class Contact {
    abstract val contactId: String
    abstract val userId: String
    abstract val userName: String?
    abstract val name: String?
    abstract val avatarUri: String?
    // abstract val createdAt: String?
    abstract val phoneNumber: String?
}

/**
 * Represents a received connection invite
 */
@Serializable
data class ReceivedContactInvite(
    override val contactId: String,
    override val userId: String,
    override val userName: String?,
    override val name: String?,
    override val avatarUri: String?,
    // override val createdAt: String?,
    override val phoneNumber: String?,
) : Contact()

/**
 * Represents a sent connection invite
 */
@Serializable
data class SentInviteContactInvite(
    val senderUserId: String,
    override val contactId: String,
    override val userId: String,
    override val userName: String?,
    override val name: String?,
    override val avatarUri: String?,
    // override val createdAt: String?,
    override val phoneNumber: String?,
) : Contact()

/**
 * Represents a current/accepted contact
 */
@Serializable
data class CurrentContact(
    override val contactId: String,
    override val userId: String,
    override val userName: String?,
    override val name: String?,
    override val avatarUri: String?,
    // override val createdAt: String?,
    override val phoneNumber: String?,
) : Contact()

/**
 * Contact type enum for UI state management
 */
@Serializable
enum class ContactType {
    RECEIVED, SENT, CURRENT
}

/**
 * Search user result with connection state
 */
@Serializable
data class SearchUser(
    val id: String,
    val userName: String?,
    val avatarUri: String?,
    val userId: String,
    val phone: String?,
    val connectButtonEnabled: Boolean = true,
    val contactType: ContactType?,
)

/**
 * Device contact from phone contacts
 */
@Serializable
data class DeviceContact(
    val name: String,
    val phoneNumbers: List<String>,
    val avatarUri: String? = null,
    val thumbnailUri: String? = null,
)

/**
 * Wrapper for categorized device contacts
 */
@Serializable
data class ContactsWrapper(
    val appUsers: List<DeviceContact>,
    val nonAppUsers: List<DeviceContact>,
)

/**
 * Invite model matching backend schema
 */
@Serializable
data class Invite(
    val id: String,
    val userId: String,
    val senderId: String,
    val receiverId: String,
    val createdAt: String? = null,
    val updatedAt: String? = null,
)


