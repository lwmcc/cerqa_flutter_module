package com.cerqa.repository

import com.cerqa.models.Contact
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
            Contact(
                id = "1",
                userId = "user-1",
                firstName = "John",
                lastName = "Doe",
                name = "John Doe",
                phone = "+1-555-0100",
                email = "john.doe@example.com",
                userName = "johndoe",
                avatarUri = null,
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z"
            ),
            Contact(
                id = "2",
                userId = "user-2",
                firstName = "Jane",
                lastName = "Smith",
                name = "Jane Smith",
                phone = "+1-555-0101",
                email = "jane.smith@example.com",
                userName = "janesmith",
                avatarUri = null,
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z"
            ),
            Contact(
                id = "3",
                userId = "user-3",
                firstName = "Bob",
                lastName = "Johnson",
                name = "Bob Johnson",
                phone = "+1-555-0102",
                email = "bob.johnson@example.com",
                userName = "bobjohnson",
                avatarUri = null,
                createdAt = "2024-01-01T00:00:00Z",
                updatedAt = "2024-01-01T00:00:00Z"
            )
        )

        return Result.success(mockContacts)
    }
}
