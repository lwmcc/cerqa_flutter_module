package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.cerqa.graphql.CheckGroupNameExistsQuery
import com.cerqa.graphql.CreateGroupWithMembersMutation
import com.cerqa.graphql.ListUserGroupsQuery

/**
 * Apollo implementation of GroupRepository
 */
class GroupRepositoryImpl(
    private val apolloClient: ApolloClient
) : GroupRepository {

    override suspend fun checkGroupNameExists(groupName: String): Result<Boolean> {
        return try {
            // Note: This query may return cached results. However, the backend Lambda function
            // also validates for duplicate names, so the backend will reject duplicates even if
            // the frontend cache is stale.
            val response = apolloClient
                .query(CheckGroupNameExistsQuery(name = groupName))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                Result.failure(Exception(errors ?: "Unknown error checking group name"))
            } else {
                // If items list is not empty, the name exists
                val exists = response.data?.listGroups?.items?.isNotEmpty() == true
                Result.success(exists)
            }
        } catch (e: Exception) {
            println("GroupRepository: checkGroupNameExists error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun createGroup(groupName: String, memberUserIds: List<String>, creatorUserId: String): Result<String> {
        return try {
            val response = apolloClient
                .mutation(CreateGroupWithMembersMutation(
                    groupName = groupName,
                    memberUserIds = memberUserIds,
                    creatorUserId = creatorUserId
                ))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("GroupRepository: createGroup error - $errors")
                Result.failure(Exception(errors ?: "Unknown error creating group"))
            } else {
                val result = response.data?.createGroupWithMembers
                if (result?.success == true && result.groupId != null) {
                    println("GroupRepository: ${result.message}")
                    println("GroupRepository: Group ID: ${result.groupId}, Channel ID: ${result.channelId}, Members: ${result.memberCount}")
                    Result.success(result.groupId)
                } else {
                    val errorMsg = result?.error ?: "Group creation failed"
                    println("GroupRepository: createGroup failed - $errorMsg")
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            println("GroupRepository: createGroup error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getUserGroups(userId: String): Result<List<ListUserGroupsQuery.Item>> {
        return try {
            val filter = Optional.present(
                com.cerqa.graphql.type.ModelUserGroupFilterInput(
                    userId = Optional.present(
                        com.cerqa.graphql.type.ModelIDInput(
                            eq =Optional.present(userId)
                        )
                    )
                )
            )

            val response = apolloClient
                .query(ListUserGroupsQuery(
                        filter = filter,
                        limit = Optional.Absent,
                        nextToken = Optional.Absent
                    ))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("GroupRepository: getUserGroups error - $errors")
                Result.failure(Exception(errors ?: "Unknown error fetching user groups"))
            } else {
                val items = response.data?.listUserGroups?.items?.filterNotNull() ?: emptyList()
                println("GroupRepository: Fetched ${items.size} groups for user $userId")
                Result.success(items)
            }
        } catch (e: Exception) {
            println("GroupRepository: getUserGroups error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
