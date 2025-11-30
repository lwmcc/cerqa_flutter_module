package com.cerqa.data

import com.apollographql.apollo.ApolloClient
import com.cerqa.auth.AuthTokenProvider
import com.cerqa.graphql.CreateUserMutation
import com.cerqa.graphql.CreateUserContactMutation
import com.cerqa.graphql.type.CreateUserInput
import com.cerqa.graphql.type.CreateUserContactInput

/**
 * Seeds test data: testUser1 (Larry) and testUser2 (LeBron) as contacts.
 * Detects which user is logged in and creates both users + contact relationship.
 */
class TestDataSeeder(
    private val apolloClient: ApolloClient,
    private val authTokenProvider: AuthTokenProvider
) {

    suspend fun seedTestData(): Result<String> {
        return try {
            val currentUserId = authTokenProvider.getCurrentUserId()
                ?: return Result.failure(Exception("Not signed in"))

            val currentUserEmail = authTokenProvider.getCurrentUserEmail() ?: ""

            println("🌱 Seeding test data for: $currentUserEmail (ID: $currentUserId)")

            // Detect which user is logged in
            val isLarry = currentUserEmail.contains("larry@cerqa.net", ignoreCase = true)
            val isLeBron = currentUserEmail.contains("admin@cerqa.com", ignoreCase = true)

            // testUser1 - Larry McCarty
            val larryId = if (isLarry) currentUserId else "larry-placeholder-id"
            createUser(
                id = larryId,
                firstName = "Larry",
                lastName = "McCarty",
                name = "LM",
                phone = "+14808104545",
                userName = "LarryM",
                email = "larry@cerqa.net",
                avatarUri = "https://www.google.com"
            )

            // testUser2 - LeBron James
            val lebronId = if (isLeBron) currentUserId else "lebron-placeholder-id"
            createUser(
                id = lebronId,
                firstName = "Lebron",
                lastName = "James",
                name = "Bron",
                phone = "+14805554545",
                userName = "KingJames",
                email = "admin@cerqa.com",
                avatarUri = "https://example.com/avatar.png"
            )

            // Make them contacts of each other
            createUserContact(userId = larryId, contactId = lebronId)
            createUserContact(userId = lebronId, contactId = larryId)

            val message = when {
                isLarry -> "✅ Larry's User record created with REAL Cognito ID!\n" +
                          "✅ LeBron created as contact (placeholder ID for now)\n" +
                          "💡 Sign in as LeBron next to update with real Cognito ID"
                isLeBron -> "✅ LeBron's User record created with REAL Cognito ID!\n" +
                           "✅ Larry created as contact (placeholder ID for now)\n" +
                           "💡 Sign in as Larry next to update with real Cognito ID"
                else -> "✅ Both users created\n⚠️ Unknown user logged in"
            }

            Result.success(message)
        } catch (e: Exception) {
            println("❌ Error seeding test data: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun createUser(
        id: String,
        firstName: String,
        lastName: String,
        name: String,
        phone: String,
        userName: String,
        email: String,
        avatarUri: String? = null
    ) {
        try {
            val response = apolloClient.mutation(
                CreateUserMutation(
                    input = CreateUserInput(
                        id = com.apollographql.apollo.api.Optional.present(id),
                        firstName = firstName,
                        lastName = lastName,
                        name = com.apollographql.apollo.api.Optional.present(name),
                        phone = com.apollographql.apollo.api.Optional.present(phone),
                        userName = com.apollographql.apollo.api.Optional.present(userName),
                        email = com.apollographql.apollo.api.Optional.present(email),
                        avatarUri = com.apollographql.apollo.api.Optional.presentIfNotNull(avatarUri)
                    )
                )
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                if (errors?.contains("already exists") != true &&
                    errors?.contains("ConditionalCheckFailedException") != true) {
                    println("⚠️ Error creating user $userName: $errors")
                } else {
                    println("✓ User already exists: $userName")
                }
            } else {
                println("✓ Created user: $userName")
            }
        } catch (e: Exception) {
            println("⚠️ Exception creating user $userName: ${e.message}")
        }
    }

    private suspend fun createUserContact(userId: String, contactId: String) {
        try {
            val response = apolloClient.mutation(
                CreateUserContactMutation(
                    input = CreateUserContactInput(
                        userId = userId,
                        contactId = contactId
                    )
                )
            ).execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                if (errors?.contains("already exists") != true &&
                    errors?.contains("ConditionalCheckFailedException") != true) {
                    println("⚠️ Error creating contact: $errors")
                } else {
                    println("✓ Contact relationship already exists")
                }
            } else {
                println("✓ Created contact relationship")
            }
        } catch (e: Exception) {
            println("⚠️ Exception creating contact: ${e.message}")
        }
    }
}
