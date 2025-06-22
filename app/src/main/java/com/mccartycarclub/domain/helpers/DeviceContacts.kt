package com.mccartycarclub.domain.helpers

interface DeviceContacts {
    suspend fun getDeviceContacts(): List<LocalDeviceContacts>
}
