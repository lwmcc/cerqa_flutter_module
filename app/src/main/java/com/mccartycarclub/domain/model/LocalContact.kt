package com.mccartycarclub.domain.model

import android.net.Uri

data class LocalContact(
    val name: String,
    val phoneNumber: String,
)

data class DeviceContact(
    val name: String,
    val phoneNumbers: List<String?> = emptyList(),
    val avatarUri: Uri?,
    val thumbnailUri: Uri?,
)

data class SearchContact(
    val name: String,
    val phoneNumbers: List<String?> = emptyList(),
    val avatarUri: Uri?,
    val thumbnailUri: Uri?,
    val connectButtonEnabled: Boolean = true,
)