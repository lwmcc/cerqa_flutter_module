package com.mccartycarclub.repository

import com.mccartycarclub.domain.UserPreferencesManager
import com.mccartycarclub.domain.helpers.DeviceContacts
import com.mccartycarclub.domain.helpers.LocalDeviceContacts
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repo @Inject constructor(
    private val contactsHelper: DeviceContacts,
    private val userPreferencesManager: UserPreferencesManager,
) : LocalRepository {
    override suspend fun getAllContacts(): List<LocalDeviceContacts> {
        return contactsHelper.getDeviceContacts()
    }

    override suspend fun setLocalUserId(userId: String) {
        userPreferencesManager.setLocalUserId(userId)
    }

    override fun getUserId(): Flow<String?> = userPreferencesManager.getUserId()
}

