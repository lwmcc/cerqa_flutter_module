package com.mccartycarclub.repository.shared

import android.net.Uri
import com.cerqa.domain.model.LocalDeviceContacts as SharedLocalDeviceContacts
import com.cerqa.domain.repository.LocalRepository as SharedLocalRepository
import com.mccartycarclub.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Android implementation of shared LocalRepository interface
 * Adapts existing Android LocalRepository to shared interface
 */
class SharedLocalRepositoryImpl @Inject constructor(
    private val androidLocalRepository: LocalRepository
) : SharedLocalRepository {

    override fun getUserId(): Flow<String?> {
        return androidLocalRepository.getUserId()
    }

    override suspend fun getAllContacts(): List<SharedLocalDeviceContacts> {
        return androidLocalRepository.getAllContacts().map { androidContact ->
            SharedLocalDeviceContacts(
                name = androidContact.name,
                phoneNumbers = androidContact.phoneNumbers.filterNotNull(),
                photoUri = androidContact.photoUri?.toString(),
                thumbnailUri = androidContact.thumbnailUri?.toString()
            )
        }
    }

    override suspend fun setLocalUserId(userId: String) {
        androidLocalRepository.setLocalUserId(userId)
    }

    override fun getUserName(): Flow<String?> {
        // Android LocalRepository doesn't have getUserName, return null for now
        return flow { emit(null) }
    }

    override suspend fun setUserData(userId: String, userName: String) {
        // Android LocalRepository only has setLocalUserId
        // Store both if needed, for now just store userId
        androidLocalRepository.setLocalUserId(userId)
    }

    override suspend fun clearUserData() {
        // Implementation would clear user data from DataStore
        // For now, this is a placeholder
        // Could set userId to empty string or null
    }
}
