package com.cerqa.platform

import com.cerqa.models.DeviceContact
import kotlinx.cinterop.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import platform.Contacts.*
import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * iOS implementation of DeviceContactsProvider.
 * Accesses device contacts using iOS's Contacts framework.
 */
@OptIn(ExperimentalForeignApi::class)
actual class DeviceContactsProvider {

    private val contactStore = CNContactStore()

    /**
     * Get all contacts from the device with phone numbers.
     */
    actual suspend fun getDeviceContacts(): List<DeviceContact> = withContext(Dispatchers.IO) {
        if (!hasContactsPermission()) {
            // Try to request permission if not granted
            val granted = requestContactsPermission()
            if (!granted) {
                return@withContext emptyList()
            }
        }

        val contacts = mutableListOf<DeviceContact>()

        try {
            val keysToFetch = listOf(
                CNContactGivenNameKey,
                CNContactFamilyNameKey,
                CNContactPhoneNumbersKey,
                CNContactImageDataAvailableKey,
                CNContactImageDataKey,
                CNContactThumbnailImageDataKey
            )

            val request = CNContactFetchRequest(keysToFetch = keysToFetch)

            memScoped {
                val errorPtr = alloc<ObjCObjectVar<NSError?>>()

                contactStore.enumerateContactsWithFetchRequest(request, errorPtr.ptr) { contact, stopPtr ->
                    contact?.let {
                        val givenName = it.givenName ?: ""
                        val familyName = it.familyName ?: ""
                        val fullName = "$givenName $familyName".trim().ifEmpty { "Unknown" }

                        val phoneNumbers = mutableListOf<String>()
                        val phoneNumbersArray = it.phoneNumbers as? List<CNLabeledValue>

                        phoneNumbersArray?.forEach { labeledValue ->
                            val phoneNumber = labeledValue.value as? CNPhoneNumber
                            phoneNumber?.stringValue?.let { number ->
                                val cleanedNumber = cleanPhoneNumber(number)
                                if (cleanedNumber.isNotEmpty()) {
                                    phoneNumbers.add(cleanedNumber)
                                }
                            }
                        }

                        if (phoneNumbers.isNotEmpty()) {
                            // Get thumbnail/avatar if available
                            val thumbnailData = if (it.imageDataAvailable) {
                                it.thumbnailImageData?.toString()
                            } else null

                            contacts.add(
                                DeviceContact(
                                    name = fullName,
                                    phoneNumbers = phoneNumbers,
                                    avatarUri = thumbnailData,
                                    thumbnailUri = thumbnailData
                                )
                            )
                        }
                    }
                }

                errorPtr.value?.let { error ->
                    println("Error fetching contacts: ${error.localizedDescription}")
                }
            }
        } catch (e: Exception) {
            println("Exception fetching contacts: ${e.message}")
        }

        contacts
    }

    /**
     * Clean phone number by removing non-digit characters (except +).
     */
    private fun cleanPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace(Regex("[^+\\d]"), "")
    }

    /**
     * Request contacts permission.
     */
    actual suspend fun requestContactsPermission(): Boolean = suspendCoroutine { continuation ->
        contactStore.requestAccessForEntityType(
            CNEntityType.CNEntityTypeContacts
        ) { success, error ->
            if (error != null) {
                println("Permission error: ${error.localizedDescription}")
            }
            continuation.resume(success)
        }
    }

    /**
     * Check if contacts permission is granted.
     */
    actual fun hasContactsPermission(): Boolean {
        val status = CNContactStore.authorizationStatusForEntityType(CNEntityType.CNEntityTypeContacts)
        return status == CNAuthorizationStatusAuthorized
    }
}
