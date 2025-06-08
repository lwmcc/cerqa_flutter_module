package com.mccartycarclub.domain.helpers

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.mccartycarclub.domain.model.LocalContact
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsHelper @Inject constructor(private val context: Context) : DeviceContacts {

    override fun getAllContacts(localContacts: (List<LocalContact>) -> Unit) {

        val contacts = mutableListOf<LocalContact>()
        val contentResolver: ContentResolver = context.contentResolver

        contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME),
            null, null, null
        )?.use {
            val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (it.moveToNext()) {

                val phoneCursor: Cursor? = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(it.getString(idIndex)), null
                )

                // TODO: revisit because of inner loop
                phoneCursor?.use { phoneIt ->
                    val numberIndex =
                        phoneIt.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (phoneIt.moveToNext()) {
                        contacts.add(
                            LocalContact(
                                name = it.getString(nameIndex),
                                phoneNumber = phoneIt.getString(numberIndex),
                            )
                        )
                    }
                }
            }
            localContacts(contacts)
        }
    }
}
