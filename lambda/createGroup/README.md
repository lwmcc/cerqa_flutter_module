# Create Group Lambda Function

This Lambda function creates a group with members and sets up the necessary chat channel in AppSync.

## Function Overview

**Input:**
```json
{
  "groupName": "My Awesome Group",
  "memberUserIds": ["user1", "user2", "user3"],
  "creatorUserId": "creator123"
}
```

**Output:**
```json
{
  "statusCode": 200,
  "body": {
    "success": true,
    "groupId": "group_abc123",
    "channelId": "channel_xyz789",
    "memberCount": 4,
    "message": "Group 'My Awesome Group' created successfully with 4 members"
  }
}
```

## What the Function Does

1. **Validates Input**: Ensures group name is at least 3 characters and has members
2. **Creates Group**: Creates a Group record in DynamoDB via AppSync
3. **Creates UserGroup Links**: Links each member (including creator) to the group
4. **Creates Channel**: Creates a chat channel for the group
5. **Creates UserChannel Links**: Links each member to the channel

## Deployment Instructions

### Option 1: AWS Console

1. **Create the Lambda Function:**
   - Go to AWS Lambda Console
   - Click "Create function"
   - Choose "Author from scratch"
   - Function name: `createGroup`
   - Runtime: Node.js 18.x or later
   - Click "Create function"

2. **Upload the Code:**
   - Copy the contents of `index.js`
   - Paste into the Lambda code editor
   - Click "Deploy"

3. **Configure IAM Role:**
   - Go to Configuration > Permissions
   - Edit the execution role
   - Attach policy: `AWSAppSyncInvokeFullAccess` (or create custom policy below)

4. **Set Timeout:**
   - Go to Configuration > General configuration
   - Set Timeout to 30 seconds (default 3s may be too short)

### Option 2: AWS CLI

```bash
# Navigate to the lambda directory
cd /Users/larrymccarty/AndroidStudioProjects/carclub/lambda/createGroup

# Create a deployment package
zip -r function.zip index.js package.json

# Create the Lambda function (replace ROLE_ARN with your execution role ARN)
aws lambda create-function \
  --function-name createGroup \
  --runtime nodejs18.x \
  --role <YOUR_EXECUTION_ROLE_ARN> \
  --handler index.handler \
  --zip-file fileb://function.zip \
  --timeout 30 \
  --region us-east-2

# Or update existing function
aws lambda update-function-code \
  --function-name createGroup \
  --zip-file fileb://function.zip \
  --region us-east-2
```

### Required IAM Policy

The Lambda execution role needs this policy to call AppSync:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "appsync:GraphQL"
      ],
      "Resource": [
        "arn:aws:appsync:us-east-2:*:apis/qtjqzunv4rgbtkphmdz2f2ybrq/*"
      ]
    },
    {
      "Effect": "Allow",
      "Action": [
        "logs:CreateLogGroup",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "arn:aws:logs:*:*:*"
    }
  ]
}
```

## Connect to AppSync

### Option 1: Direct Invocation (for testing)

Test the Lambda function directly from AWS Console or CLI:

```bash
aws lambda invoke \
  --function-name createGroup \
  --payload '{"groupName":"Test Group","memberUserIds":["user1","user2"],"creatorUserId":"creator1"}' \
  --region us-east-2 \
  response.json

cat response.json
```

### Option 2: AppSync Resolver (recommended)

1. **Add Custom Query/Mutation to Schema:**

Add this to your AppSync schema:

```graphql
type Mutation {
  # ... existing mutations ...
  createGroupWithMembers(groupName: String!, memberUserIds: [String!]!, creatorUserId: String!): CreateGroupResponse @aws_lambda
}

type CreateGroupResponse {
  success: Boolean!
  groupId: String
  channelId: String
  memberCount: Int
  message: String
  error: String
}
```

2. **Create Lambda Data Source:**
   - Go to AppSync Console > Your API > Data Sources
   - Click "Create data source"
   - Data source name: `CreateGroupLambda`
   - Data source type: AWS Lambda function
   - Region: us-east-2
   - Function ARN: (your Lambda ARN)
   - Create new role or use existing

3. **Attach Resolver:**
   - Go to Schema
   - Find `createGroupWithMembers` mutation
   - Click "Attach" resolver
   - Data source: `CreateGroupLambda`
   - Request mapping template: (use default or custom)
   - Response mapping template: (use default or custom)

## Update Kotlin Repository

Update `GroupRepositoryImpl.kt` to use the new mutation:

```kotlin
// Add mutation to mutations.graphql
mutation CreateGroupWithMembers($groupName: String!, $memberUserIds: [String!]!, $creatorUserId: String!) {
  createGroupWithMembers(
    groupName: $groupName
    memberUserIds: $memberUserIds
    creatorUserId: $creatorUserId
  ) {
    success
    groupId
    channelId
    memberCount
    message
    error
  }
}

// Update repository implementation
override suspend fun createGroup(groupName: String, memberUserIds: List<String>): Result<String> {
    return try {
        val creatorUserId = authTokenProvider.getCurrentUserId() ?: return Result.failure(Exception("No authenticated user"))

        val response = apolloClient
            .mutation(CreateGroupWithMembersMutation(
                groupName = groupName,
                memberUserIds = memberUserIds,
                creatorUserId = creatorUserId
            ))
            .execute()

        if (response.hasErrors()) {
            val errors = response.errors?.joinToString { it.message }
            Result.failure(Exception(errors ?: "Unknown error"))
        } else {
            val result = response.data?.createGroupWithMembers
            if (result?.success == true) {
                Result.success(result.groupId ?: "")
            } else {
                Result.failure(Exception(result?.error ?: "Failed to create group"))
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

## Testing

Test the function with this payload:

```json
{
  "groupName": "Test Group",
  "memberUserIds": ["user_abc123", "user_def456", "user_ghi789"],
  "creatorUserId": "user_creator"
}
```

Expected response:

```json
{
  "statusCode": 200,
  "body": "{\"success\":true,\"groupId\":\"group_...\",\"channelId\":\"channel_...\",\"memberCount\":4,\"message\":\"Group 'Test Group' created successfully with 4 members\"}"
}
```

## Monitoring

View Lambda logs in CloudWatch:

```bash
aws logs tail /aws/lambda/createGroup --follow --region us-east-2
```

## Troubleshooting

### Common Issues:

1. **Timeout Error**: Increase Lambda timeout to 30 seconds
2. **Permission Denied**: Check IAM role has AppSync permissions
3. **GraphQL Errors**: Check AppSync API key and endpoint URL are correct
4. **Duplicate IDs**: Function uses UUID to ensure unique IDs

### Debug Mode:

All GraphQL operations are logged. Check CloudWatch logs for detailed information about each step.
