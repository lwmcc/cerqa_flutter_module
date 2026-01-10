package com.cerqa.repository

import com.cerqa.graphql.CheckGroupNameExistsQuery
import com.cerqa.graphql.ListUserGroupsQuery

/**
 * Repository interface for Group operations
 */
interface GroupRepository {
    /**
     * Check if a group name already exists
     * @param groupName The name to check
     * @return Result with true if name exists, false if available
     */
    suspend fun checkGroupNameExists(groupName: String): Result<Boolean>

    /**
     * Create a new group
     * @param groupName The name of the group
     * @param memberUserIds List of user IDs to add as members
     * @param creatorUserId The ID of the user creating the group
     * @return Result with the created group ID
     */
    suspend fun createGroup(groupName: String, memberUserIds: List<String>, creatorUserId: String): Result<String>

    /**
     * Get all groups for a user
     * @param userId The user ID
     * @return Result with list of user's groups
     */
    suspend fun getUserGroups(userId: String): Result<List<ListUserGroupsQuery.Item>>

    /**
     * Delete a group (creator only)
     * Deletes the group, all UserGroup entries, channel, and all UserChannel entries
     * @param groupId The DynamoDB ID of the group
     * @param channelId The ID of the associated channel
     * @return Result indicating success or failure
     */
    suspend fun deleteGroup(groupId: String, channelId: String): Result<Unit>

    /**
     * Leave a group (member only)
     * Removes the user from the group by deleting their UserGroup and UserChannel entries
     * @param userGroupId The ID of the UserGroup entry to delete
     * @param userId The user ID (to find UserChannel entry)
     * @param channelId The channel ID (to find UserChannel entry)
     * @return Result indicating success or failure
     */
    suspend fun leaveGroup(userGroupId: String, userId: String, channelId: String): Result<Unit>
}
