package com.mccartycarclub.repository

import kotlinx.coroutines.flow.Flow

interface RemoteRepo {
    suspend fun contactExists(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean>

    suspend fun hasExistingInvite(
        senderUserId: String,
        receiverUserId: String,
    ): Flow<Boolean>
}