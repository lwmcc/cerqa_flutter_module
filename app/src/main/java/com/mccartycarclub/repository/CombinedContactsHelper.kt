package com.mccartycarclub.repository

import kotlinx.coroutines.flow.Flow

interface CombinedContactsHelper {
    fun fetchAllContacts(): Flow<NetworkResponse<List<Contact>>>
    fun fetchContactAppUsers()
}