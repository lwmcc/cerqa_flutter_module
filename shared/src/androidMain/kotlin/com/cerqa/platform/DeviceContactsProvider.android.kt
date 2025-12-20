package com.cerqa.platform

import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.cerqa.models.DeviceContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Android implementation of DeviceContactsProvider.
 * Accesses device contacts using Android's ContactsContract ContentProvider.
 */
actual class DeviceContactsProvider(private val context: Context) {

    /**
     * Get all contacts from the device with phone numbers.
     */
    actual suspend fun getDeviceContacts(): List<DeviceContact> = withContext(Dispatchers.IO) {
        println("DeviceContactsProvider.android: getDeviceContacts called")
        if (!hasContactsPermission()) {
            println("DeviceContactsProvider.android: READ_CONTACTS permission NOT granted")
            return@withContext emptyList()
        }
        println("DeviceContactsProvider.android: READ_CONTACTS permission granted, fetching contacts...")

        val contacts = mutableListOf<DeviceContact>()
        val contentResolver: ContentResolver = context.contentResolver

        return@withContext try {
            contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                    ContactsContract.Contacts.PHOTO_URI,
                    ContactsContract.Contacts.DISPLAY_NAME,
                ),
                "${ContactsContract.Contacts.HAS_PHONE_NUMBER} > 0",
                null,
                null,
            )?.use { cursor ->
                val idIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID)
                val thumbnailIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
                val photoUriIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
                val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val contactId = cursor.getString(idIndex)
                    val name = cursor.getString(nameIndex) ?: ""
                    val thumbnailUri = cursor.getString(thumbnailIndex)
                    val photoUri = cursor.getString(photoUriIndex)

                    val phoneNumbers = getPhoneNumbers(contentResolver, contactId)

                    if (phoneNumbers.isNotEmpty()) {
                        contacts.add(
                            DeviceContact(
                                name = name,
                                phoneNumbers = phoneNumbers,
                                avatarUri = photoUri,
                                thumbnailUri = thumbnailUri
                            )
                        )
                    }
                }
            }
            println("DeviceContactsProvider.android: successfully loaded ${contacts.size} contacts")
            contacts
        } catch (e: SecurityException) {
            // Permission denied
            println("DeviceContactsProvider.android: SecurityException - ${e.message}")
            emptyList()
        } catch (e: RuntimeException) {
            // Other runtime errors
            println("DeviceContactsProvider.android: RuntimeException - ${e.message}")
            emptyList()
        }
    }

    /**
     * Get phone numbers for a specific contact.
     */
    private fun getPhoneNumbers(contentResolver: ContentResolver, contactId: String): List<String> {
        val phoneNumbers = mutableListOf<String>()

        val phoneCursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
            arrayOf(contactId),
            null
        )

        phoneCursor?.use { phoneIt ->
            val numberIndex = phoneIt.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (phoneIt.moveToNext()) {
                val number = phoneIt.getString(numberIndex)
                if (!number.isNullOrBlank()) {
                    // Clean up phone number (remove spaces, dashes, etc.)
                    val cleanedNumber = cleanPhoneNumber(number)
                    if (cleanedNumber.isNotEmpty()) {
                        phoneNumbers.add(cleanedNumber)
                    }
                }
            }
        }

        return phoneNumbers
    }

    /**
     * Clean phone number by removing non-digit characters (except +).
     */
    private fun cleanPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace(Regex("[^+\\d]"), "")
    }

    /**
     * Request contacts permission (not directly implemented here, as it requires Activity context).
     * In practice, this should be called from the UI layer.
     */
    actual suspend fun requestContactsPermission(): Boolean {
        // On Android, permission requests must be made from an Activity
        // This should be handled by the platform-specific UI layer
        return hasContactsPermission()
    }

    /**
     * Check if READ_CONTACTS permission is granted.
     */
    actual fun hasContactsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
