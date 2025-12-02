package com.cerqa.platform

import com.cerqa.models.DeviceContact

/**
 * Platform-specific interface for accessing device contacts.
 * Implementations for Android and iOS will provide platform-specific
 * access to the device's contact list.
 */
expect class DeviceContactsProvider {
    /**
     * Get all contacts from the device's contact list.
     * Returns a list of DeviceContact objects containing name, phone numbers, and photo URIs.
     */
    suspend fun getDeviceContacts(): List<DeviceContact>

    /**
     * Request permission to access contacts (if needed on the platform).
     * Returns true if permission is granted, false otherwise.
     */
    suspend fun requestContactsPermission(): Boolean

    /**
     * Check if contacts permission is granted.
     */
    fun hasContactsPermission(): Boolean
}
