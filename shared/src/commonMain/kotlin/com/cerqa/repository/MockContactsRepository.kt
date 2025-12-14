package com.cerqa.repository

import com.cerqa.models.Contact
import com.cerqa.models.CurrentContact
import kotlinx.coroutines.delay

/**
 * Mock repository for testing the contacts UI without a backend.
 * Replace with ApolloContactsRepository once your AppSync schema is ready.
 */
class MockContactsRepository {
    /**
     * Returns mock contacts for testing.
     */
    suspend fun fetchContacts(): Result<List<Contact>> {
        // Simulate network delay
        delay(1000)

        val mockContacts = listOf(
            CurrentContact(
                contactId = "1",
                userId = "user-1",
                userName = "johndoe",
                name = "John Doe",
                phoneNumber = "+1-555-0100",
                avatarUri = null,
                //createdAt = "2024-01-01T00:00:00Z"
            ),
            CurrentContact(
                contactId = "2",
                userId = "user-2",
                userName = "janesmith",
                name = "Jane Smith",
                phoneNumber = "+1-555-0101",
                avatarUri = null,
                //createdAt = "2024-01-01T00:00:00Z"
            ),
            CurrentContact(
                contactId = "3",
                userId = "user-3",
                userName = "bobjohnson",
                name = "Bob Johnson",
                phoneNumber = "+1-555-0102",
                avatarUri = null,
                //createdAt = "2024-01-01T00:00:00Z"
            )
        )

        return Result.success(mockContacts)
    }
}
