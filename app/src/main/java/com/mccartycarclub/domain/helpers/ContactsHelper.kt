package com.mccartycarclub.domain.helpers

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import androidx.core.net.toUri
import com.mccartycarclub.repository.RemoteRepo
import com.mccartycarclub.utils.phoneNumberParser

@Singleton
class ContactsHelper @Inject constructor(
    private val context: Context,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
) : DeviceContacts {
    override suspend fun getDeviceContacts(): List<LocalDeviceContacts> =
        withContext(ioDispatcher) {

            val contacts = mutableListOf<LocalDeviceContacts>()
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
                    val thumbnailIndex =
                        cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI)
                    val photoUriIndex = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)
                    val nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)

                    while (cursor.moveToNext()) {
                        val contactId = cursor.getString(idIndex)
                        val name = cursor.getString(nameIndex)
                        val thumbnailUri = cursor.getString(thumbnailIndex)?.toUri()
                        val photoUri = cursor.getString(photoUriIndex)?.toUri()

                        val phoneNumbers = mutableListOf<String?>()

                        val phoneCursor: Cursor? = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                            arrayOf(contactId), null
                        )

                        phoneCursor?.use { phoneIt ->
                            val numberIndex =
                                phoneIt.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                            while (phoneIt.moveToNext()) {
                                phoneIt.getString(numberIndex)?.let { number ->
                                    if (number.isNotBlank()) {
                                        phoneNumberParser(number)?.let { num ->
                                            phoneNumbers.add(num)
                                        } ?: phoneNumbers.add(null)
                                    }
                                }
                            }
                        }

                        if (phoneNumbers.isNotEmpty()) {
                            contacts.add(
                                LocalDeviceContacts(
                                    name = name,
                                    phoneNumbers = phoneNumbers,
                                    photoUri = photoUri,
                                    thumbnailUri = thumbnailUri
                                )
                            )
                        }
                    }
                }
                contacts
            } catch (rte: RuntimeException) {
                emptyList()
            }
        }
}

data class LocalDeviceContacts(
    val name: String,
    val phoneNumbers: List<String?> = emptyList(),
    val photoUri: Uri?,
    val thumbnailUri: Uri?
)
