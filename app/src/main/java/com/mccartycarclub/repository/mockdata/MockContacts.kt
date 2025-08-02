package com.mccartycarclub.repository.mockdata

import com.mccartycarclub.repository.Contact
import com.mccartycarclub.repository.SentInviteContactInvite
import java.util.Calendar
import java.util.UUID

object MockContacts {
    fun loadMockContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val names = listOf(
            "Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace", "Helen", "Ian", "Julia",
            "Kyle", "Laura", "Mike", "Nina", "Oscar", "Paula", "Quincy", "Rachel", "Steve", "Tina",
            "Uma", "Victor", "Wendy", "Xavier", "Yara", "Zane", "Amber", "Blake", "Cleo", "Derek",
            "Elena", "Felix", "Gia", "Hank", "Isla", "Jack", "Kara", "Liam", "Mona", "Noah",
            "Olive", "Pete", "Queenie", "Ralph", "Sara", "Tom", "Ursula", "Vince", "Will", "Zoe"
        )

        repeat(50) { index ->
            val name = names[index % names.size]
            contacts.add(
                Contact(
                    contactId = UUID.randomUUID().toString(),
                    userId = UUID.randomUUID().toString(),
                    userName = "$name${index + 1}",
                    name = name,
                    avatarUri = "https://api.dicebear.com/7.x/personas/svg?seed=$name$index",
                    createdAt = "2025-05-08T14:30:00Z",
                    phoneNumber = "555-1212"
                )
            )
        }

        return contacts
    }

    fun loadMockSentInvites(): MutableList<SentInviteContactInvite> {
        val names = listOf(
            "Alice", "Bob", "Charlie", "David", "Eva", "Frank", "Grace", "Helen", "Ian", "Julia",
            "Kyle", "Laura", "Mike", "Nina", "Oscar", "Paula", "Quincy", "Rachel", "Steve", "Tina",
            "Uma", "Victor", "Wendy", "Xavier", "Yara", "Zane", "Amber", "Blake", "Cleo", "Derek",
            "Elena", "Felix", "Gia", "Hank", "Isla", "Jack", "Kara", "Liam", "Mona", "Noah",
            "Olive", "Pete", "Queenie", "Ralph", "Sara", "Tom", "Ursula", "Vince", "Will", "Zoe"
        )

        val calendar = Calendar.getInstance()
        val invites = mutableListOf<SentInviteContactInvite>()

        repeat(50) { index ->
            val name = names[index % names.size]
            calendar.add(Calendar.MINUTE, -index) // stagger the sentDate backwards

            invites.add(
                SentInviteContactInvite(
                    senderUserId = UUID.randomUUID().toString(),
                    contactId = UUID.randomUUID().toString(),
                    userId = UUID.randomUUID().toString(),
                    userName = "$name$index",
                    name = name,
                    avatarUri = "https://api.dicebear.com/7.x/personas/svg?seed=$name$index",
                    createdAt = "2025-05-08T12:${String.format("%02d", index % 60)}:00Z",
                    phoneNumber = "555-2222"
                )
            )
        }

        return invites
    }
}


