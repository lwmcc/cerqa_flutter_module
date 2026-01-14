# Group Member Roles - Deployment Guide

This guide covers deploying the group member roles feature, which adds CREATOR, MODERATOR, and MEMBER roles to group memberships.

## What Changed

### 1. GraphQL Schema Updates ✅
- Added `GroupMemberRole` enum with values: `CREATOR`, `MODERATOR`, `MEMBER`
- Updated `UserGroup` type to include `role: GroupMemberRole!` field
- Updated all related input types and filters

### 2. Lambda Function Updates ✅
- Modified `createUserGroup()` to accept and store member roles
- Updated group creation to assign `CREATOR` role to creator
- Updated group creation to assign `MEMBER` role to other members

### 3. GraphQL Query Updates ✅
- Updated `ListUserGroups` query to include the `role` field

## Deployment Steps

### Step 1: Update the GraphQL Schema in AWS AppSync

1. Go to AWS AppSync Console: https://console.aws.amazon.com/appsync/
2. Select your API: `wtl5wlqxxvb6lp2nenxcjpvpwq`
3. Navigate to "Schema" in the left sidebar
4. Add the `GroupMemberRole` enum to your schema:

```graphql
enum GroupMemberRole {
  CREATOR
  MODERATOR
  MEMBER
}
```

5. Update the `UserGroup` type to include the role field:

```graphql
type UserGroup @aws_api_key @aws_iam {
  createdAt: AWSDateTime!
  group: Group
  groupId: ID!
  id: ID!
  role: GroupMemberRole!  # <- ADD THIS LINE
  updatedAt: AWSDateTime!
  user: User
  userId: ID!
}
```

6. Update the `CreateUserGroupInput`:

```graphql
input CreateUserGroupInput {
  groupId: ID!
  id: ID
  role: GroupMemberRole!  # <- ADD THIS LINE
  userId: ID!
}
```

7. Update the `UpdateUserGroupInput`:

```graphql
input UpdateUserGroupInput {
  groupId: ID
  id: ID!
  role: GroupMemberRole  # <- ADD THIS LINE
  userId: ID
}
```

8. Update filter/condition inputs to include `role: ModelStringInput`
9. Click "Save Schema"

### Step 2: Update DynamoDB Table Schema

The `role` field will be automatically created when the Lambda function writes new UserGroup entries. No manual DynamoDB schema changes needed.

### Step 3: Deploy the Updated Lambda Function

```bash
cd lambda/createGroup
./deploy.sh
```

Or manually:

```bash
# Zip the Lambda function
zip -r function.zip index.js node_modules/

# Upload to AWS Lambda
aws lambda update-function-code \
  --function-name createGroupWithMembers \
  --zip-file fileb://function.zip \
  --region us-east-2
```

### Step 4: Migrate Existing Groups (IMPORTANT!)

Run the migration script to add roles to existing UserGroup entries:

```bash
cd lambda/createGroup
node add-roles-to-existing-groups.js
```

This script will:
- ✅ Find all existing UserGroup entries
- ✅ Identify the group creator from Channel table
- ✅ Assign `CREATOR` role to creators
- ✅ Assign `MEMBER` role to all other members
- ✅ Skip entries that already have a role

**Output Example:**
```
=========================================
Adding Roles to Existing UserGroup Entries
=========================================

Step 1: Fetching all Groups...
Found 5 Groups

Step 2: Fetching all Channels to find group creators...
Found 5 group Channels

Step 3: Fetching all UserGroup entries...
Found 15 UserGroup entries

Step 4: Adding roles to UserGroup entries...

📝 UPDATING: UserGroup abc123
   Group: My Test Group
   User: user123...
   Assigning role: CREATOR
   ✅ UPDATED!

...

=========================================
SUMMARY
=========================================
Total UserGroups:      15
Updated with role:     15
Already had role:      0
Errors:                0
=========================================

✅ Success! Roles have been added to existing group memberships.
```

### Step 5: Deploy the Mobile App

The mobile app already includes the updated GraphQL queries and will automatically use the role field once deployed.

For Android:
```bash
./gradlew assembleRelease
```

For iOS:
```bash
cd cerqaiOS
xcodebuild -scheme cerqaiOS -configuration Release
```

## Verification

### 1. Test New Group Creation

Create a new group in the app and verify:
- Creator has `role: "CREATOR"` in DynamoDB UserGroup table
- Members have `role: "MEMBER"` in DynamoDB UserGroup table

### 2. Test Existing Groups

Check that existing groups now display correctly with roles:
- Open a group you created → should show you as CREATOR
- Open a group you're a member of → should show you as MEMBER

### 3. Query in AppSync Console

Test the query in AppSync:

```graphql
query TestUserGroups {
  listUserGroups(filter: { userId: { eq: "YOUR_USER_ID" } }) {
    items {
      id
      userId
      role
      group {
        name
      }
    }
  }
}
```

Expected output:
```json
{
  "data": {
    "listUserGroups": {
      "items": [
        {
          "id": "...",
          "userId": "...",
          "role": "CREATOR",
          "group": {
            "name": "My Group"
          }
        },
        {
          "id": "...",
          "userId": "...",
          "role": "MEMBER",
          "group": {
            "name": "Another Group"
          }
        }
      ]
    }
  }
}
```

## Rollback Plan

If issues occur:

1. **Schema Rollback**: Remove `role` field from GraphQL schema (make it optional first)
2. **Lambda Rollback**: Deploy previous Lambda version
3. **Data Rollback**: Not needed - having `role` field doesn't break existing functionality

## Future Enhancements

With roles in place, you can now:

- ✅ Show creator badge in group UI
- ✅ Restrict group editing to creator only
- ✅ Implement "promote to moderator" feature
- ✅ Show different permissions based on role
- ✅ Filter groups by role (e.g., "Groups I created")

## Troubleshooting

### "role is required" error when creating groups
- Ensure Lambda function is updated and deployed
- Check CloudWatch logs for the Lambda function

### Existing groups don't show roles
- Run the migration script: `node add-roles-to-existing-groups.js`
- Check DynamoDB UserGroup table for `role` field

### Mobile app doesn't show roles
- Ensure you rebuilt the app after GraphQL schema changes
- Clear app cache and rebuild
- Check that Apollo client cache is cleared

## Support

For issues or questions, contact the development team or check the project documentation.