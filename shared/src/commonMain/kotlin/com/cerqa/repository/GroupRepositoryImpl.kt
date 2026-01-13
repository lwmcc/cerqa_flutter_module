package com.cerqa.repository

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.cerqa.graphql.CheckGroupNameExistsQuery
import com.cerqa.graphql.CreateGroupWithMembersMutation
import com.cerqa.graphql.DeleteChannelMutation
import com.cerqa.graphql.DeleteGroupMutation
import com.cerqa.graphql.DeleteUserChannelMutation
import com.cerqa.graphql.DeleteUserGroupMutation
import com.cerqa.graphql.ListUserGroupsQuery
import com.cerqa.graphql.UpdateGroupMutation
import com.cerqa.graphql.CreateUserGroupMutation
// TODO: Uncomment after backend deployment
// import com.cerqa.graphql.CleanupUnknownDataMutation
import com.cerqa.graphql.type.DeleteChannelInput
import com.cerqa.graphql.type.DeleteGroupInput
import com.cerqa.graphql.type.DeleteUserChannelInput
import com.cerqa.graphql.type.DeleteUserGroupInput
import com.cerqa.graphql.type.UpdateGroupInput
import com.cerqa.graphql.type.CreateUserGroupInput
import com.cerqa.graphql.type.GroupMemberRole
import com.cerqa.graphql.type.ModelUserGroupFilterInput
import com.cerqa.graphql.type.ModelIDInput

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

    override suspend fun deleteGroup(groupId: String, channelId: String): Result<Unit> {
        return try {
            // Step 1: Delete all UserGroup entries for this group
            // Note: In a real implementation, you'd want to query for all UserGroup entries first
            // For now, we rely on cascade delete or manual cleanup

            // Step 2: Delete all UserChannel entries for this channel
            // Note: Same as above - ideally query first then delete

            // Step 3: Delete the Channel
            val channelResponse = apolloClient
                .mutation(DeleteChannelMutation(
                    input = DeleteChannelInput(id = channelId)
                ))
                .execute()

            if (channelResponse.hasErrors()) {
                val errors = channelResponse.errors?.joinToString { it.message }
                println("GroupRepository: deleteGroup - channel deletion error - $errors")
                // Continue even if channel delete fails
            }

            // Step 4: Delete the Group
            val groupResponse = apolloClient
                .mutation(DeleteGroupMutation(
                    input = DeleteGroupInput(id = groupId)
                ))
                .execute()

            if (groupResponse.hasErrors()) {
                val errors = groupResponse.errors?.joinToString { it.message }
                println("GroupRepository: deleteGroup error - $errors")
                Result.failure(Exception(errors ?: "Unknown error deleting group"))
            } else {
                println("GroupRepository: Group deleted successfully")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            println("GroupRepository: deleteGroup error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun leaveGroup(
        userGroupId: String,
        userChannelId: String,
        channelId: String
    ): Result<Unit> {
        return try {
            // Step 1: Delete the UserChannel entry
            val channelResponse = apolloClient
                .mutation(DeleteUserChannelMutation(
                    input = DeleteUserChannelInput(id = userChannelId)
                ))
                .execute()

            if (channelResponse.hasErrors()) {
                val errors = channelResponse.errors?.joinToString { it.message }
                println("GroupRepository: leaveGroup - channel deletion error - $errors")
                // Continue even if channel delete fails
            }

            // Step 2: Delete the UserGroup entry
            val groupResponse = apolloClient
                .mutation(DeleteUserGroupMutation(
                    input = DeleteUserGroupInput(id = userGroupId)
                ))
                .execute()

            if (groupResponse.hasErrors()) {
                val errors = groupResponse.errors?.joinToString { it.message }
                println("GroupRepository: leaveGroup error - $errors")
                Result.failure(Exception(errors ?: "Unknown error leaving group"))
            } else {
                println("GroupRepository: Left group successfully")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            println("GroupRepository: leaveGroup error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun updateGroupName(groupId: String, newName: String): Result<Unit> {
        return try {
            val response = apolloClient
                .mutation(UpdateGroupMutation(
                    input = UpdateGroupInput(
                        id = groupId,
                        name = Optional.present(newName)
                    ),
                    condition = Optional.Absent
                ))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("GroupRepository: updateGroupName error - $errors")
                Result.failure(Exception(errors ?: "Failed to update group name"))
            } else {
                println("GroupRepository: Group name updated successfully")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            println("GroupRepository: updateGroupName error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun addGroupMember(groupId: String, userId: String): Result<String> {
        return try {
            val response = apolloClient
                .mutation(CreateUserGroupMutation(
                    input = CreateUserGroupInput(
                        groupId = groupId,
                        userId = userId,
                        role = GroupMemberRole.MEMBER
                    ),
                    condition = Optional.Absent
                ))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("GroupRepository: addGroupMember error - $errors")
                Result.failure(Exception(errors ?: "Failed to add member"))
            } else {
                val userGroupId = response.data?.createUserGroup?.id
                if (userGroupId != null) {
                    println("GroupRepository: Member added successfully")
                    Result.success(userGroupId)
                } else {
                    Result.failure(Exception("No UserGroup ID returned"))
                }
            }
        } catch (e: Exception) {
            println("GroupRepository: addGroupMember error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun removeGroupMember(userGroupId: String): Result<Unit> {
        return try {
            val response = apolloClient
                .mutation(DeleteUserGroupMutation(
                    input = DeleteUserGroupInput(id = userGroupId)
                ))
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("GroupRepository: removeGroupMember error - $errors")
                Result.failure(Exception(errors ?: "Failed to remove member"))
            } else {
                println("GroupRepository: Member removed successfully")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            println("GroupRepository: removeGroupMember error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getGroupMembers(groupId: String): Result<List<ListUserGroupsQuery.Item>> {
        return try {
            val filter = Optional.present(
                ModelUserGroupFilterInput(
                    groupId = Optional.present(
                        ModelIDInput(eq = Optional.present(groupId))
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
                println("GroupRepository: getGroupMembers error - $errors")
                Result.failure(Exception(errors ?: "Failed to fetch members"))
            } else {
                val items = response.data?.listUserGroups?.items?.filterNotNull() ?: emptyList()
                println("GroupRepository: Fetched ${items.size} members for group $groupId")
                Result.success(items)
            }
        } catch (e: Exception) {
            println("GroupRepository: getGroupMembers error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun cleanupUnknownData(): Result<String> {
        // TODO: Uncomment after backend deployment and GraphQL regeneration
        return Result.failure(Exception("Cleanup function not yet deployed. Deploy backend with 'npx ampx sandbox' first."))

        /*
        return try {
            println("GroupRepository: Starting cleanup of unknown data...")

            val response = apolloClient
                .mutation(CleanupUnknownDataMutation())
                .execute()

            if (response.hasErrors()) {
                val errors = response.errors?.joinToString { it.message }
                println("GroupRepository: cleanupUnknownData error - $errors")
                Result.failure(Exception(errors ?: "Failed to cleanup data"))
            } else {
                val result = response.data?.cleanupUnknownData ?: "Cleanup completed"
                println("GroupRepository: Cleanup completed - $result")
                Result.success(result)
            }
        } catch (e: Exception) {
            println("GroupRepository: cleanupUnknownData error - ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
        */
    }
}
