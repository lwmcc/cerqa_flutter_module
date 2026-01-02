package com.mccartycarclub.repository.shared

import com.cerqa.domain.model.Contact as SharedContact
import com.cerqa.domain.model.ContactsWrapper as SharedContactsWrapper
import com.cerqa.domain.model.DeviceContact as SharedDeviceContact
import com.cerqa.domain.model.LocalDeviceContacts as SharedLocalDeviceContacts
import com.cerqa.domain.model.NetDeleteResult as SharedNetDeleteResult
import com.cerqa.domain.model.NetworkResponse as SharedNetworkResponse
import com.cerqa.domain.model.SearchContact as SharedSearchContact
import com.cerqa.domain.model.StandardContact
import com.cerqa.domain.repository.ContactsRepository as SharedContactsRepository
import com.mccartycarclub.repository.ContactsRepository
import com.mccartycarclub.repository.NetDeleteResult
import com.mccartycarclub.repository.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Android implementation of shared ContactsRepository interface
 * Adapts existing Android ContactsRepository to shared interface
 */
class SharedContactsRepositoryImpl @Inject constructor(
    private val androidContactsRepository: ContactsRepository
) : SharedContactsRepository {

    override fun createContact(
        senderUserId: String,
        loggedInUserId: String
    ): Flow<SharedNetDeleteResult> {
        return androidContactsRepository.createContact(senderUserId, loggedInUserId)
            .map { it.toShared() }
    }

    override fun contactExists(
        senderUserId: String,
        receiverUserId: String
    ): Flow<Boolean> {
        return androidContactsRepository.contactExists(senderUserId, receiverUserId)
    }

    override fun fetchAllContacts(): Flow<SharedNetworkResponse<List<SharedContact>>> {
        return androidContactsRepository.fetchAllContacts()
            .map { response ->
                when (response) {
                    is NetworkResponse.Success -> SharedNetworkResponse.Success(
                        response.data?.map { it.toShared() }
                    )
                    is NetworkResponse.Error -> SharedNetworkResponse.Error(response.exception)
                    NetworkResponse.NoInternet -> SharedNetworkResponse.NoInternet
                }
            }
    }

    override suspend fun fetchUsersByPhoneNumber(): Pair<List<SharedSearchContact>, List<SharedSearchContact>> {
        val (appUsers, nonAppUsers) = androidContactsRepository.fetchUsersByPhoneNumber()
        return Pair(
            appUsers.map { it.toShared() },
            nonAppUsers.map { it.toShared() }
        )
    }

    override suspend fun getDeviceContacts(): SharedContactsWrapper {
        val wrapper = androidContactsRepository.getDeviceContacts()
        return SharedContactsWrapper(
            appUsers = wrapper.appUsers.map { it.toShared() },
            nonAppUsers = wrapper.nonAppUsers.map { it.toShared() }
        )
    }

    override suspend fun createContactFromUser(userId: String) {
        // Implementation would use Amplify User model
        // For now, this is a placeholder
        TODO("Implementation requires Amplify User model conversion")
    }
}

// Extension functions to convert Android models to shared models
private fun com.mccartycarclub.repository.Contact.toShared(): SharedContact {
    return StandardContact(
        contactId = this.contactId,
        userId = this.userId,
        userName = this.userName,
        name = this.name,
        avatarUri = this.avatarUri,
        createdAt = this.createdAt,
        phoneNumber = this.phoneNumber
    )
}

private fun com.mccartycarclub.domain.model.SearchContact.toShared(): SharedSearchContact {
    return SharedSearchContact(
        userId = "", // Android SearchContact doesn't have userId
        userName = "", // Android SearchContact doesn't have userName
        name = this.name,
        phone = this.phoneNumbers.firstOrNull() ?: "",
        avatarUri = this.avatarUri?.toString()
    )
}

private fun com.mccartycarclub.domain.model.DeviceContact.toShared(): SharedDeviceContact {
    return SharedDeviceContact(
        name = this.name,
        phoneNumber = this.phoneNumbers.firstOrNull() ?: "",
        isAppUser = false
    )
}

private fun NetDeleteResult.toShared(): SharedNetDeleteResult {
    return when (this) {
        NetDeleteResult.Success -> SharedNetDeleteResult.Success
        is NetDeleteResult.Error -> SharedNetDeleteResult.Error(this.exception)
        NetDeleteResult.NoInternet -> SharedNetDeleteResult.NoInternet
        NetDeleteResult.Pending -> SharedNetDeleteResult.Pending
    }
}
