# Group Member Role - UI Implementation Summary

## ✅ Implementation Complete!

The `role` field is now fully integrated into the UI and available for conditional logic.

## Data Flow

```
GraphQL Query (ListUserGroups)
    ↓ (includes role: GroupMemberRole!)
ChatViewModel.getUserGroups()
    ↓ (ListUserGroupsQuery.Item with role)
ChatListItem.GroupChat
    ↓ (passes role to UI)
GroupChatListItem
    ↓ (extracts role on long click)
LongClickedChat.GroupChat
    ↓ (stores role)
GroupChatBottomSheet
    ↓ (conditional display based on role)
UI shows different options for CREATOR vs MEMBER
```

## Role Type

- **Type**: `com.cerqa.graphql.type.GroupMemberRole` (GraphQL enum)
- **Values**:
  - `GroupMemberRole.CREATOR` - Group creator
  - `GroupMemberRole.MODERATOR` - Group moderator (future use)
  - `GroupMemberRole.MEMBER` - Regular member

## UI Behavior

### When CREATOR Long-Clicks a Group:
```
✅ Edit Group
✅ Archive
✅ Delete Group (red text)
✅ Cancel
```

### When MEMBER Long-Clicks a Group:
```
✅ Archive
✅ Leave Group (red text)
✅ Cancel
```

## Code Locations

### 1. Data Model (`Chat.kt:50`)
```kotlin
sealed class LongClickedChat {
    data class DirectChat(val channelId: String, val userName: String) : LongClickedChat()
    data class GroupChat(
        val groupId: String,
        val groupName: String,
        val role: com.cerqa.graphql.type.GroupMemberRole?
    ) : LongClickedChat()
}
```

### 2. GroupChatListItem (`Chat.kt:805`)
```kotlin
private fun GroupChatListItem(
    chatItem: ChatListItem.GroupChat,
    onClick: () -> Unit,
    onLongClick: (groupId: String, groupName: String, role: GroupMemberRole?) -> Unit
) {
    val userGroup = chatItem.group
    val role = userGroup.role  // ← Role is extracted here
    // ...
    onLongClick = { onLongClick(groupId, groupName, role) }
}
```

### 3. GroupChatBottomSheet (`Chat.kt:597`)
```kotlin
fun GroupChatBottomSheet(
    groupName: String,
    role: com.cerqa.graphql.type.GroupMemberRole?,  // ← Role is passed here
    editGroup: () -> Unit,
    onDeleteGroup: () -> Unit,
    onLeaveGroup: () -> Unit
) {
    val isCreator = role == com.cerqa.graphql.type.GroupMemberRole.CREATOR

    // Show "Edit Group" only for creators
    if (isCreator) { /* ... */ }

    // Show "Delete Group" only for creators
    if (isCreator) { /* ... */ }

    // Show "Leave Group" only for members
    if (!isCreator) { /* ... */ }
}
```

## Usage Examples

### Check if user is creator:
```kotlin
val isCreator = userGroup.role == com.cerqa.graphql.type.GroupMemberRole.CREATOR
```

### Check if user is moderator:
```kotlin
val isModerator = userGroup.role == com.cerqa.graphql.type.GroupMemberRole.MODERATOR
```

### Check if user has admin privileges (creator or moderator):
```kotlin
val isAdmin = userGroup.role in listOf(
    com.cerqa.graphql.type.GroupMemberRole.CREATOR,
    com.cerqa.graphql.type.GroupMemberRole.MODERATOR
)
```

## Next Steps - TODO Items

The following TODOs are marked in the code for you to implement:

### 1. Delete Group (Creator Only)
**Location**: `Chat.kt:95`
```kotlin
onDeleteGroup = {
    // TODO: Implement delete group
    // - Call groupRepository.deleteGroup(groupId)
    // - Remove all UserGroup entries
    // - Delete associated Channel
    // - Refresh UI
    longClickedChat = null
}
```

### 2. Leave Group (Member Only)
**Location**: `Chat.kt:100`
```kotlin
onLeaveGroup = {
    // TODO: Implement leave group
    // - Find UserGroup entry for current user
    // - Delete UserGroup entry
    // - Delete UserChannel entry
    // - Refresh UI
    longClickedChat = null
}
```

## Testing Checklist

- [ ] Build succeeds ✅ (Already done!)
- [ ] Deploy updated Lambda function (see DEPLOYMENT_GUIDE.md)
- [ ] Run migration script for existing groups
- [ ] Test creator long-click → shows "Edit Group" and "Delete Group"
- [ ] Test member long-click → shows "Leave Group" only
- [ ] Implement delete group functionality
- [ ] Implement leave group functionality
- [ ] Test on both Android and iOS

## Related Files

- **GraphQL Schema**: `shared/src/commonMain/graphql/schema.graphqls`
- **GraphQL Queries**: `shared/src/commonMain/graphql/queries.graphql`
- **GraphQL Mutations**: `shared/src/commonMain/graphql/mutations.graphql`
- **Lambda Function**: `lambda/createGroup/index.js`
- **Migration Script**: `lambda/createGroup/add-roles-to-existing-groups.js`
- **Deployment Guide**: `lambda/createGroup/DEPLOYMENT_GUIDE.md`
- **UI Implementation**: `shared/src/commonMain/kotlin/com/cerqa/ui/screens/Chat.kt`
- **ViewModel**: `shared/src/commonMain/kotlin/com/cerqa/viewmodels/ChatViewModel.kt`
- **Repository**: `shared/src/commonMain/kotlin/com/cerqa/repository/GroupRepositoryImpl.kt`

## Support

For questions or issues, refer to the DEPLOYMENT_GUIDE.md or contact the development team.
